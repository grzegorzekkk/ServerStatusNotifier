package com.github.grzegorzekkk.serverstatusnotifier.serverslist.task

import android.content.Context
import android.os.AsyncTask
import com.github.grzegorzekkk.serverstatusnotifier.ProgressBarHandler
import com.github.grzegorzekkk.serverstatusnotifier.client.NotifierClient
import com.github.grzegorzekkk.serverstatusnotifier.database.SrvConnDetails
import com.github.grzegorzekkk.serverstatusnotifier.serverdetails.model.ServerDetails
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.InetAddress

class AddNewServerTask(private val context: WeakReference<Context>) : AsyncTask<String, Unit, ServerDetails>() {
    private lateinit var progressBar: ProgressBarHandler
    private lateinit var newServerAddListener: OnNewServerAddListener
    private lateinit var srvConnDetails: SrvConnDetails
    private var hasTimedOut: Boolean = false

    override fun onPreExecute() {
        super.onPreExecute()

        newServerAddListener = context.get() as OnNewServerAddListener
        progressBar = ProgressBarHandler(context.get()!!)
        progressBar.show()
    }

    override fun doInBackground(vararg args: String): ServerDetails? {
        try {
            val address = args[0]
            val port = args[1].toInt()
            val password = args[2]
            srvConnDetails = SrvConnDetails(address, port, password)

            val server = NotifierClient(InetAddress.getByName(address), port)
            val serverDetails = server.fetchServerDetails(password)
            server.shutdown()
            serverDetails?.srvConnDetails = srvConnDetails
            return serverDetails
        } catch (ex: IOException) {
            hasTimedOut = true
            return null
        }
    }

    override fun onPostExecute(result: ServerDetails?) {
        super.onPostExecute(result)
        when {
            hasTimedOut -> newServerAddListener.onTimeout()
            else -> newServerAddListener.onNewServerAdd(result)
        }
        progressBar.hide()
    }

    interface OnNewServerAddListener {
        fun onNewServerAdd(serversDetails: ServerDetails?)
        fun onTimeout()
    }
}