package com.liulishuo.okdownload.core.listener.assist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.SparseArray;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;

public class Listener4SpeedAssistExtend implements Listener4Assist.AssistExtend, ListenerModelHandler.ModelCreator<Listener4SpeedAssistExtend.Listener4SpeedModel> {

    private Listener4SpeedCallback callback;

    public void setCallback(Listener4SpeedCallback callback) {
        this.callback = callback;
    }

    @Override
    public boolean dispatchInfoReady(DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4Assist.Listener4Model model) {
        if (callback != null) {
            callback.infoReady(task, info, fromBreakpoint, (Listener4SpeedModel) model);
        }
        return true;
    }

    @Override
    public boolean dispatchFetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes, @NonNull Listener4Assist.Listener4Model model) {
        final Listener4SpeedModel speedModel = (Listener4SpeedModel) model;
        speedModel.blockSpeeds.get(blockIndex).downloading(increaseBytes);
        speedModel.taskSpeed.downloading(increaseBytes);
        if (callback != null) {
            callback.progressBlock(task, blockIndex, model.blockCurrentOffsetMap.get(blockIndex), speedModel.getBlockSpeed(blockIndex));
            callback.progress(task, model.currentOffset, speedModel.taskSpeed);
        }
        return true;
    }

    @Override
    public boolean dispatchBlockEnd(DownloadTask task, int blockIndex, Listener4Assist.Listener4Model model) {
        final Listener4SpeedModel speedModel = (Listener4SpeedModel) model;
        speedModel.blockSpeeds.get(blockIndex).endTask();
        if (callback != null) {
            callback.blockEnd(task, blockIndex, model.info.getBlock(blockIndex), speedModel.getBlockSpeed(blockIndex));
        }
        return true;
    }

    @Override
    public boolean dispatchTaskEnd(DownloadTask task, EndCause cause, @Nullable Exception realCause, @NonNull Listener4Assist.Listener4Model model) {
        final Listener4SpeedModel speedModel = (Listener4SpeedModel) model;
        final SpeedCalculator speedCalculator;
        if (speedModel.taskSpeed != null) {
            speedCalculator = speedModel.taskSpeed;
            speedCalculator.endTask();
        } else {
            speedCalculator = new SpeedCalculator();
        }
        if (callback != null) {
            callback.taskEnd(task, cause, realCause, speedCalculator);
        }
        return true;
    }

    @Override
    public Listener4SpeedModel create(int id) {
        return new Listener4SpeedModel(id);
    }

    public interface Listener4SpeedCallback {
        void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4SpeedModel model);

        void progressBlock(@NonNull DownloadTask task, int blockIndex, long currentBlockOffset, @NonNull SpeedCalculator blockSpeed);

        void progress(@NonNull DownloadTask task, long currentOffset, @NonNull SpeedCalculator taskSpeed);

        void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info, @NonNull SpeedCalculator blockSpeed);

        void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull SpeedCalculator taskSpeed);
    }

    public static class Listener4SpeedModel extends Listener4Assist.Listener4Model {
        SpeedCalculator taskSpeed;
        SparseArray<SpeedCalculator> blockSpeeds;

        public Listener4SpeedModel(int id) {
            super(id);
        }

        public SpeedCalculator getTaskSpeed() {
            return taskSpeed;
        }

        public SpeedCalculator getBlockSpeed(int blockIndex) {
            return blockSpeeds.get(blockIndex);
        }

        @Override
        public void onInfoValid(@NonNull BreakpointInfo info) {
            super.onInfoValid(info);
            this.taskSpeed = new SpeedCalculator();
            this.blockSpeeds = new SparseArray<>();

            final int blockCount = info.getBlockCount();
            for (int i = 0; i < blockCount; i++) {
                blockSpeeds.put(i, new SpeedCalculator());
            }
        }
    }
}
