package com.example.customviewdemo

import android.graphics.Path
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class CustomPath(
    val path: Path,
    var strokeWidth: Float
)
class SimpleViewModel :ViewModel() {

    private val _pathData: MutableLiveData<CustomPath> = MutableLiveData()
    val pathData: LiveData<CustomPath> = _pathData

    fun setPath(path: Path, strokeWidth: Float) {
        _pathData.value = CustomPath(path, strokeWidth)
    }

}