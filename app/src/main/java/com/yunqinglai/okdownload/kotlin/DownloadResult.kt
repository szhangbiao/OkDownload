package com.yunqinglai.okdownload.kotlin

import com.liulishuo.okdownload.core.cause.EndCause

/**
 * This class only represents download successful.
 */
data class DownloadResult(val cause: EndCause) {
    fun becauseOfCompleted(): Boolean = cause == EndCause.COMPLETED
    fun becauseOfRepeatedTask(): Boolean = cause == EndCause.SAME_TASK_BUSY || cause == EndCause.FILE_BUSY
}