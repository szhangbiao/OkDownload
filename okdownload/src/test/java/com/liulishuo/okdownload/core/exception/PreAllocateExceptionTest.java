package com.liulishuo.okdownload.core.exception;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class PreAllocateExceptionTest {

    private PreAllocateException exception;
    private long freeSpace = 1;
    private long requireSpace = 2;

    @Before
    public void setup() {
        exception = new PreAllocateException(requireSpace, freeSpace);
    }

    @Test
    public void construct() {
        assertThat(exception.getMessage()).isEqualTo(
                "There is Free space less than Require space: " + freeSpace + " < " + requireSpace);
        assertThat(exception.getRequireSpace()).isEqualTo(requireSpace);
        assertThat(exception.getFreeSpace()).isEqualTo(freeSpace);
    }
}