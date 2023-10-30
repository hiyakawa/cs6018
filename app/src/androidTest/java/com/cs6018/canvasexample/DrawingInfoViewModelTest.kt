package com.cs6018.canvasexample

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cs6018.canvasexample.data.DrawingInfo
import com.cs6018.canvasexample.data.DrawingInfoDAO
import com.cs6018.canvasexample.data.DrawingInfoDatabase
import com.cs6018.canvasexample.model.DrawingInfoViewModel
import com.cs6018.canvasexample.model.deleteImageFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.security.SecureRandom
import java.util.Date
import java.util.Random
import java.util.concurrent.TimeUnit
import com.cs6018.canvasexample.data.DrawingInfoRepository as Repository

@RunWith(AndroidJUnit4::class)
class DrawingInfoViewModelTest {
    private lateinit var dao: DrawingInfoDAO
    private lateinit var db: DrawingInfoDatabase
    private lateinit var repository: Repository
    private lateinit var scope: CoroutineScope
    private lateinit var viewModel: DrawingInfoViewModel

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, DrawingInfoDatabase::class.java
        ).build()
        dao = db.drawingInfoDao()
        scope = CoroutineScope(Dispatchers.IO)
        repository = Repository(scope, dao)
        viewModel = DrawingInfoViewModel(repository)
    }

    @Test
    fun testAddDrawingInfo() {
        val drawingInfoList = generateRandomTestDrawingInfoList(10)
        for (drawingInfo in drawingInfoList) {
            viewModel.addDrawingInfo(drawingInfo.drawingTitle, null, null)
        }

        runBlocking {
            val lifecycleOwner = TestLifecycleOwner()

            lifecycleOwner.run {
                withContext(Dispatchers.Main) {
                    val allDrawing = dao.allDrawingInfo().asLiveData()
                    allDrawing.observe(lifecycleOwner, object : Observer<List<DrawingInfo>> {
                        override fun onChanged(value: List<DrawingInfo>) {
                            assert(value.containsAll(drawingInfoList))
                            allDrawing.removeObserver(this)
                        }
                    })
                }
            }
        }
    }

    @Test
    fun testActiveCapturedImage() {
        val bitmap = generateRandomBitmap(100, 100)

        scope.launch {
            viewModel.setActiveCapturedImage(bitmap)
            val fetchedViewModel = viewModel.getActiveCapturedImage().value
            assert(fetchedViewModel == bitmap)
        }
    }

    @Test
    fun testUpdateThumbnail() {
        val random = SecureRandom()
        val thumbnail = ByteArray(100)
        random.nextBytes(thumbnail)

        scope.launch {
            viewModel.setActiveDrawingInfoById(1)
            viewModel.updateThumbnailForActiveDrawingInfo(thumbnail)
            val fetchedDrawingInfo = db.drawingInfoDao().fetchDrawingInfoWithId(1).firstOrNull()
            assert(fetchedDrawingInfo?.thumbnail?.contentEquals(thumbnail) ?: false)
        }
    }

    @Test
    fun testAddDrawingInfoWithRecentCapturedImage() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val bitmap = generateRandomBitmap(100, 100)

        scope.launch {
            viewModel.setActiveCapturedImage(bitmap)
            viewModel.setActiveDrawingInfoById(1)
            val imagePath = viewModel.addDrawingInfoWithRecentCapturedImage(context, "Untitled")
            assert(imagePath != null)
            println("Image path: $imagePath")

            deleteImageFile(imagePath, context)
            assert(!fileExists(imagePath))
        }
    }

    private fun fileExists(filePath: String?): Boolean {
        if (filePath != null) {
            val imageFile = File(Uri.parse(filePath).path ?: "")
            return imageFile.exists()
        }
        return false
    }

    @Test
    fun testDeleteDrawingInfo() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        scope.launch {
            val drawingInfoList = viewModel.allDrawingInfo.value
            if (drawingInfoList != null) {
                for (drawingInfo in drawingInfoList) {
                    viewModel.deleteDrawingInfoWithId(drawingInfo, context)
                    assert(viewModel.allDrawingInfo.value?.contains(drawingInfo) == false)
                }
            } else {
                assert(false)
            }
        }
    }
}

fun generateRandomBitmap(width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    val random = Random()

    val red = random.nextInt(256)
    val green = random.nextInt(256)
    val blue = random.nextInt(256)
    val color = Color.rgb(red, green, blue)
    canvas.drawColor(color)

    val circleRadius = width / 4
    val circleX = random.nextInt(width)
    val circleY = random.nextInt(height)
    paint.color = Color.WHITE
    canvas.drawCircle(circleX.toFloat(), circleY.toFloat(), circleRadius.toFloat(), paint)

    return bitmap
}

fun generateRandomTestDrawingInfoList(n: Int): List<DrawingInfo> {
    val drawingInfoList = mutableListOf<DrawingInfo>()
    val random = Random()

    for (i in 1..n) {
        val createdTimeMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(
            random.nextInt(365)
                .toLong()
        )

        val lastModifiedTimeMillis =
            createdTimeMillis + TimeUnit.DAYS.toMillis(random.nextInt(365).toLong())
        val lastModifiedDate = Date(lastModifiedTimeMillis)
        val createdDate = Date(createdTimeMillis)

        val drawingInfo = DrawingInfo(lastModifiedDate, createdDate, "Drawing $i", null, null)
        drawingInfo.id = i
        drawingInfoList.add(drawingInfo)
    }

    return drawingInfoList
}
