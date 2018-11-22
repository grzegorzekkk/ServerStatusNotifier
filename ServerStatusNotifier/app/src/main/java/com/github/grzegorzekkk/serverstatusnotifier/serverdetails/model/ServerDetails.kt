package com.github.grzegorzekkk.serverstatusnotifier.serverdetails.model

import com.github.grzegorzekkk.serverstatusnotifier.database.SrvConnDetails
import com.github.grzegorzekkk.serverstatusnotifier.serverslist.model.ServerStatus

data class ServerDetails(val serverStatus: ServerStatus, var srvConnDetails: SrvConnDetails, val playersCount: Int)
