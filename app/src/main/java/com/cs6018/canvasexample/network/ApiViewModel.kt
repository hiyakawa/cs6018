package com.cs6018.canvasexample.network

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {
    val currentUserDrawingHistory: LiveData<List<DrawingResponse>> =
        repository.currentUserDrawingHistory
    val currentUserExploreFeed: LiveData<List<DrawingResponse>> =
        repository.currentUserExploreFeed
    var activeDrawingInfo: LiveData<DrawingResponse?> = repository.activeDrawingInfo
    var activeDrawingTitle: LiveData<String?> = repository.activeDrawingTitle
    private val activeCapturedImage: LiveData<Bitmap?> = MutableLiveData(null)
    var activeDrawingBackgroundImageReference: LiveData<String?> =
        repository.activeDrawingBackgroundImageReference

    fun setActiveDrawingInfoTitle(title: String) {
        repository.setActiveDrawingInfoTitle(title)
    }

    fun getActiveCapturedImage(): LiveData<Bitmap?> {
        return activeCapturedImage
    }

    fun setActiveCapturedImage(imageBitmap: Bitmap?) {
        (activeCapturedImage as MutableLiveData).value = imageBitmap
    }

    private suspend fun updateDrawingTitleById(thumbnail: String) {
        repository.updateDrawingTitleById(activeDrawingTitle.value ?: "Untitled", thumbnail)
    }

    private suspend fun postNewDrawing(creatorId: String, imagePath: String, thumbnail: String) {
        repository.postNewDrawing(creatorId, imagePath, thumbnail)
    }

    suspend fun addDrawingInfoWithRecentCapturedImage(context: Context): String? {
        val bitmap = activeCapturedImage.value ?: return null

        if (activeDrawingInfo.value == null) {
            val imagePath = saveImage(bitmap, context) ?: return null

            val thumbnail = bitmapToBase64String(
                bitmap
            )

            postNewDrawing(
                Firebase.auth.currentUser?.uid ?: "",
                imagePath,
                thumbnail
            )
            return imagePath
        } else {
            val imagePath =
                overwriteCurrentImageFile(bitmap, context, activeDrawingInfo.value?.imagePath ?: "")
                    ?: return null

            val thumbnail = bitmapToBase64String(bitmap)
            updateDrawingTitleById(thumbnail)
            return imagePath
        }
    }

    fun getCurrentUserDrawingHistory(userId: String) {
        repository.getCurrentUserDrawingHistory(userId)
    }

    suspend fun getCurrentUserExploreFeed(userId: String) {
        repository.getCurrentUserExploreFeed(userId)
    }

    fun setActiveDrawingInfoById(id: Int?) {
        repository.setActiveDrawingInfoById(id ?: 0)
    }

    fun deleteDrawingById(id: Int) {
        repository.deleteDrawingById(id)
    }

    fun setActiveDrawingBackgroundImageReference(imageReference: String?) {
        repository.setActiveDrawingBackgroundImageReference(imageReference)
    }

    fun resetData() {
        repository.resetData()
    }

    fun saveImage(bitmap: Bitmap, context: Context): String? {
        try {
            val imageFileName = "${getCurrentDateTimeString()}.jpg"
            val imageFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                imageFileName
            )

            val outputStream = FileOutputStream(imageFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            return imageFile.toUri().toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun getCurrentDateTimeString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault())
        val currentTime = Date()
        return dateFormat.format(currentTime)
    }

    fun bitmapToBase64String(bitmap: Bitmap): String {
        val thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 150, 150)
        val byteArray = bitmapToByteArray(thumbnail)
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
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

    fun overwriteCurrentImageFile(bitmap: Bitmap, context: Context, filePath: String): String? {
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
}

