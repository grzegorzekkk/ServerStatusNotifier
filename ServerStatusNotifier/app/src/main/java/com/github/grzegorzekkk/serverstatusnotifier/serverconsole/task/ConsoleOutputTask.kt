package com.github.grzegorzekkk.serverstatusnotifier.serverconsole.task

import android.os.AsyncTask
import com.github.grzegorzekkk.serverstatusnotifier.client.NotifierClient
import com.github.grzegorzekkk.serverstatusnotifier.serverconsole.model.ConsoleLine
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.SrvConnDetails
import java.io.IOException
import java.net.InetAddress

class ConsoleOutputTask(var listener: OnConsoleLineReceivedListener) : AsyncTask<SrvConnDetails, List<ConsoleLine>, Unit>() {
    private lateinit var client: NotifierClient
    private var exception: IOException? = null

    override fun doInBackground(vararg args: SrvConnDetails?) {
        val connDetails = args[0]!!

        try {
            client = NotifierClient(InetAddress.getByName(connDetails.address), connDetails.port)
            client.authorize(connDetails.password)
            client.openConsoleStream()

            while (!isCancelled) {
                if (client.isDataToReadAvailable()) {
                    val consoleLines = client.fetchConsoleLines()
                    if (consoleLines.isNotEmpty()) {
                        publishProgress(consoleLines)
                    }
                }
            }
        } catch (ex: IOException) {
            exception = ex
            cancel(true)
        }
    }

    override fun onCancelled() {
        client.shutdown()
        if (exception != null) {
            listener.onConnectionTimeout()
        }
        super.onCancelled()
    }

    override fun onProgressUpdate(vararg lines: List<ConsoleLine>) {
        listener.onConsoleLineReceived(lines[0])
    }

    interface OnConsoleLineReceivedListener : OnConnectionTimeoutListener {
        fun onConsoleLineReceived(consoleLines: List<ConsoleLine>)
    }
}