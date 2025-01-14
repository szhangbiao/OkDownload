package com.liulishuo.okdownload.core.listener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.assist.Listener4Assist;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;
import com.liulishuo.okdownload.core.listener.assist.ListenerModelHandler;

public abstract class DownloadListener4WithSpeed extends DownloadListener4 implements Listener4SpeedAssistExtend.Listener4SpeedCallback {

    private DownloadListener4WithSpeed(Listener4SpeedAssistExtend assistExtend) {
        super(new Listener4Assist<>(new Listener4WithSpeedModelCreator()));
        assistExtend.setCallback(this);
        setAssistExtend(assistExtend);
    }

    public DownloadListener4WithSpeed() {
        this(new Listener4SpeedAssistExtend());
    }

    @Override
    public final void infoReady(DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4Assist.Listener4Model model) {
    }

    @Override
    public final void progressBlock(DownloadTask task, int blockIndex, long currentBlockOffset) {
    }

    @Override
    public final void progress(DownloadTask task, long currentOffset) {
    }

    @Override
    public final void blockEnd(DownloadTask task, int blockIndex, BlockInfo info) {
    }

    @Override
    public final void taskEnd(DownloadTask task, EndCause cause, @Nullable Exception realCause, @NonNull Listener4Assist.Listener4Model model) {
    }

    private static class Listener4WithSpeedModelCreator implements ListenerModelHandler.ModelCreator<Listener4SpeedAssistExtend.Listener4SpeedModel> {
        @Override
        public Listener4SpeedAssistExtend.Listener4SpeedModel create(int id) {
            return new Listener4SpeedAssistExtend.Listener4SpeedModel(id);
        }
    }
}
