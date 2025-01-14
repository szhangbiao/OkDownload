package com.liulishuo.filedownloader.util;

import com.liulishuo.filedownloader.DownloadTaskAdapter;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class FileDownloadUtilsTest {

    @Before
    public void setup() throws IOException {
        TestUtils.mockOkDownload();
    }

    @Test
    public void findDownloadTaskAdapter() {
        DownloadTask downloadTask = mock(DownloadTask.class);
        DownloadTaskAdapter downloadTaskAdapter = FileDownloadUtils
                .findDownloadTaskAdapter(downloadTask);
        assertNull(downloadTaskAdapter);

        final String url = "url";
        final String path = "path";
        final DownloadTaskAdapter mockDownloadTaskAdapter =
                (DownloadTaskAdapter) FileDownloader.getImpl().create(url).setPath(path);
        mockDownloadTaskAdapter.insureAssembleDownloadTask();
        downloadTaskAdapter = FileDownloadUtils
                .findDownloadTaskAdapter(mockDownloadTaskAdapter.getDownloadTask());
        assertThat(downloadTaskAdapter).isEqualTo(mockDownloadTaskAdapter);

        final DownloadTaskAdapter sameIdTask =
                (DownloadTaskAdapter) FileDownloader.getImpl().create(url).setPath(path);
        sameIdTask.insureAssembleDownloadTask();
        assertThat(sameIdTask.getId()).isEqualTo(mockDownloadTaskAdapter.getId());
        downloadTaskAdapter = FileDownloadUtils
                .findDownloadTaskAdapter(sameIdTask.getDownloadTask());
        assertThat(downloadTaskAdapter).isEqualTo(sameIdTask);
        assertThat(sameIdTask).isNotEqualTo(mockDownloadTaskAdapter);

    }
}
