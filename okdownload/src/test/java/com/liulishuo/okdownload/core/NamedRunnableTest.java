package com.liulishuo.okdownload.core;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class NamedRunnableTest {

    private NamedRunnable runnable;

    @Before
    public void setup() {
        String name = "name";
        runnable = spy(new NamedRunnable(name) {
            @Override
            protected void execute() {
            }

            @Override
            protected void interrupted(InterruptedException e) {
            }

            @Override
            protected void finished() {
            }
        });
    }

    @Test
    public void run_nonInterrupt() throws InterruptedException {
        Thread.currentThread().setName("oldName");
        runnable.run();

        verify(runnable).execute();
        verify(runnable, never()).interrupted(any(InterruptedException.class));
        verify(runnable).finished();
        assertThat(Thread.currentThread().getName()).isEqualTo("oldName");
    }

    @Test
    public void run_interrupt() throws InterruptedException {
        doThrow(InterruptedException.class).when(runnable).execute();

        runnable.run();
        verify(runnable).execute();
        verify(runnable).interrupted(any(InterruptedException.class));
        verify(runnable).finished();
    }
}