package com.yunqinglai.okdownload

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.yunqinglai.okdownload.base.BaseSampleActivity
import com.yunqinglai.okdownload.kotlin.listener.createDownloadContextListener
import com.yunqinglai.okdownload.util.queue.QueueController
import com.yunqinglai.okdownload.util.queue.QueueRecyclerAdapter

/**
 * On this demo you will be known how to download batch tasks as a queue and download with different
 * priority.
 */
class QueueActivity : BaseSampleActivity() {

    private var controller: QueueController? = null
    private var adapter: QueueRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue)
        initQueueActivity(
            findViewById(R.id.actionView),
            findViewById<View>(R.id.actionTv) as TextView,
            findViewById<View>(R.id.serialRb) as AppCompatRadioButton,
            findViewById<View>(R.id.parallelRb) as AppCompatRadioButton,
            findViewById<View>(R.id.recyclerView) as RecyclerView,
            findViewById<View>(R.id.deleteActionView) as CardView,
            findViewById(R.id.deleteActionTv)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        this.controller?.stop()
    }

    override fun titleRes(): Int = R.string.queue_download_title

    private fun initQueueActivity(
        actionView: View,
        actionTv: TextView,
        serialRb: AppCompatRadioButton,
        parallelRb: AppCompatRadioButton,
        recyclerView: RecyclerView,
        deleteActionView: CardView,
        deleteActionTv: View
    ) {
        initController(actionView, actionTv, serialRb, parallelRb, deleteActionView, deleteActionTv)
        initRecyclerView(recyclerView)
        initAction(actionView, actionTv, serialRb, parallelRb, deleteActionView, deleteActionTv)
    }

    private fun initController(
        actionView: View,
        actionTv: TextView,
        serialRb: AppCompatRadioButton,
        parallelRb: AppCompatRadioButton,
        deleteActionView: CardView,
        deleteActionTv: View
    ) {
        val controller = QueueController()
        this.controller = controller
        controller.initTasks(this, createDownloadContextListener {
            actionView.tag = null
            actionTv.setText(R.string.start)
            // to cancel
            controller.stop()

            serialRb.isEnabled = true
            parallelRb.isEnabled = true

            deleteActionView.isEnabled = true
            deleteActionView.cardElevation = deleteActionView.tag as Float
            deleteActionTv.isEnabled = true

            adapter?.notifyDataSetChanged()
        })
    }

    private fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        controller?.let {
            val adapter = QueueRecyclerAdapter(it)
            this.adapter = adapter
            recyclerView.adapter = adapter
        }
    }

    private fun initAction(
        actionView: View,
        actionTv: TextView,
        serialRb: AppCompatRadioButton,
        parallelRb: AppCompatRadioButton,
        deleteActionView: CardView,
        deleteActionTv: View
    ) {
        deleteActionView.setOnClickListener {
            controller?.deleteFiles()
            adapter?.notifyDataSetChanged()
        }

        actionTv.setText(R.string.start)
        actionView.setOnClickListener { v ->
            val started = v.tag != null

            if (started) {
                controller?.stop()
            } else {
                v.tag = Any()
                actionTv.setText(R.string.cancel)

                // to start
                controller?.start(serialRb.isChecked)
                adapter?.notifyDataSetChanged()

                serialRb.isEnabled = false
                parallelRb.isEnabled = false
                deleteActionView.isEnabled = false
                deleteActionView.tag = deleteActionView.cardElevation
                deleteActionView.cardElevation = 0f
                deleteActionTv.isEnabled = false
            }
        }
    }
}