package com.github.grzegorzekkk.serverstatusnotifier.serverslist

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.grzegorzekkk.serverstatusnotifier.R
import com.github.grzegorzekkk.serverstatusnotifier.serverdetails.model.ServerDetails
import kotlinx.android.synthetic.main.server_status.view.*

class ServerStatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val serverName = itemView.serverName
    private val serverStatusText = itemView.status_text
    private val serverStatusImage = itemView.itemImage

    fun bindItem(serverDetails: ServerDetails, onDetailsClicked: (ServerDetails) -> Unit) {
        serverName.text = serverDetails.serverStatus.serverName
        if (serverDetails.serverStatus.isOnline) {
            serverStatusText.text = itemView.resources.getString(R.string.common_online)
            serverStatusImage.setImageBitmap(BitmapFactory.decodeResource(itemView.resources, R.drawable.emerald_block))
        } else {
            serverStatusText.text = itemView.resources.getString(R.string.common_offline)
            serverStatusImage.setImageBitmap(BitmapFactory.decodeResource(itemView.resources, R.drawable.redstone_block))
        }
        itemView.setOnClickListener { onDetailsClicked(serverDetails) }
    }
}