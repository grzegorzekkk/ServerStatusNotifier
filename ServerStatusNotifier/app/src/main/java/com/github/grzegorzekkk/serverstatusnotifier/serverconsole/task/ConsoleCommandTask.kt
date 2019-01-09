package com.github.grzegorzekkk.serverstatusnotifier.serverconsole.task

import android.os.AsyncTask
import com.github.grzegorzekkk.serverstatusnotifier.client.NotifierClient
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.SrvConnDetails
import java.io.IOException
import java.net.InetAddress

class ConsoleCommandTask(var listener: OnConnectionTimeoutListener, private val connDetails: SrvConnDetails) : AsyncTask<String, Unit, Unit>() {
    private var exception: IOException? = null

    override fun doInBackground(vararg commands: String?) {
        try {
            val client = NotifierClient(InetAddress.getByName(connDetails.address), connDetails.port)
            client.authorize(connDetails.password)
            client.sendConsoleCommand(commands[0]!!)
            client.shutdown()
        } catch (ex: IOException) {
            exception = ex
            cancel(true)
        }
    }

    override fun onCancelled() {
        if (exception != null) {
            listener.onConnectionTimeout()
        }
        super.onCancelled()
    }
}