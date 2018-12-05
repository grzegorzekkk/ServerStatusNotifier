package com.github.grzegorzekkk.serverstatusnotifier.serverdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails

class ServerDetailsViewModel : ViewModel() {
    private val serverDetailsLiveData = MutableLiveData<ServerDetails>()

    fun serverDetails(): LiveData<ServerDetails> = serverDetailsLiveData

    fun load(serverDetails: ServerDetails) {
        serverDetailsLiveData.value = serverDetails
    }
}