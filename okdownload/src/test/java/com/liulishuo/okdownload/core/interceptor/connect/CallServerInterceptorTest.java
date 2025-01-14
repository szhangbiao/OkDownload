package com.liulishuo.okdownload.core.interceptor.connect;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.core.connection.DownloadConnection;
import com.liulishuo.okdownload.core.download.DownloadChain;
import com.liulishuo.okdownload.core.download.DownloadStrategy;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;

import static com.liulishuo.okdownload.TestUtils.mockOkDownload;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallServerInterceptorTest {

    private CallServerInterceptor serverInterceptor;
    @Mock private DownloadChain chain;
    @Mock private DownloadConnection connection;
    @Mock private DownloadTask task;

    @BeforeClass
    public static void setupClass() throws IOException {
        mockOkDownload();
    }

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        serverInterceptor = new CallServerInterceptor();
        when(chain.getConnectionOrCreate()).thenReturn(connection);
        when(chain.getTask()).thenReturn(task);
    }

    @Test
    public void interceptConnect() throws Exception {
        serverInterceptor.interceptConnect(chain);

        final DownloadStrategy downloadStrategy = OkDownload.with().downloadStrategy();
        verify(downloadStrategy).inspectNetworkOnWifi(eq(task));
        verify(downloadStrategy).inspectNetworkAvailable();
    }
}