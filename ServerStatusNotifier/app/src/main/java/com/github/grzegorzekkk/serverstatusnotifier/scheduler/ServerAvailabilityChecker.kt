package com.github.grzegorzekkk.serverstatusnotifier.scheduler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.github.grzegorzekkk.serverstatusnotifier.R
import com.github.grzegorzekkk.serverstatusnotifier.ServersActivity
import com.github.grzegorzekkk.serverstatusnotifier.database.SrvConnDetailsDbHelper
import com.github.grzegorzekkk.serverstatusnotifier.serverslist.task.LoadSavedServersTask
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails

class ServerAvailabilityChecker : JobService(), LoadSavedServersTask.OnLoadSavedServersListener {
    private var isInitialized: Boolean = false

    private lateinit var pendingIntent: PendingIntent
    private lateinit var dbHelper: SrvConnDetailsDbHelper
    private var params: JobParameters? = null

    override fun onStartJob(params: JobParameters?): Boolean {
        this.params = params

        if (!isInitialized) {
            createNotificationChannel()
            createNotificationIntent()
            isInitialized = true
            dbHelper = SrvConnDetailsDbHelper(this)
        }

        val dbServersList = dbHelper.fetchServersFromDb()
        val loadTask = LoadSavedServersTask(this)
        loadTask.execute(dbServersList)
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }

    override fun onLoadSavedServers(serverDetailsList: List<ServerDetails>) {
        dbHelper.close()
        val offlineServers = serverDetailsList.filter { !it.isOnline }.toList()
        val offlineCount = offlineServers.size

        if (offlineServers.isNotEmpty()) {
            showOfflineServerNotification(offlineCount)
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            scheduleRefresh()

        jobFinished(params, false)
    }

    private fun showOfflineServerNotification(offlineCount: Int) {
        val notificationTitle = getString(R.string.notification_title)
        val notificationDesc = String.format(getString(R.string.notification_description), offlineCount)

        val notifBuilder = NotificationCompat.Builder(this, "SSN_CH")
                .setSmallIcon(R.drawable.redstone_dust)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDesc)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(notificationDesc))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(0, notifBuilder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notif_channel)
            val descriptionText = getString(R.string.notif_channel)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("SSN_CH", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationIntent() {
        val intent = Intent(this, ServersActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
    }

    private fun scheduleRefresh() {
        val mJobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val builder = JobInfo.Builder(1,
                ComponentName(packageName, this::class.java.name))
                .setMinimumLatency(60 * 1000)
        mJobScheduler.schedule(builder.build())
    }
}