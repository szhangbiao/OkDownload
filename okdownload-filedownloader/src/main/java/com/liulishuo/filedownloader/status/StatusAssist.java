package com.liulishuo.filedownloader.status;

import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.StatusUtil;

public class StatusAssist {

    private byte status = FileDownloadStatus.INVALID_STATUS;

    private DownloadTask downloadTask;

    public synchronized DownloadTask getDownloadTask() {
        return downloadTask;
    }

    public synchronized void setDownloadTask(DownloadTask downloadTask) {
        this.downloadTask = downloadTask;
    }

    public synchronized byte getStatus() {
        if (downloadTask == null) {
            return status;
        }
        StatusUtil.Status okDownloadStatus = StatusUtil.getStatus(downloadTask);
        status = convert(okDownloadStatus);
        return status;
    }

    private synchronized byte convert(StatusUtil.Status status) {
        return FileDownloadUtils.convertDownloadStatus(status);
    }

    public synchronized boolean isUsing() {
        return getStatus() != FileDownloadStatus.INVALID_STATUS;
    }

    public synchronized boolean isOver() {
        return FileDownloadStatus.isOver(getStatus());
    }
}
