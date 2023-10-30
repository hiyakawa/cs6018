package com.cs6018.canvasexample.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cs6018.canvasexample.data.DrawingInfoRepository

class DrawingInfoViewModelFactory(private val repository: DrawingInfoRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DrawingInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DrawingInfoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}