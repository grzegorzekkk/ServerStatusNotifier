package com.github.grzegorzekkk.serverstatusnotifier.serverslist

import android.graphics.BitmapFactory
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.github.grzegorzekkk.serverstatusnotifier.R
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails
import kotlinx.android.synthetic.main.server_status.view.*

class ServerStatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val serverName = itemView.serverName
    private val serverStatusText = itemView.status_text
    private val serverStatusImage = itemView.itemImage

    fun bindItem(serverDetails: ServerDetails, onDetailsClicked: (ServerDetails) -> Unit, onDeleteClicked: (ServerDetails) -> Unit) {
        serverName.text = serverDetails.serverName
        if (serverDetails.isOnline) {
            serverStatusText.text = itemView.resources.getString(R.string.common_online)
            serverStatusImage.setImageBitmap(BitmapFactory.decodeResource(itemView.resources, R.drawable.emerald_block))
            itemView.setOnClickListener { onDetailsClicked(serverDetails) }
            itemView.serverInfoButton.setOnClickListener { onDetailsClicked(serverDetails) }
        } else {
            serverStatusText.text = itemView.resources.getString(R.string.common_offline)
            serverStatusImage.setImageBitmap(BitmapFactory.decodeResource(itemView.resources, R.drawable.redstone_block))
        }
        itemView.deleteServerButton.setOnClickListener { onDeleteClicked(serverDetails) }
    }
}