package com.github.grzegorzekkk.serverstatusnotifier.scheduler

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build

class StartUpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val mJobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val builder = JobInfo.Builder(1, ComponentName(context.packageName, ServerAvailabilityChecker::class.java.name))

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(5 * 1000)
        } else {
            builder.setPeriodic(5000)
        }

        mJobScheduler.schedule(builder.build())
    }
}