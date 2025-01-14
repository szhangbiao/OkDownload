package com.liulishuo.filedownloader;

import android.app.Notification;

/**
 * The FileDownload synchronous line.
 *
 * @see FileDownloader#insureServiceBind()
 */

public class FileDownloadLine {

    @Deprecated
    public void startForeground(final int id, final Notification notification) {
        // do nothing
    }

    /**
     * The {@link FileDownloader#getSoFar(int)} request.
     */
    public long getSoFar(final int id) {
        return FileDownloader.getImpl().getSoFar(id);
    }

    /**
     * The {@link FileDownloader#getTotal(int)} request.
     */
    public long getTotal(final int id) {
        return FileDownloader.getImpl().getTotal(id);
    }

    /**
     * The {@link FileDownloader#getStatus(int, String)} request.
     */
    public byte getStatus(final int id, final String path) {
        return FileDownloader.getImpl().getStatus(id, path);
    }
}
