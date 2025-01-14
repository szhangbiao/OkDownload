package com.liulishuo.okdownload.core.listener.assist;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.annotation.Config.NONE;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class Listener1AssistTest {

    private final int taskId = 1;
    private Listener1Assist assist;
    @Mock
    private Listener1Assist.Listener1Callback callback;
    @Mock
    private DownloadTask task;
    @Mock
    private BreakpointInfo info;
    @Mock
    private ResumeFailedCause cause;
    @Mock
    private ListenerModelHandler<Listener1Assist.Listener1Model> handler;
    @Mock
    private Listener1Assist.Listener1Model model;

    @Before
    public void setup() {
        initMocks(this);

        assist = new Listener1Assist(handler);
        assist.setCallback(callback);

        when(task.getId()).thenReturn(taskId);
        when(task.getInfo()).thenReturn(info);
    }

    @Test
    public void taskStart() {
        when(handler.addAndGetModel(task, null)).thenReturn(model);
        assist.taskStart(task);

        verify(callback).taskStart(eq(task), eq(model));
    }

    @Test
    public void taskEnd() {
        when(handler.removeOrCreate(task, info)).thenReturn(model);

        assist.taskEnd(task, EndCause.COMPLETED, null);
        verify(handler).removeOrCreate(eq(task), nullable(BreakpointInfo.class));

        verify(callback).taskEnd(eq(task), eq(EndCause.COMPLETED), nullable(Exception.class),
                eq(model));
    }

    @Test
    public void downloadFromBeginning() {
        // no model
        when(handler.getOrRecoverModel(task, info)).thenReturn(null);
        assist.downloadFromBeginning(task, info, cause);
        verify(model, never()).onInfoValid(eq(info));

        Listener1Assist.Listener1Model model = spy(new Listener1Assist.Listener1Model(1));
        when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.downloadFromBeginning(task, info, cause);
        verify(model).onInfoValid(eq(info));

        model = new Listener1Assist.Listener1Model(1);
        when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.downloadFromBeginning(task, info, cause);
        Assert.assertTrue(model.isStarted);
        Assert.assertFalse(model.isFromResumed);
        Assert.assertTrue(model.isFirstConnect);
        verify(callback, never()).retry(eq(task), eq(cause));

        // retry
        model.isStarted = true;
        when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.downloadFromBeginning(task, info, cause);
        verify(callback).retry(eq(task), eq(cause));
    }

    @Test
    public void downloadFromBreakpoint() {
        // no model
        when(handler.getOrRecoverModel(task, info)).thenReturn(null);
        assist.downloadFromBreakpoint(task, info);
        verify(model, never()).onInfoValid(eq(info));

        when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.downloadFromBreakpoint(task, info);
        verify(model).onInfoValid(eq(info));

        // assign
        final Listener1Assist.Listener1Model model = new Listener1Assist.Listener1Model(1);
        when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.downloadFromBreakpoint(task, info);
        Assert.assertTrue(model.isStarted);
        Assert.assertTrue(model.isFromResumed);
        Assert.assertTrue(model.isFirstConnect);
    }

    @Test
    public void connectEnd() {
        // no model
        when(handler.getOrRecoverModel(task, info)).thenReturn(null);
        assist.connectEnd(task);
        verify(callback, never()).connected(eq(task), anyInt(), anyLong(), anyLong());

        // callback
        final Listener1Assist.Listener1Model model = new Listener1Assist.Listener1Model(1);
        model.onInfoValid(info);
        model.blockCount = 1;
        model.currentOffset.set(2);
        model.totalLength = 3;
        when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.connectEnd(task);
        verify(callback).connected(eq(task), eq(1), eq(2L), eq(3L));

        // assign
        model.isFromResumed = true;
        model.isFirstConnect = true;
        assist.connectEnd(task);
        Assert.assertFalse(model.isFirstConnect);
    }

    @Test
    public void fetchProgress() {
        // no model
        when(handler.getOrRecoverModel(task, info)).thenReturn(null);
        assist.fetchProgress(task, 1);
        verify(callback, never()).progress(eq(task), anyLong(), anyLong());

        // callback
        final Listener1Assist.Listener1Model model = new Listener1Assist.Listener1Model(1);
        model.currentOffset.set(2);
        model.totalLength = 3;
        when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.fetchProgress(task, 1);
        verify(callback).progress(eq(task), eq(2L + 1L), eq(3L));
    }

    @Test
    public void isAlwaysRecoverAssistModel() {
        when(handler.isAlwaysRecoverAssistModel()).thenReturn(true);
        Assert.assertTrue(assist.isAlwaysRecoverAssistModel());
        when(handler.isAlwaysRecoverAssistModel()).thenReturn(false);
        Assert.assertFalse(assist.isAlwaysRecoverAssistModel());
    }

    @Test
    public void setAlwaysRecoverAssistModel() {
        assist.setAlwaysRecoverAssistModel(true);
        verify(handler).setAlwaysRecoverAssistModel(eq(true));
        assist.setAlwaysRecoverAssistModel(false);
        verify(handler).setAlwaysRecoverAssistModel(eq(false));
    }

    @Test
    public void setAlwaysRecoverAssistModelIfNotSet() {
        assist.setAlwaysRecoverAssistModelIfNotSet(true);
        verify(handler).setAlwaysRecoverAssistModelIfNotSet(eq(true));
        assist.setAlwaysRecoverAssistModelIfNotSet(false);
        verify(handler).setAlwaysRecoverAssistModelIfNotSet(eq(false));
    }
}