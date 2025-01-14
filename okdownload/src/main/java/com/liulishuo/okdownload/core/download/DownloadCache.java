package com.liulishuo.okdownload.core.download;

import androidx.annotation.NonNull;

import com.liulishuo.okdownload.core.Util;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.exception.FileBusyAfterRunException;
import com.liulishuo.okdownload.core.exception.InterruptException;
import com.liulishuo.okdownload.core.exception.PreAllocateException;
import com.liulishuo.okdownload.core.exception.ResumeFailedException;
import com.liulishuo.okdownload.core.exception.ServerCanceledException;
import com.liulishuo.okdownload.core.file.MultiPointOutputStream;

import java.io.IOException;
import java.net.SocketException;

public class DownloadCache {
    private final MultiPointOutputStream outputStream;
    private String redirectLocation;
    private volatile boolean preconditionFailed;
    private volatile boolean userCanceled;
    private volatile boolean serverCanceled;
    private volatile boolean unknownError;
    private volatile boolean fileBusyAfterRun;
    private volatile boolean preAllocateFailed;
    private volatile IOException realCause;

    DownloadCache(@NonNull MultiPointOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    private DownloadCache() {
        this.outputStream = null;
    }

    @NonNull
    MultiPointOutputStream getOutputStream() {
        if (outputStream == null) throw new IllegalArgumentException();
        return outputStream;
    }

    String getRedirectLocation() {
        return redirectLocation;
    }

    void setRedirectLocation(String redirectLocation) {
        this.redirectLocation = redirectLocation;
    }

    boolean isPreconditionFailed() {
        return preconditionFailed;
    }

    public void setPreconditionFailed(IOException realCause) {
        this.preconditionFailed = true;
        this.realCause = realCause;
    }

    public boolean isUserCanceled() {
        return userCanceled;
    }

    boolean isServerCanceled() {
        return serverCanceled;
    }

    public void setServerCanceled(IOException realCause) {
        this.serverCanceled = true;
        this.realCause = realCause;
    }

    boolean isUnknownError() {
        return unknownError;
    }

    public void setUnknownError(IOException realCause) {
        this.unknownError = true;
        this.realCause = realCause;
    }

    boolean isFileBusyAfterRun() {
        return fileBusyAfterRun;
    }

    public boolean isPreAllocateFailed() {
        return preAllocateFailed;
    }

    public void setPreAllocateFailed(IOException realCause) {
        this.preAllocateFailed = true;
        this.realCause = realCause;
    }

    IOException getRealCause() {
        return realCause;
    }

    ResumeFailedCause getResumeFailedCause() {
        return ((ResumeFailedException) realCause).getResumeFailedCause();
    }

    public boolean isInterrupt() {
        return preconditionFailed || userCanceled || serverCanceled || unknownError || fileBusyAfterRun || preAllocateFailed;
    }

    void setUserCanceled() {
        this.userCanceled = true;
    }

    public void setFileBusyAfterRun() {
        this.fileBusyAfterRun = true;
    }

    public void catchException(IOException e) {
        if (isUserCanceled()) return; // ignored

        if (e instanceof ResumeFailedException) {
            setPreconditionFailed(e);
        } else if (e instanceof ServerCanceledException) {
            setServerCanceled(e);
        } else if (e == FileBusyAfterRunException.SIGNAL) {
            setFileBusyAfterRun();
        } else if (e instanceof PreAllocateException) {
            setPreAllocateFailed(e);
        } else if (e != InterruptException.SIGNAL) {
            setUnknownError(e);
            if (!(e instanceof SocketException)) {
                // we know socket exception, so ignore it,  otherwise print stack trace.
                Util.d("DownloadCache", "catch unknown error " + e);
            }
        }
    }

    static class PreError extends DownloadCache {
        PreError(IOException realCause) {
            super(null);
            setUnknownError(realCause);
        }
    }
}