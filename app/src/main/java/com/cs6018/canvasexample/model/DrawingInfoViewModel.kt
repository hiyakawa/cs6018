package com.cs6018.canvasexample.model

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs6018.canvasexample.data.DrawingInfo
import com.cs6018.canvasexample.data.DrawingInfoRepository
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DrawingInfoViewModel(private val repository: DrawingInfoRepository) : ViewModel() {
    var activeDrawingInfo: LiveData<DrawingInfo?> = repository.activeDrawingInfo
    private val activeCapturedImage: LiveData<Bitmap?> = MutableLiveData(null)
    val allDrawingInfo: LiveData<List<DrawingInfo>> = repository.allDrawingInfo

    fun getActiveCapturedImage(): LiveData<Bitmap?> {
        return activeCapturedImage
    }

    suspend fun setActiveDrawingInfoById(id: Int?) {
        repository.setActiveDrawingInfoById(id ?: 0)
    }

    fun addDrawingInfo(
        title: String,
        imageUrl: String?, thumbnail: ByteArray?) {
        val drawingInfo = DrawingInfo(Date(), Date(),
            title,
            imageUrl, thumbnail)
        repository.addNewDrawingInfo(drawingInfo)
    }

    private fun updateDrawingInfoTitle(title:String) {
        repository.updateDrawingInfoTitle(title, activeDrawingInfo.value?.id ?: 0)
    }

    fun setActiveCapturedImage(imageBitmap: Bitmap?) {
        (activeCapturedImage as MutableLiveData).value = imageBitmap
    }

    fun addDrawingInfoWithRecentCapturedImage(context: Context,
                                              drawingTitle: String): String? {
        val bitmap = activeCapturedImage.value ?: return null
        if (activeDrawingInfo.value == null) {
            val imagePath = saveImage(bitmap, context) ?: return null
            val thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 256, 256)
            addDrawingInfo(
                drawingTitle,
                imagePath, bitmapToByteArray(thumbnail)
            )
            return imagePath
        } else {
            val imagePath =
                overwriteCurrentImageFile(bitmap, context, activeDrawingInfo.value?.imagePath ?: "")
                    ?: return null
            if (drawingTitle != activeDrawingInfo.value?.drawingTitle) {
                updateDrawingInfoTitle(drawingTitle)
            }

            val thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 256, 256)
            updateThumbnailForActiveDrawingInfo(bitmapToByteArray(thumbnail))

            return imagePath
        }
    }

    private fun overwriteCurrentImageFile(bitmap: Bitmap, context: Context, filePath: String): String? {
        try {
            val imageUri = Uri.parse(filePath)
            val imageFile = imageUri.path?.let { File(it) }
            val outputStream = FileOutputStream(imageFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            return imageUri.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun saveImage(bitmap: Bitmap, context: Context): String? {
        try {
            val curTime = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "${curTime}.jpg"
            val imageFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                imageFileName
            )

            val outputStream = FileOutputStream(imageFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            val imageUri = imageFile.toUri().toString()

            return imageUri
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun updateThumbnailForActiveDrawingInfo(thumbnail: ByteArray) {
        repository.updateDrawingInfoThumbnail(thumbnail, activeDrawingInfo.value?.id ?: 0)
    }

    suspend fun deleteDrawingInfoWithId(drawingInfo: DrawingInfo, context: Context) {
        deleteImageFile(drawingInfo.imagePath, context)
        repository.deleteDrawingInfoWithId(drawingInfo.id)
    }
}

fun deleteImageFile(imagePath: String?, context: Context): Boolean {
    if (imagePath != null) {
        try {
            val imageFile = File(Uri.parse(imagePath).path ?: "")
            if (imageFile.exists()) {
                val deleted = imageFile.delete()
                if (deleted) {
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return false
}

fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(
        Bitmap.CompressFormat.PNG,
        100,
        outputStream
    )
    return outputStream.toByteArray()
}