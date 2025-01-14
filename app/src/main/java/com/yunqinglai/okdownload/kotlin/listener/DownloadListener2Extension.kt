package com.yunqinglai.okdownload.kotlin.listener

import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.listener.DownloadListener2
import java.lang.Exception

/**
 * A concise way to create a [DownloadListener2], only the [DownloadListener2.taskEnd] is necessary.
 */
fun createListener2(onTaskStart: onTaskStart = {}, onTaskEnd: onTaskEnd): DownloadListener2 = object : DownloadListener2() {

    override fun taskStart(task: DownloadTask) = onTaskStart(task)

    override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?) = onTaskEnd(task, cause, realCause)
}