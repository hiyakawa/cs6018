package com.cs6018.canvasexample.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel

class CapturableImageViewModel : ViewModel() {
    private val _signalChannel = MutableLiveData<Channel<Unit>>(Channel())

    val signalChannel: MutableLiveData<Channel<Unit>>
        get() = _signalChannel

    fun setNewSignalChannel(channel: Channel<Unit> = Channel()) {
        _signalChannel.value = channel
    }

    fun fireSignal() {
        _signalChannel.value?.trySend(Unit)?.isSuccess
    }
}
