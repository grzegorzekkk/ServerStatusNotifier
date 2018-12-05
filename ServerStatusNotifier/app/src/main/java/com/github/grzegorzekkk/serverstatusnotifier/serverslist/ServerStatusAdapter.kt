package com.github.grzegorzekkk.serverstatusnotifier.serverslist

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.grzegorzekkk.serverstatusnotifier.R.layout.server_status
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails

open class ServerStatusAdapter(var servers: List<ServerDetails>, private val onItemClicked: (ServerDetails) -> Unit) : RecyclerView.Adapter<ServerStatusViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerStatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(server_status, parent, false)
        return ServerStatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServerStatusViewHolder, position: Int) = holder.bindItem(servers[position], onItemClicked)

    override fun getItemCount(): Int = servers.size
}