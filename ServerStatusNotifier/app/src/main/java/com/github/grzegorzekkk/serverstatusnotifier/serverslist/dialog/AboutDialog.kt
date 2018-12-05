package com.github.grzegorzekkk.serverstatusnotifier.serverslist.dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.grzegorzekkk.serverstatusnotifier.R.layout.dialog_about
import kotlinx.android.synthetic.main.dialog_about.*

class AboutDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(dialog_about, container, false)
    }

    override fun onStart() {
        super.onStart()
        close_button.setOnClickListener {
            dismiss()
        }
    }
}