package com.github.grzegorzekkk.serverstatusnotifier.serverdetails

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails

class ServerDetailsViewModel : ViewModel() {
    private val serverDetailsLiveData = MutableLiveData<ServerDetails>()

    fun serverDetails(): LiveData<ServerDetails> = serverDetailsLiveData

    fun load(serverDetails: ServerDetails) {
        serverDetailsLiveData.value = serverDetails
    }
}