package com.github.grzegorzekkk.serverstatusnotifier.serverslist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.grzegorzekkk.serverstatusnotifier.serverdetails.model.ServerDetails
import com.github.grzegorzekkk.serverstatusnotifier.serverslist.task.LoadSavedServersTask

class ServersListViewModel : ViewModel(), LoadSavedServersTask.OnLoadSavedServersListener {
    private val serversLiveData = MutableLiveData<List<ServerDetails>>()

    fun serversList(): LiveData<List<ServerDetails>> = serversLiveData

    fun addServer(server: ServerDetails) {
        val list = serversLiveData.value?.toMutableList()
        list?.add(server)
        serversLiveData.value = list?.toList()
    }

    override fun onLoadSavedServers(serverDetailsList: List<ServerDetails>) {
        serversLiveData.value = serverDetailsList
    }
}