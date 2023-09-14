package com.example.customviewdemo

import android.graphics.Path
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class CustomPath(
    val path: Path,
    var strokeWidth: Float,
    var color: Int
)

class SimpleViewModel : ViewModel() {

    private val _pathData: MutableLiveData<List<CustomPath>> = MutableLiveData()
    val pathData: LiveData<List<CustomPath>> = _pathData

    fun setPath(paths: List<CustomPath>) {
        _pathData.value = paths
    }
}