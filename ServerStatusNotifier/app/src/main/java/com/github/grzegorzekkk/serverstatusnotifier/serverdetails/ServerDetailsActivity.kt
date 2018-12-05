package com.github.grzegorzekkk.serverstatusnotifier.serverdetails

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.grzegorzekkk.serverstatusnotifier.R
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails
import kotlinx.android.synthetic.main.activity_server_details.*
import kotlinx.android.synthetic.main.details_item.view.*

class ServerDetailsActivity : AppCompatActivity() {
    private lateinit var srvDetailsViewModel: ServerDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_details)

        srvDetailsViewModel = ViewModelProviders.of(this).get(ServerDetailsViewModel::class.java)
        srvDetailsViewModel.serverDetails().observe(this, Observer(this::updateView))
        srvDetailsViewModel.load(intent.getSerializableExtra(ServerDetails.SERIAL_NAME) as ServerDetails)
    }

    private fun updateView(serverDetails: ServerDetails?) {
        serverName.text = serverDetails?.serverName
        playersCountPanel.itemTitle.text = getString(R.string.details_player_count)
        playersCountPanel.itemData.text = serverDetails?.playersCount.toString()
    }
}