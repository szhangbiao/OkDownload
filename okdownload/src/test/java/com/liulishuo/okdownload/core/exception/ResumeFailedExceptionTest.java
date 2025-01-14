package com.liulishuo.okdownload.core.exception;

import com.liulishuo.okdownload.core.cause.ResumeFailedCause;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ResumeFailedExceptionTest {

    @Test
    public void construct() {
        final ResumeFailedCause cause = ResumeFailedCause.FILE_NOT_EXIST;

        ResumeFailedException exception = new ResumeFailedException(cause);
        assertThat(exception.getMessage()).isEqualTo("Resume failed because of " + cause);
        assertThat(exception.getResumeFailedCause()).isEqualTo(cause);
    }
}