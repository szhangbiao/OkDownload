package com.liulishuo.okdownload.core.file;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;

import static com.liulishuo.okdownload.TestUtils.mockOkDownload;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProcessFileStrategyTest {
    private ProcessFileStrategy strategy;

    @Mock
    private DownloadTask task;

    @Before
    public void setup() throws IOException {
        initMocks(this);
        strategy = new ProcessFileStrategy();
    }

    @Test
    public void discardProcess() throws IOException {
        final File existFile = new File("./exist-path");
        existFile.createNewFile();

        when(task.getFile()).thenReturn(existFile);

        strategy.discardProcess(task);

        assertThat(existFile.exists()).isFalse();
    }

    @Test(expected = IOException.class)
    public void discardProcess_deleteFailed() throws IOException {
        final File file = mock(File.class);
        when(task.getFile()).thenReturn(file);
        when(file.exists()).thenReturn(true);
        when(file.delete()).thenReturn(false);

        strategy.discardProcess(task);
    }

    @Test
    public void isPreAllocateLength() throws IOException {
        mockOkDownload();

        // no pre-allocate set on task.
        when(task.getSetPreAllocateLength()).thenReturn(null);

        final DownloadOutputStream.Factory factory = OkDownload.with().outputStreamFactory();
        when(factory.supportSeek()).thenReturn(false);

        assertThat(strategy.isPreAllocateLength(task)).isFalse();
        when(factory.supportSeek()).thenReturn(true);

        assertThat(strategy.isPreAllocateLength(task)).isTrue();

        // pre-allocate set on task.
        when(task.getSetPreAllocateLength()).thenReturn(false);
        assertThat(strategy.isPreAllocateLength(task)).isFalse();

        when(task.getSetPreAllocateLength()).thenReturn(true);
        assertThat(strategy.isPreAllocateLength(task)).isTrue();

        // pre-allocate set on task is true but can't support seek.
        when(factory.supportSeek()).thenReturn(false);
        assertThat(strategy.isPreAllocateLength(task)).isFalse();
    }
}