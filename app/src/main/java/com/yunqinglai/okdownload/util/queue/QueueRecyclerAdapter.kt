package com.yunqinglai.okdownload.util.queue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yunqinglai.okdownload.R

class QueueRecyclerAdapter(private val controller: QueueController) : RecyclerView.Adapter<QueueRecyclerAdapter.QueueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        return QueueViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_queue, parent, false))
    }

    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
        controller.bind(holder, position)
    }

    override fun getItemCount(): Int = controller.size()

    class QueueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTv: TextView = itemView.findViewById(R.id.nameTv)
        var priorityTv: TextView = itemView.findViewById(R.id.priorityTv)
        var prioritySb: SeekBar = itemView.findViewById(R.id.prioritySb)
        var statusTv: TextView = itemView.findViewById(R.id.statusTv)
        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }
}