package com.github.grzegorzekkk.serverstatusnotifier

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.github.grzegorzekkk.serverstatusnotifier.R.id.action_about
import com.github.grzegorzekkk.serverstatusnotifier.R.id.action_settings
import com.github.grzegorzekkk.serverstatusnotifier.R.layout.activity_servers
import com.github.grzegorzekkk.serverstatusnotifier.R.menu.menu_servers
import com.github.grzegorzekkk.serverstatusnotifier.database.SrvConnDetailsDbHelper
import com.github.grzegorzekkk.serverstatusnotifier.scheduler.ServerAvailabilityChecker
import com.github.grzegorzekkk.serverstatusnotifier.serverdetails.ServerDetailsActivity
import com.github.grzegorzekkk.serverstatusnotifier.serverslist.ServerStatusAdapter
import com.github.grzegorzekkk.serverstatusnotifier.serverslist.ServersListViewModel
import com.github.grzegorzekkk.serverstatusnotifier.serverslist.dialog.AboutDialog
import com.github.grzegorzekkk.serverstatusnotifier.serverslist.dialog.AddServerDialog
import com.github.grzegorzekkk.serverstatusnotifier.serverslist.task.AddNewServerTask
import com.github.grzegorzekkk.serverstatusnotifier.serverslist.task.LoadSavedServersTask
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails
import kotlinx.android.synthetic.main.activity_servers.*


class ServersActivity : AppCompatActivity(), AddNewServerTask.OnNewServerAddListener {
    private lateinit var serversViewModel: ServersListViewModel
    private lateinit var serverStatusAdapter: ServerStatusAdapter
    private lateinit var mJobScheduler: JobScheduler
    private lateinit var progressBarHandler: ProgressBarHandler
    private val dbHelper = SrvConnDetailsDbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_servers)
        setSupportActionBar(toolbar)
        progressBarHandler = ProgressBarHandler(this)

        addServerButton.setOnClickListener {
            showAddServerDialog()
        }
        refreshButton.setOnClickListener {
            refreshServersList(serversViewModel.serversList().value!!)
        }

        serversViewModel = ViewModelProviders.of(this).get(ServersListViewModel::class.java)
        serversViewModel.serversList().observe(this, Observer(this::updateServersList))

        serverStatusAdapter = ServerStatusAdapter(emptyList(), this::startDetailsActivity)
        serverItemRecyclerView.layoutManager = LinearLayoutManager(this)
        serverItemRecyclerView.adapter = serverStatusAdapter

        val dbServersList = dbHelper.fetchServersFromDb()
        val loadTask = LoadSavedServersTask(serversViewModel)
        loadTask.progressBar = progressBarHandler
        loadTask.execute(dbServersList)

        startBackgroundServerChecker()
    }

    override fun onResume() {
        super.onResume()
        mJobScheduler.cancel(1)
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    override fun onStop() {
        startBackgroundServerChecker()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(menu_servers, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            action_settings -> true
            action_about -> showAboutDialog()
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNewServerAdd(serversDetails: ServerDetails?) {
        if (serversDetails != null) {
            serversViewModel.addServer(serversDetails)
            dbHelper.saveServerConnDetails(serversDetails.connDetails)
        }
    }

    override fun onTimeout() {
        Toast.makeText(this, R.string.conn_server_timeout, Toast.LENGTH_LONG).show()
    }

    private fun updateServersList(serverDetailsList: List<ServerDetails>?) {
        if (serverDetailsList == null) return
        serverStatusAdapter.servers = serverDetailsList
        serverStatusAdapter.notifyDataSetChanged()
    }

    private fun refreshServersList(serversList: List<ServerDetails>) {
        if (serversList.isNotEmpty()) {
            val connDetailsList = serversList.map { it.connDetails }.toList()
            val loadTask = LoadSavedServersTask(serversViewModel)
            loadTask.progressBar = progressBarHandler
            loadTask.execute(connDetailsList)
        }
    }

    private fun startDetailsActivity(server: ServerDetails) {
        val i = Intent(this, ServerDetailsActivity::class.java)
                .putExtra(ServerDetails.SERIAL_NAME, server)
        startActivity(i)
    }

    private fun showAboutDialog(): Boolean {
        val dialog = AboutDialog()
        dialog.show(supportFragmentManager, "dialog")
        return true
    }

    private fun showAddServerDialog() {
        val dialog = AddServerDialog()
        dialog.show(supportFragmentManager, "dialog")
    }

    private fun startBackgroundServerChecker() {
        mJobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val builder = JobInfo.Builder(1, ComponentName(packageName, ServerAvailabilityChecker::class.java.name))

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(5 * 1000)
        } else {
            builder.setPeriodic(5000)
        }

        if (mJobScheduler.schedule(builder.build()) <= 0) {
            //If something goes wrong
        }
    }
}
