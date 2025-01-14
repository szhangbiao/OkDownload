package com.liulishuo.filedownloader;

import com.liulishuo.filedownloader.event.IDownloadEvent;
import com.liulishuo.filedownloader.event.IDownloadListener;

/**
 * The listener for listening whether the service establishes connection or disconnected.
 */
@Deprecated
public abstract class FileDownloadConnectListener extends IDownloadListener {

    public FileDownloadConnectListener() {
    }

    @Override
    public boolean callback(IDownloadEvent event) {
        return false;
    }

    /**
     * connected file download service
     */
    public abstract void connected();

    /**
     * disconnected file download service
     */
    public abstract void disconnected();

}
