package com.github.grzegorzekkk.serverstatusnotifier.serverconsole

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.grzegorzekkk.serverstatusnotifier.serverconsole.model.ConsoleLine
import kotlinx.android.synthetic.main.console_line.view.*

class ConsoleLineViewHolder(lineView: View) : RecyclerView.ViewHolder(lineView) {
    private val lineContent = lineView.line

    fun bindItem(consoleLine: ConsoleLine) {
        lineContent.text = consoleLine.content
    }
}