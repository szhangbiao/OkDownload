package com.yunqinglai.okdownload;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;
import com.yunqinglai.okdownload.base.BaseSampleActivity;
import com.yunqinglai.okdownload.util.DemoUtil;
import com.yunqinglai.okdownload.util.EachBlockProgressUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * On this demo we will download a task and display each block progress for it.
 */
public class EachBlockProgressActivity extends BaseSampleActivity {

    private static final String TAG = "EachBlockProgress";
    private DownloadTask task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_block_progress);

        initEachBlockProgress(findViewById(R.id.statusTv),
                findViewById(R.id.extInfoTv),
                findViewById(R.id.actionView),
                findViewById(R.id.actionTv),
                findViewById(R.id.startSameTaskView),
                findViewById(R.id.startSameFileView),
                findViewById(R.id.taskPb),
                findViewById(R.id.block0Pb),
                findViewById(R.id.block1Pb),
                findViewById(R.id.block2Pb),
                findViewById(R.id.block3Pb),
                findViewById(R.id.taskTitleTv),
                findViewById(R.id.block0TitleTv),
                findViewById(R.id.block1TitleTv),
                findViewById(R.id.block2TitleTv),
                findViewById(R.id.block3TitleTv),
                findViewById(R.id.taskSpeedTv),
                findViewById(R.id.block0SpeedTv),
                findViewById(R.id.block1SpeedTv),
                findViewById(R.id.block2SpeedTv),
                findViewById(R.id.block3SpeedTv));
    }

    @Override
    public int titleRes() {
        return R.string.each_block_progress_title;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null) task.cancel();
    }

    private void initEachBlockProgress(TextView statusTv,
                                       TextView extInfoTv,
                                       View actionView,
                                       TextView actionTv,
                                       View startSameTaskView,
                                       View startSameFileView,
                                       ProgressBar taskPb,
                                       ProgressBar block0Pb,
                                       ProgressBar block1Pb,
                                       ProgressBar block2Pb,
                                       ProgressBar block3Pb,
                                       TextView taskTitleTv,
                                       TextView block0TitleTv,
                                       TextView block1TitleTv,
                                       TextView block2TitleTv,
                                       TextView block3TitleTv,
                                       TextView taskSpeedTv,
                                       TextView block0SpeedTv,
                                       TextView block1SpeedTv,
                                       TextView block2SpeedTv,
                                       TextView block3SpeedTv) {
        initTask();
        initStatus(statusTv,
                taskPb, block0Pb, block1Pb, block2Pb, block3Pb,
                taskTitleTv, block0TitleTv, block1TitleTv, block2TitleTv, block3TitleTv);
        initAction(statusTv, extInfoTv,
                actionView, actionTv,
                startSameTaskView, startSameFileView,
                taskPb, block0Pb, block1Pb, block2Pb, block3Pb,
                taskTitleTv, block0TitleTv, block1TitleTv, block2TitleTv, block3TitleTv,
                taskSpeedTv, block0SpeedTv, block1SpeedTv, block2SpeedTv, block3SpeedTv);
    }

    private void initTask() {
        task = createTask();
    }

    private DownloadTask createTask() {
        final String url = "https://cdn.llscdn.com/yy/files/xs8qmxn8-lls-LLS-5.8-800-20171207-111607.apk";
        return createTask(url);
    }

    private DownloadTask createSameFileAnotherUrlTask() {
        final String anotherUrl = "http://dldir1.qq.com/weixin/android/seixin6516android1120.apk";
        return createTask(anotherUrl);
    }

    private DownloadTask createTask(String url) {
        final String filename = "each-block-progress-test";
        final File parentFile = DemoUtil.getParentFile(this);
        return new DownloadTask.Builder(url, parentFile)
                .setFilename(filename)
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(64)
                // ignore the same task has already completed in the past.
                .setPassIfAlreadyCompleted(false)
                .build();
    }


    private void initStatus(TextView statusTv,
                            ProgressBar taskPb,
                            ProgressBar block0Pb, ProgressBar block1Pb,
                            ProgressBar block2Pb, ProgressBar block3Pb,
                            TextView taskTitleTv,
                            TextView block0TitleTv, TextView block1TitleTv,
                            TextView block2TitleTv, TextView block3TitleTv) {
        final StatusUtil.Status status = StatusUtil.getStatus(task);
        statusTv.setText(status.toString());
        if (status == StatusUtil.Status.COMPLETED) {
            taskPb.setProgress(taskPb.getMax());
        }

        final BreakpointInfo info = StatusUtil.getCurrentInfo(task);
        if (info != null) {
            Log.d(TAG, "init status with: " + info.toString());
            EachBlockProgressUtil.initTitle(info, taskTitleTv, block0TitleTv, block1TitleTv, block2TitleTv, block3TitleTv);
            EachBlockProgressUtil.initProgress(info, taskPb, block0Pb, block1Pb, block2Pb, block3Pb);
        }
    }

    private void initAction(TextView statusTv,
                            TextView extInfoTv,
                            View actionView,
                            TextView actionTv,
                            View startSameTaskView,
                            View startSameFileView,
                            ProgressBar taskPb,
                            ProgressBar block0Pb,
                            ProgressBar block1Pb,
                            ProgressBar block2Pb,
                            ProgressBar block3Pb,
                            TextView taskTitleTv,
                            TextView block0TitleTv,
                            TextView block1TitleTv,
                            TextView block2TitleTv,
                            TextView block3TitleTv,
                            TextView taskSpeedTv,
                            TextView block0SpeedTv,
                            TextView block1SpeedTv,
                            TextView block2SpeedTv,
                            TextView block3SpeedTv) {

        actionTv.setText(R.string.start);

        // start or cancel
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean started = task.getTag() != null;
                if (started) {
                    // to cancel
                    task.cancel();
                } else {
                    actionTv.setText(R.string.cancel);
                    // to start
                    startTask(statusTv, actionTv,
                            taskPb,
                            block0Pb, block1Pb, block2Pb, block3Pb,
                            taskTitleTv, block0TitleTv, block1TitleTv, block2TitleTv, block3TitleTv,
                            taskSpeedTv,
                            block0SpeedTv, block1SpeedTv, block2SpeedTv, block3SpeedTv);
                    // mark
                    task.setTag("mark-task-started");
                }
            }
        });
        // start same task -- same-busy
        startSameTaskView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean started = task.getTag() != null;
                if (!started) return;
                final DownloadTask task = createTask();
                task.enqueue(EachBlockProgressUtil.createSampleListener(extInfoTv));
            }
        });
        // start same file -- file-busy
        startSameFileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean started = task.getTag() != null;
                if (!started) return;
                final DownloadTask sameFileAnotherUrlTask = createSameFileAnotherUrlTask();
                sameFileAnotherUrlTask.enqueue(EachBlockProgressUtil.createSampleListener(extInfoTv));
            }
        });
    }

    private void startTask(final TextView statusTv, final TextView actionTv,
                           final ProgressBar taskPb,
                           final ProgressBar block0Pb, final ProgressBar block1Pb,
                           final ProgressBar block2Pb, final ProgressBar block3Pb,
                           final TextView taskTitleTv,
                           final TextView block0TitleTv, final TextView block1TitleTv,
                           final TextView block2TitleTv, final TextView block3TitleTv,
                           final TextView taskSpeedTv,
                           final TextView block0SpeedTv, final TextView block1SpeedTv,
                           final TextView block2SpeedTv, final TextView block3SpeedTv) {
        task.enqueue(new DownloadListener4WithSpeed() {

            @Override
            public void taskStart(@NonNull DownloadTask task) {
                statusTv.setText(R.string.task_start);
            }

            @Override
            public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {
                EachBlockProgressUtil.initTitle(info, taskTitleTv, block0TitleTv, block1TitleTv, block2TitleTv, block3TitleTv);
                EachBlockProgressUtil.initProgress(info, taskPb, block0Pb, block1Pb, block2Pb, block3Pb);
            }

            @Override
            public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaders) {
                final String status = "connectStart " + blockIndex + " " + requestHeaders;
                statusTv.setText(status);
            }

            @Override
            public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaders) {
                final String status = "connectEnd " + blockIndex + " " + responseCode + " " + responseHeaders;
                statusTv.setText(status);
            }

            @Override
            public void progressBlock(@NonNull DownloadTask task, int blockIndex, long currentBlockOffset, @NonNull SpeedCalculator blockSpeed) {
                final ProgressBar progressBar = EachBlockProgressUtil.getProgressBar(blockIndex, block0Pb, block1Pb, block2Pb, block3Pb);
                EachBlockProgressUtil.updateProgress(progressBar, currentBlockOffset);
                final TextView speedTv = EachBlockProgressUtil.getSpeedTv(blockIndex, block0SpeedTv, block1SpeedTv, block2SpeedTv, block3SpeedTv);
                if (speedTv != null) speedTv.setText(blockSpeed.speed());
            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, @NonNull SpeedCalculator taskSpeed) {
                statusTv.setText(R.string.fetch_progress);
                EachBlockProgressUtil.updateProgress(taskPb, currentOffset);
                taskSpeedTv.setText(taskSpeed.speed());
            }

            @Override
            public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info, @NonNull SpeedCalculator blockSpeed) {
                final TextView speedTv = EachBlockProgressUtil.getSpeedTv(blockIndex, block0SpeedTv, block1SpeedTv, block2SpeedTv, block3SpeedTv);
                if (speedTv != null) speedTv.setText(blockSpeed.averageSpeed());
            }


            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull SpeedCalculator taskSpeed) {
                statusTv.setText(cause.toString());
                taskSpeedTv.setText(taskSpeed.averageSpeed());
                actionTv.setText(R.string.start);
                // mark
                task.setTag(null);
            }
        });
    }
}
