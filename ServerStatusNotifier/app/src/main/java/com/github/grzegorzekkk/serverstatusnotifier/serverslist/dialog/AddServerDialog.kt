package com.github.grzegorzekkk.serverstatusnotifier.serverslist.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.grzegorzekkk.serverstatusnotifier.ProgressBarHandler
import com.github.grzegorzekkk.serverstatusnotifier.R
import com.github.grzegorzekkk.serverstatusnotifier.ServersActivity
import com.github.grzegorzekkk.serverstatusnotifier.inputfilters.IntegerRangeInputFilter
import com.github.grzegorzekkk.serverstatusnotifier.serverslist.task.AddNewServerTask
import kotlinx.android.synthetic.main.dialog_add_server.*

class AddServerDialog : DialogFragment() {
    private lateinit var progressBarHandler: ProgressBarHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_add_server, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialogServerPort.filters = arrayOf(IntegerRangeInputFilter(1, 65565))

        close_button.setOnClickListener {
            dismiss()
        }
        confirm_button.isEnabled = false
        confirm_button.setOnClickListener {
            if (!isValidIpOrHostname(dialogServerIP.text.toString())) {
                Toast.makeText(activity, R.string.invalid_ip, Toast.LENGTH_SHORT).show()
            } else {
                dismiss()
                if (activity is ServersActivity) {
                    val serversActivity = activity as ServersActivity
                    val task = AddNewServerTask(serversActivity)
                    progressBarHandler = ProgressBarHandler(serversActivity)
                    task.progressBar = progressBarHandler
                    task.execute(dialogServerIP.text.toString(), dialogServerPort.text.toString(), dialogServerPassword.text.toString())
                }
            }
        }
        enableConfirmIfInputNotEmpty()
    }

    private fun isValidIpOrHostname(input: String): Boolean {
        return input.matches(IP_ADDRESS_REGEX) or input.matches(HOSTNAME_REGEX)
    }

    private fun enableConfirmIfInputNotEmpty() {
        dialogServerPort.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                confirm_button.isEnabled = s.toString().trim().isNotEmpty() && dialogServerIP.text.toString().trim().isNotEmpty() && dialogServerPassword.text.toString().trim().isNotEmpty()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        dialogServerIP.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                confirm_button.isEnabled = s.toString().trim().isNotEmpty() && dialogServerPort.text.toString().trim().isNotEmpty() && dialogServerPassword.text.toString().trim().isNotEmpty()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        dialogServerPassword.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                confirm_button.isEnabled = s.toString().trim().isNotEmpty() && dialogServerPort.text.toString().trim().isNotEmpty() && dialogServerIP.text.toString().trim().isNotEmpty()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    companion object {
        val IP_ADDRESS_REGEX = Regex("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")
        val HOSTNAME_REGEX = Regex("^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$")
    }
}