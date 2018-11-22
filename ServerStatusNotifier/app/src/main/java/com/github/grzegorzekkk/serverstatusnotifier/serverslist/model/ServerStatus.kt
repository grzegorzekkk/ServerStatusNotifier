package com.github.grzegorzekkk.serverstatusnotifier.serverslist.model

import java.io.Serializable

data class ServerStatus(val serverName: String, val isOnline: Boolean) : Serializable {
    constructor(serverStatus: ServerStatus) : this(serverName = serverStatus.serverName, isOnline = serverStatus.isOnline)
}