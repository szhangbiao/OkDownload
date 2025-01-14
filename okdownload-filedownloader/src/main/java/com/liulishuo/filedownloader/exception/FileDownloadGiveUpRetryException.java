package com.liulishuo.filedownloader.exception;

/**
 * Throwing this exception, when we can't know the size of the download file, and its
 * Transfer-Encoding is not Chunked either.
 * <p/>
 * When you occur this type exception, the chance of retry will be ignored.
 */
public class FileDownloadGiveUpRetryException extends RuntimeException {
    public FileDownloadGiveUpRetryException(final String detailMessage) {
        super(detailMessage);
    }
}