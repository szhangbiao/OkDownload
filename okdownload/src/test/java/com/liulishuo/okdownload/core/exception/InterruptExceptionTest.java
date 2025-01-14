package com.liulishuo.okdownload.core.exception;

import com.liulishuo.okdownload.core.NamedRunnable;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class InterruptExceptionTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void printStackTrace() {
        assertThat(InterruptException.SIGNAL.getMessage()).isEqualTo("Interrupted");

        thrown.expect(IllegalAccessError.class);
        thrown.expectMessage("Stack is ignored for signal");
        InterruptException.SIGNAL.printStackTrace();
    }

    @Test
    public void testInterruptedStatus() {
        final CountDownLatch latch = new CountDownLatch(1);
        final Thread r1 = new Thread(new NamedRunnable("test runnable") {
            @Override
            protected void execute() throws InterruptedException {
                latch.countDown();
                Thread.sleep(100);
            }

            @Override
            protected void interrupted(InterruptedException e) {
            }

            @Override
            protected void finished() {
                Assert.assertTrue(Thread.currentThread().isInterrupted());
            }
        });
        r1.start();
        try {
            latch.await(100, TimeUnit.MILLISECONDS);
            r1.interrupt();
            r1.join();
        } catch (Exception ignored) {
        }
    }
}
