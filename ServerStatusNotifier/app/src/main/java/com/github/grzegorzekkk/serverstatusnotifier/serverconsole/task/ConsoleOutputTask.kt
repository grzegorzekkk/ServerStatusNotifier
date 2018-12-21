package com.github.grzegorzekkk.serverstatusnotifier.serverconsole.task

import android.os.AsyncTask
import com.github.grzegorzekkk.serverstatusnotifier.client.NotifierClient
import com.github.grzegorzekkk.serverstatusnotifier.serverconsole.model.ConsoleLine
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.SrvConnDetails
import java.net.InetAddress

class ConsoleOutputTask(var listener: OnConsoleLineReceivedListener) : AsyncTask<SrvConnDetails, List<ConsoleLine>, Unit>() {
    private lateinit var client: NotifierClient

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg args: SrvConnDetails?) {
        val connDetails = args[0]!!

        client = NotifierClient(InetAddress.getByName(connDetails.address), connDetails.port, true)
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
    }

    override fun onCancelled() {
        client.shutdown()
        super.onCancelled()
    }

    override fun onProgressUpdate(vararg lines: List<ConsoleLine>) {
        listener.onConsoleLineReceived(lines[0])
    }

    interface OnConsoleLineReceivedListener {
        fun onConsoleLineReceived(consoleLines: List<ConsoleLine>)
    }
}