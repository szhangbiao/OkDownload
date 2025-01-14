package com.liulishuo.okdownload.core.exception;

import java.io.IOException;

import com.liulishuo.okdownload.core.cause.ResumeFailedCause;

public class ResumeFailedException extends IOException {
    private final ResumeFailedCause resumeFailedCause;

    public ResumeFailedException(ResumeFailedCause cause) {
        super("Resume failed because of " + cause);
        this.resumeFailedCause = cause;
    }

    public ResumeFailedCause getResumeFailedCause() {
        return resumeFailedCause;
    }
}
