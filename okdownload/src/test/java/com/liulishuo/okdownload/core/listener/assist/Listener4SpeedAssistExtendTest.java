package com.liulishuo.okdownload.core.listener.assist;

import android.util.SparseArray;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.annotation.Config.NONE;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class Listener4SpeedAssistExtendTest {

    @Mock
    private DownloadTask task;
    @Mock
    private BreakpointInfo info;
    @Mock
    private Listener4SpeedAssistExtend.Listener4SpeedCallback callback;

    private Listener4SpeedAssistExtend.Listener4SpeedModel model;
    private Listener4SpeedAssistExtend assistExtend;

    @Before
    public void setup() {
        initMocks(this);

        when(task.getId()).thenReturn(1);

        when(info.getId()).thenReturn(1);

        final SparseArray<Long> blockOffsetMap = new SparseArray<>();
        final SparseArray<SpeedCalculator> blockSpeeds = new SparseArray<>();


        for (int i = 0; i < 3; i++) {
            when(info.getBlock(i)).thenReturn(mock(BlockInfo.class));
            blockOffsetMap.put(i, (long) i);
            blockSpeeds.put(i, mock(SpeedCalculator.class));
        }

        model = new Listener4SpeedAssistExtend.Listener4SpeedModel(1);
        model.info = info;
        model.blockSpeeds = blockSpeeds;
        model.blockCurrentOffsetMap = blockOffsetMap;
        model.taskSpeed = mock(SpeedCalculator.class);
        assistExtend = new Listener4SpeedAssistExtend();
        assistExtend.setCallback(callback);
    }

    @Test
    public void dispatchFetchProgress() {
        final boolean result = assistExtend.dispatchFetchProgress(task, 0, 1L, model);
        assertThat(result).isTrue();

        verify(model.blockSpeeds.get(0)).downloading(eq(1L));
        verify(model.blockSpeeds.get(1), never()).downloading(eq(1L));
        verify(model.blockSpeeds.get(2), never()).downloading(eq(1L));
        verify(model.taskSpeed).downloading(eq(1L));
        verify(callback).progressBlock(eq(task), eq(0), eq(model.blockCurrentOffsetMap.get(0)),
                eq(model.getBlockSpeed(0)));
    }

    @Test
    public void dispatchBlockEnd() {
        final boolean result = assistExtend.dispatchBlockEnd(task, 0, model);
        assertThat(result).isTrue();

        verify(model.blockSpeeds.get(0)).endTask();
        verify(callback)
                .blockEnd(task, eq(0), info.getBlock(0), model.getBlockSpeed(0));
    }

    @Test
    public void dispatchTaskEnd() {
        final boolean result = assistExtend.dispatchTaskEnd(task, EndCause.COMPLETED, null, model);
        assertThat(result).isTrue();

        verify(model.taskSpeed).endTask();
        verify(callback).taskEnd(eq(task), eq(EndCause.COMPLETED), nullable(Exception.class),
                eq(model.taskSpeed));
    }

    @Test
    public void dispatchTaskEnd_withUnValidModel() {
        final Listener4SpeedAssistExtend.Listener4SpeedModel invalidModel =
                new Listener4SpeedAssistExtend.Listener4SpeedModel(1);

        assertThat(invalidModel.blockSpeeds).isEqualTo(null);
        assertThat(invalidModel.taskSpeed).isEqualTo(null);

        final boolean result = assistExtend.dispatchTaskEnd(
                task, EndCause.CANCELED, null, invalidModel);
        assertThat(result).isTrue();

        verify(callback).taskEnd(eq(task), eq(EndCause.CANCELED), nullable(Exception.class),
                (SpeedCalculator) notNull());
    }
}