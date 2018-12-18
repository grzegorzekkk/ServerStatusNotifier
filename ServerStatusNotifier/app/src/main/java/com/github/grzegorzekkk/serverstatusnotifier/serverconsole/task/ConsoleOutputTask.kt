package com.github.grzegorzekkk.serverstatusnotifier.serverconsole.task

import android.os.AsyncTask
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.SrvConnDetails

class ConsoleOutputTask(var listener: OnConsoleLineReceivedListener) : AsyncTask<SrvConnDetails, String, Unit>() {

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg args: SrvConnDetails?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProgressUpdate(vararg values: String?) {
        //todo tu update'uj ui
    }

    interface OnConsoleLineReceivedListener {
        fun onConsoleLineReceived(consoleLine: String)
    }
}