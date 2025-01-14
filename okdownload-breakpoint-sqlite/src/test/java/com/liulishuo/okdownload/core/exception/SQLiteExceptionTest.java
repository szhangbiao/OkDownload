package com.liulishuo.okdownload.core.exception;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class SQLiteExceptionTest {

    @Test
    public void constructor() {
        final SQLiteException exception = new SQLiteException("message");
        assertThat(exception.getMessage()).isEqualTo("message");
    }
}