package com.github.grzegorzekkk.serverstatusnotifier.serverdetails

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import com.github.grzegorzekkk.serverstatusnotifier.R
import com.github.grzegorzekkk.serverstatusnotifier.serverconsole.ServerConsoleActivity
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.SrvConnDetails
import kotlinx.android.synthetic.main.activity_server_details.*
import kotlinx.android.synthetic.main.details_item.view.*

class ServerDetailsActivity : AppCompatActivity() {
    private lateinit var srvDetailsViewModel: ServerDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_details)

        val srvDetails = intent.getSerializableExtra(ServerDetails.SERIAL_NAME) as ServerDetails

        console_button.setOnClickListener {
            showConsoleActivity(srvDetails.connDetails)
        }

        srvDetailsViewModel = ViewModelProviders.of(this).get(ServerDetailsViewModel::class.java)
        srvDetailsViewModel.serverDetails().observe(this, Observer(this::updateView))
        srvDetailsViewModel.load(srvDetails)
    }

    private fun updateView(serverDetails: ServerDetails?) {
        if (serverDetails != null) {
            serverName.text = serverDetails.serverName
            playersCountPanel.itemTitle.text = getString(R.string.details_player_count)
            playersCountPanel.itemData.text = String.format(getString(R.string.value_of_format), serverDetails.playersCount, serverDetails.playersMax)
            versionPanel.itemTitle.text = getString(R.string.server_version)
            versionPanel.itemData.text = serverDetails.serverVersion
            versionPanel.itemData.gravity = Gravity.CENTER
        }
    }

    private fun showConsoleActivity(srvConnDetails: SrvConnDetails) {
        val i = Intent(this, ServerConsoleActivity::class.java)
                .putExtra(ServerDetails.SERIAL_NAME, srvConnDetails)
        startActivity(i)
    }
}