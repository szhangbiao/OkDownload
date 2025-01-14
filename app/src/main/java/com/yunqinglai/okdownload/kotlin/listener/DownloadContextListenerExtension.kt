package com.yunqinglai.okdownload.kotlin.listener

import com.liulishuo.okdownload.DownloadContext
import com.liulishuo.okdownload.DownloadContextListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import java.lang.Exception

/**
 * Correspond to [com.liulishuo.okdownload.DownloadContextListener.taskEnd].
 */
typealias onQueueTaskEnd = (
    context: DownloadContext,
    task: DownloadTask,
    cause: EndCause,
    realException: Exception?,
    remainCount: Int
) -> Unit

/**
 * Correspond to [com.liulishuo.okdownload.DownloadContextListener.queueEnd].
 */
typealias onQueueEnd = (context: DownloadContext) -> Unit

/**
 * A concise way to create a [DownloadContextListener], only the
 * [DownloadContextListener.queueEnd] is necessary.
 */
fun createDownloadContextListener(onQueueTaskEnd: onQueueTaskEnd? = null, onQueueEnd: onQueueEnd): DownloadContextListener = object : DownloadContextListener {
    override fun taskEnd(
        context: DownloadContext,
        task: DownloadTask,
        cause: EndCause,
        realCause: Exception?,
        remainCount: Int
    ) {
        onQueueTaskEnd?.invoke(context, task, cause, realCause, remainCount)
    }

    override fun queueEnd(context: DownloadContext) = onQueueEnd(context)
}