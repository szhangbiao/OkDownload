package com.liulishuo.okdownload.core.listener;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static org.mockito.MockitoAnnotations.initMocks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DownloadListener2Test {

    private DownloadListener2 listener2 = new DownloadListener2() {
        @Override
        public void taskStart(@NonNull DownloadTask task) {
        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
        }
    };

    @Mock
    private DownloadTask task;
    @Mock
    private BreakpointInfo info;
    @Mock
    private ResumeFailedCause cause;
    @Mock
    private Map<String, List<String>> headerFields;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void connectTrialStart() throws Exception {
        listener2.connectTrialStart(task, headerFields);
    }

    @Test
    public void connectTrialEnd() throws Exception {
        listener2.connectTrialEnd(task, 200, headerFields);
    }

    @Test
    public void downloadFromBeginning() throws Exception {
        listener2.downloadFromBeginning(task, info, cause);
    }

    @Test
    public void downloadFromBreakpoint() throws Exception {
        listener2.downloadFromBreakpoint(task, info);
    }

    @Test
    public void connectStart() throws Exception {
        listener2.connectStart(task, 1, headerFields);
    }

    @Test
    public void connectEnd() throws Exception {
        listener2.connectEnd(task, 1, 200, headerFields);
    }

    @Test
    public void fetchStart() throws Exception {
        listener2.fetchStart(task, 1, 2);
    }

    @Test
    public void fetchProgress() throws Exception {
        listener2.fetchProgress(task, 1, 2);
    }

    @Test
    public void fetchEnd() throws Exception {
        listener2.fetchEnd(task, 1, 2);
    }

}