package com.github.grzegorzekkk.serverstatusnotifier

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout

class ProgressBarHandler(private val mContext: Context) {
    private val blockUiView: View
    private val window: Window

    init {
        val layout = (mContext as Activity).findViewById<View>(android.R.id.content).rootView as ViewGroup
        window = mContext.window
        blockUiView = mContext.layoutInflater.inflate(R.layout.dialog_progressbar, layout, false)

        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)

        layout.addView(blockUiView, params)

        hide()
    }

    fun show() {
        blockUiView.visibility = View.VISIBLE
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun hide() {
        blockUiView.visibility = View.INVISIBLE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}