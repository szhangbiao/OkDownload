package com.liulishuo.okdownload.core.exception;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class NetworkPolicyExceptionTest {

    private NetworkPolicyException exception;

    @Before
    public void setup() {
        exception = new NetworkPolicyException();
    }

    @Test
    public void construct() {
        assertThat(exception.getMessage())
                .isEqualTo("Only allows downloading this task on the wifi network type!");
    }
}