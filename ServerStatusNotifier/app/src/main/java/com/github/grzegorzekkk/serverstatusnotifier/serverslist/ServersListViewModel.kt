package com.github.grzegorzekkk.serverstatusnotifier.serverslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.grzegorzekkk.serverstatusnotifier.serverslist.task.LoadSavedServersTask
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails

class ServersListViewModel : ViewModel(), LoadSavedServersTask.OnLoadSavedServersListener {
    private val serversLiveData = MutableLiveData<List<ServerDetails>>()

    fun serversList(): LiveData<List<ServerDetails>> = serversLiveData

    fun addServer(server: ServerDetails) {
        val list = serversLiveData.value?.toMutableList()
        list?.add(server)
        serversLiveData.value = list?.toList()
    }

    fun deleteServer(server: ServerDetails) {
        val list = serversLiveData.value?.toMutableList()
        list?.remove(server)
        serversLiveData.value = list?.toList()
    }

    override fun onLoadSavedServers(serverDetailsList: List<ServerDetails>) {
        serversLiveData.value = serverDetailsList
    }
}