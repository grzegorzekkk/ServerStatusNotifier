package com.github.grzegorzekkk.serverstatusnotifier.serverconsole

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.grzegorzekkk.serverstatusnotifier.R
import com.github.grzegorzekkk.serverstatusnotifier.R.layout.activity_server_console
import com.github.grzegorzekkk.serverstatusnotifier.ServersActivity
import com.github.grzegorzekkk.serverstatusnotifier.serverconsole.model.ConsoleLine
import com.github.grzegorzekkk.serverstatusnotifier.serverconsole.task.ConsoleCommandTask
import com.github.grzegorzekkk.serverstatusnotifier.serverconsole.task.ConsoleOutputTask
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.SrvConnDetails
import kotlinx.android.synthetic.main.activity_server_console.*


class ServerConsoleActivity : AppCompatActivity(), ConsoleOutputTask.OnConsoleLineReceivedListener {
    private lateinit var consoleViewModel: ServerConsoleViewModel
    private lateinit var consoleLineAdapter: ConsoleLineAdapter
    private lateinit var consoleFetcherTask: ConsoleOutputTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_server_console)

        val connDetails = intent.getSerializableExtra(ServerDetails.SERIAL_NAME) as SrvConnDetails

        consoleViewModel = ViewModelProviders.of(this).get(ServerConsoleViewModel::class.java)
        consoleViewModel.consoleLinesList().observe(this, Observer(this::updateConsole))

        consoleLineAdapter = ConsoleLineAdapter(emptyList())
        console_recycler_view.layoutManager = LinearLayoutManager(this)
        console_recycler_view.adapter = consoleLineAdapter

        sendButton.setOnClickListener {
            dispatchCommandFromInput(connDetails)
            try {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            consoleInput.text.clear()
        }

        consoleFetcherTask = ConsoleOutputTask(this)
        consoleFetcherTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, connDetails)
    }

    override fun onDestroy() {
        consoleFetcherTask.cancel(true)
        super.onDestroy()
    }

    override fun onConsoleLineReceived(consoleLines: List<ConsoleLine>) {
        consoleLines.forEach(consoleViewModel::addNewLine)
        console_recycler_view.smoothScrollToPosition(consoleLineAdapter.itemCount)
    }

    override fun onConnectionTimeout() {
        consoleFetcherTask.cancel(true)
        Toast.makeText(this, R.string.conn_server_timeout, Toast.LENGTH_LONG).show()
        val i = Intent(this, ServersActivity::class.java)
        startActivity(i)
    }

    private fun updateConsole(consoleLineList: List<ConsoleLine>?) {
        if (consoleLineList == null) return
        consoleLineAdapter.lines = consoleLineList
        consoleLineAdapter.notifyDataSetChanged()
    }

    private fun dispatchCommandFromInput(connDetails: SrvConnDetails) {
        val command = consoleInput.text.toString()
        if (command.trim().isNotEmpty()) {
            val sendCommandTask = ConsoleCommandTask(this, connDetails)
            sendCommandTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, command)
        }
    }
}