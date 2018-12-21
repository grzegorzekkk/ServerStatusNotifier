package com.github.grzegorzekkk.serverstatusnotifier.serverconsole

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.grzegorzekkk.serverstatusnotifier.R
import com.github.grzegorzekkk.serverstatusnotifier.R.layout.console_line
import com.github.grzegorzekkk.serverstatusnotifier.serverconsole.model.ConsoleLine

open class ConsoleLineAdapter(var lines: List<ConsoleLine>) : RecyclerView.Adapter<ConsoleLineViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsoleLineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(console_line, parent, false)
        return ConsoleLineViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsoleLineViewHolder, position: Int) = holder.bindItem(lines[position])

    override fun getItemCount(): Int = lines.size
}