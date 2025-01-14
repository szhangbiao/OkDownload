package com.liulishuo.filedownloader;

import com.liulishuo.okdownload.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class FileDownloadQueueSetTest {

    @Mock
    private
    FileDownloadListener listener;

    @Before
    public void setup() throws IOException {
        initMocks(this);

        TestUtils.mockOkDownload();
    }

    @Test
    public void downloadTogether() {
        final BaseDownloadTask task1 = FileDownloader.getImpl().create("url1");
        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(listener);

        queueSet.downloadTogether(task1);

        assertThat(queueSet.isSerial).isEqualTo(false);
        assertThat(queueSet.tasks).hasSize(1);
        assertThat(queueSet.tasks[0]).isEqualTo(task1);

        queueSet.tasks[0] = null;
        assertThat(queueSet.tasks[0]).isEqualTo(null);

        final List<BaseDownloadTask> tasks = new ArrayList<>();
        tasks.add(task1);
        queueSet.downloadTogether(tasks);

        assertThat(queueSet.isSerial).isEqualTo(false);
        assertThat(queueSet.tasks).hasSize(1);
        assertThat(queueSet.tasks[0]).isEqualTo(task1);
    }

    @Test
    public void downloadSequentially() {
        final BaseDownloadTask task1 = FileDownloader.getImpl().create("url1");
        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(listener);

        queueSet.downloadSequentially(task1);

        assertThat(queueSet.isSerial).isEqualTo(true);
        assertThat(queueSet.tasks).hasSize(1);
        assertThat(queueSet.tasks[0]).isEqualTo(task1);

        queueSet.tasks[0] = null;
        assertThat(queueSet.tasks[0]).isEqualTo(null);

        final List<BaseDownloadTask> tasks = new ArrayList<>();
        tasks.add(task1);
        queueSet.downloadSequentially(tasks);

        assertThat(queueSet.isSerial).isEqualTo(true);
        assertThat(queueSet.tasks).hasSize(1);
        assertThat(queueSet.tasks[0]).isEqualTo(task1);
    }
}
