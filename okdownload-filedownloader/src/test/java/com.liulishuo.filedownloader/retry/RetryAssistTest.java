package com.liulishuo.filedownloader.retry;

import com.liulishuo.okdownload.DownloadTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RetryAssistTest {

    private RetryAssist retryAssist;
    private int retryTimes = 2;

    @Before
    public void setup() {
        retryAssist = new RetryAssist(2);
    }

    @Test
    public void constructor() {
        assertThat(retryAssist.retryTimes).isEqualTo(retryTimes);
        assertThat(retryAssist.getRetriedTimes()).isEqualTo(0);
    }

    @Test
    public void doRetry() {
        final DownloadTask task = mock(DownloadTask.class);

        retryAssist.doRetry(task);
        verify(task).enqueue(null);

        retryAssist.doRetry(task);
        verify(task, times(2)).enqueue(null);

    }

    @Test(expected = RuntimeException.class)
    public void doRetry_error() {
        final DownloadTask task = mock(DownloadTask.class);
        retryAssist.doRetry(task);
        retryAssist.doRetry(task);
        // will throw error
        retryAssist.doRetry(task);
    }

    @Test
    public void canRetry() {
        retryAssist.retriedTimes.set(0);
        assertThat(retryAssist.canRetry()).isTrue();
        retryAssist.retriedTimes.set(1);
        assertThat(retryAssist.canRetry()).isTrue();
        retryAssist.retriedTimes.set(2);
        assertThat(retryAssist.canRetry()).isFalse();
    }

}
