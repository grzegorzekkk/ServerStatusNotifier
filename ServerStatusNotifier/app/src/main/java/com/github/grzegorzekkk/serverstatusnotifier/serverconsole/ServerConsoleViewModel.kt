package com.github.grzegorzekkk.serverstatusnotifier.serverconsole

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.grzegorzekkk.serverstatusnotifier.serverconsole.model.ConsoleLine

class ServerConsoleViewModel : ViewModel() {
    private val consoleLineLiveData = MutableLiveData<List<ConsoleLine>>()

    fun consoleLinesList(): LiveData<List<ConsoleLine>> = consoleLineLiveData

    fun addNewLine(newLine: ConsoleLine) {
        val list = consoleLineLiveData.value?.toMutableList() ?: mutableListOf()
        list.add(newLine)
        consoleLineLiveData.value = list
    }
}