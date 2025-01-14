package com.liulishuo.filedownloader.exception;

/**
 * Throwing this exception, when there are some security issues found on FileDownloader.
 */
public class FileDownloadSecurityException extends Exception {
    public FileDownloadSecurityException(String msg) {
        super(msg);
    }
}
