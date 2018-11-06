package com.github.grzegorzekkk.serverstatusnotifier.serverslist.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.github.grzegorzekkk.serverstatusnotifier.ProgressBarHandler
import com.github.grzegorzekkk.serverstatusnotifier.R
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails
import kotlinx.android.synthetic.main.dialog_server_delete.*

class DeleteServerDialog : DialogFragment() {
    private lateinit var progressBarHandler: ProgressBarHandler
    lateinit var serverDetails: ServerDetails
    lateinit var listener: OnServerDeleteListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_server_delete, container, false)
    }

    override fun onStart() {
        super.onStart()
        listener = activity as OnServerDeleteListener

        cancel_delete_button.setOnClickListener {
            dismiss()
        }
        confirm_delete_button.setOnClickListener {
            listener.onServerDelete(serverDetails)
            dismiss()
        }
    }

    interface OnServerDeleteListener {
        fun onServerDelete(server: ServerDetails)
    }
}