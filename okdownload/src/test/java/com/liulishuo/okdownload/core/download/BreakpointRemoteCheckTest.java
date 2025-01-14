package com.liulishuo.okdownload.core.download;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.exception.FileBusyAfterRunException;
import com.liulishuo.okdownload.core.exception.ServerCanceledException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.net.HttpURLConnection;

import static com.liulishuo.okdownload.TestUtils.mockOkDownload;
import static com.liulishuo.okdownload.core.Util.RANGE_NOT_SATISFIABLE;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BreakpointRemoteCheckTest {

    private BreakpointRemoteCheck check;

    @Mock private DownloadTask task;
    @Mock private BreakpointInfo info;
    @Mock private ConnectTrial connectTrial;

    private String responseFilename = "response.filename";
    private String responseEtag = "response.etag";

    @Before
    public void setup() throws Exception {
        initMocks(this);

        check = spy(new BreakpointRemoteCheck(task, info));
        when(check.createConnectTrial()).thenReturn(connectTrial);

        when(connectTrial.getResponseFilename()).thenReturn(responseFilename);
        when(connectTrial.getResponseEtag()).thenReturn(responseEtag);
    }

    @Test
    public void check_assembleBasicData() throws IOException {
        mockOkDownload();

        check.check();

        final DownloadStrategy strategy = OkDownload.with().downloadStrategy();
        verify(strategy).validFilenameFromResponse(eq(responseFilename), eq(task), eq(info));
        verify(info).setChunked(eq(connectTrial.isChunked()));
        verify(info).setEtag(eq(responseEtag));
    }

    @Test(expected = FileBusyAfterRunException.class)
    public void check_fileConflictAfterRun() throws IOException {
        mockOkDownload();

        when(OkDownload.with().downloadDispatcher().isFileConflictAfterRun(eq(task)))
                .thenReturn(true);
        check.check();
    }

    @Test
    public void check_collectResult() throws IOException {
        mockOkDownload();

        when(connectTrial.getInstanceLength()).thenReturn(1L);
        when(connectTrial.isAcceptRange()).thenReturn(true);

        check.check();

        assertThat(check.isResumable()).isTrue();
        assertThat(check.getCause()).isNull();
        assertThat(check.getInstanceLength()).isEqualTo(1L);
        assertThat(check.isAcceptRange()).isTrue();

        final ResumeFailedCause cause = mock(ResumeFailedCause.class);
        when(OkDownload.with().downloadStrategy()
                .getPreconditionFailedCause(anyInt(), anyBoolean(), eq(info), eq(responseEtag)))
                .thenReturn(cause);
        check.check();

        assertThat(check.isResumable()).isFalse();
        assertThat(check.getCause()).isEqualTo(cause);
    }

    @Test(expected = ServerCanceledException.class)
    public void check_serverCanceled() throws IOException {
        mockOkDownload();

        when(OkDownload.with().downloadStrategy()
                .isServerCanceled(0, false))
                .thenReturn(true);
        check.check();
    }

    @Test
    public void check_trialSpecialPass() throws IOException {
        mockOkDownload();

        when(OkDownload.with().downloadStrategy()
                .isServerCanceled(0, false))
                .thenReturn(true);

        doReturn(true).when(check).isTrialSpecialPass(anyInt(), anyLong(), anyBoolean());

        check.check();
        // no exception
    }

    @Test
    public void isTrialSpecialPass() {
        assertThat(check.isTrialSpecialPass(RANGE_NOT_SATISFIABLE, 0, true)).isTrue();
        assertThat(check.isTrialSpecialPass(RANGE_NOT_SATISFIABLE, -1, true)).isFalse();
        assertThat(check.isTrialSpecialPass(RANGE_NOT_SATISFIABLE, 0, false)).isFalse();
        assertThat(check.isTrialSpecialPass(RANGE_NOT_SATISFIABLE, -1, false)).isFalse();
        assertThat(check.isTrialSpecialPass(HttpURLConnection.HTTP_PARTIAL, 0, true)).isFalse();
    }

    @Test(expected = IllegalStateException.class)
    public void getCauseOrThrown() throws Exception {
        final ResumeFailedCause failedCause = mock(ResumeFailedCause.class);
        check.failedCause = failedCause;
        assertThat(check.getCauseOrThrow()).isEqualTo(failedCause);

        check.failedCause = null;
        check.getCauseOrThrow();
    }

}