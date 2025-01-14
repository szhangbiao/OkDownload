package com.liulishuo.filedownloader;

public abstract class FileDownloadListener {

    public FileDownloadListener() {
    }

    public FileDownloadListener(int priority) {
    }

    protected boolean isInvalid() {
        return false;
    }

    protected abstract void pending(BaseDownloadTask task, int soFarBytes, int totalBytes);

    protected void started(BaseDownloadTask task) {
    }

    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
    }

    protected abstract void progress(BaseDownloadTask task, int soFarBytes, int totalBytes);

    protected void blockComplete(BaseDownloadTask task) throws Throwable {
    }

    protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
    }

    protected abstract void completed(BaseDownloadTask task);

    protected abstract void paused(BaseDownloadTask task, int soFarBytes, int totalBytes);

    protected abstract void error(BaseDownloadTask task, Throwable e);

    protected abstract void warn(BaseDownloadTask task);
}
