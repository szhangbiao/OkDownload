package com.liulishuo.okdownload.core.exception;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class RetryExceptionTest {

    @Test
    public void construct() {
        String message = "message";
        RetryException exception = new RetryException(message);
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}