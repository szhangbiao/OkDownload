package com.liulishuo.okdownload.core.exception;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ServerCanceledExceptionTest {

    @Test
    public void construct() {
        int responseCode = 1;
        long offset = 2;

        ServerCanceledException exception = new ServerCanceledException(responseCode, offset);
        assertThat(exception.getMessage())
                .isEqualTo("Response code can't handled on internal " + responseCode
                        + " with current offset "
                        + offset);
        assertThat(exception.getResponseCode()).isEqualTo(responseCode);
    }
}