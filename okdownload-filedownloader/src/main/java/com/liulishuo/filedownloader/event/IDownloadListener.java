package com.liulishuo.filedownloader.event;

/**
 * The listener is used to listen the publish event from Event Pool.
 *
 * @see IDownloadEvent
 */
public abstract class IDownloadListener {
    public abstract boolean callback(IDownloadEvent event);
}
