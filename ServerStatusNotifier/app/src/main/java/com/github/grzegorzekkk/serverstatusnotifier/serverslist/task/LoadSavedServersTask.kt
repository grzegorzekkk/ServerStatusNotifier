package com.github.grzegorzekkk.serverstatusnotifier.serverslist.task

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import com.github.grzegorzekkk.serverstatusnotifier.ProgressBarHandler
import com.github.grzegorzekkk.serverstatusnotifier.client.NotifierClient
import com.github.grzegorzekkk.serverstatusnotifier.database.SrvConnDetails
import com.github.grzegorzekkk.serverstatusnotifier.serverdetails.model.ServerDetails
import com.github.grzegorzekkk.serverstatusnotifier.serverslist.model.ServerStatus
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.InetAddress

class LoadSavedServersTask(private val context: WeakReference<Context>, private val listener: OnLoadSavedServersListener) : AsyncTask<List<SrvConnDetails>, Unit, List<ServerDetails>>() {
    private lateinit var progressBar: ProgressBarHandler

    override fun onPreExecute() {
        super.onPreExecute()

        if (context.get() is Activity) {
            progressBar = ProgressBarHandler(context.get()!!)
            progressBar.show()
        }
    }

    override fun doInBackground(vararg list: List<SrvConnDetails>?): List<ServerDetails> {
        val serverDetailsList = mutableListOf<ServerDetails>()

        for (connDetails in list[0]!!) {
            try {
                val server = NotifierClient(InetAddress.getByName(connDetails.address), connDetails.port)
                val serverDetails = server.fetchServerDetails(connDetails.password)
                serverDetails?.srvConnDetails = connDetails
                server.shutdown()
                serverDetailsList.add(serverDetails!!)
            } catch (ex: IOException) {
                val serverDetails = ServerDetails(ServerStatus(connDetails.address, false), connDetails, 0)
                serverDetailsList.add(serverDetails)
            }
        }

        return serverDetailsList.toList()
    }

    override fun onPostExecute(result: List<ServerDetails>) {
        super.onPostExecute(result)
        listener.onLoadSavedServers(result)

        if (context.get() is Activity) {
            progressBar.hide()
        }
    }

    interface OnLoadSavedServersListener {
        fun onLoadSavedServers(serverDetailsList: List<ServerDetails>)
    }
}