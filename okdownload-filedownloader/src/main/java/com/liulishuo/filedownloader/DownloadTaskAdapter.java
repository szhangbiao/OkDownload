package com.liulishuo.filedownloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.SparseArray;

import com.liulishuo.filedownloader.progress.ProgressAssist;
import com.liulishuo.filedownloader.retry.RetryAssist;
import com.liulishuo.filedownloader.status.StatusAssist;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.core.Util;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadTaskAdapter implements BaseDownloadTask, BaseDownloadTask.IRunningTask {

    public static final int KEY_TASK_ADAPTER = Integer.MIN_VALUE;
    static final int DEFAULT_CALLBACK_PROGRESS_MIN_INTERVAL_MILLIS = 10;
    private static final int DEFAULT_CALLBACK_PROGRESS_COUNT = 100;
    private static final String TAG = "DownloadTaskAdapter";
    private final Object insureDownloadTaskAssembledLock = new Object();
    DownloadTask downloadTask;
    Builder builder;
    FileDownloadListener listener;
    StatusAssist statusAssist = new StatusAssist();
    private List<FinishListener> finishListeners = new ArrayList<>();
    private CompatListenerAdapter compatListener;
    private int autoRetryTimes;
    private int callbackProgressCount = DEFAULT_CALLBACK_PROGRESS_COUNT;
    private ProgressAssist progressAssist;
    private RetryAssist retryAssist;
    private volatile int attachKey;
    private volatile boolean isAddedToList;
    private Object tag;
    private SparseArray<Object> tagWithKey;

    public DownloadTaskAdapter(String url) {
        this.builder = new Builder();
        builder.url = url;
    }

    public ProgressAssist getProgressAssist() {
        return progressAssist;
    }

    public RetryAssist getRetryAssist() {
        return retryAssist;
    }

    public CompatListenerAdapter getCompatListener() {
        return compatListener;
    }

    @NonNull
    public DownloadTask getDownloadTask() {
        insureAssembleDownloadTask();
        return downloadTask;
    }

    public List<FinishListener> getFinishListeners() {
        return finishListeners;
    }

    @Override
    public BaseDownloadTask setMinIntervalUpdateSpeed(int minIntervalUpdateSpeedMs) {
        return this;
    }

    @Override
    public BaseDownloadTask setPath(String path) {
        builder.path = path;
        return this;
    }

    @Override
    public BaseDownloadTask setPath(String path, boolean pathAsDirectory) {
        builder.path = path;
        builder.pathAsDirectory = pathAsDirectory;
        return this;
    }

    @Override
    public BaseDownloadTask setListener(FileDownloadListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public BaseDownloadTask setCallbackProgressTimes(int callbackProgressCount) {
        this.callbackProgressCount = callbackProgressCount;
        progressAssist = new ProgressAssist(callbackProgressCount);
        return this;
    }

    @Override
    public BaseDownloadTask setCallbackProgressMinInterval(int minIntervalMillis) {
        builder.minIntervalMillisCallbackProgress = minIntervalMillis;
        return this;
    }

    @Override
    public BaseDownloadTask setCallbackProgressIgnored() {
        setCallbackProgressTimes(-1);
        return this;
    }

    @Override
    public BaseDownloadTask setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public BaseDownloadTask setTag(int key, Object tag) {
        if (tagWithKey == null) {
            tagWithKey = new SparseArray<>();
        }
        tagWithKey.put(key, tag);
        return this;
    }

    @Override
    public BaseDownloadTask setForceReDownload(boolean isForceReDownload) {
        builder.forceReDownload = isForceReDownload;
        return this;
    }

    @Override
    public BaseDownloadTask setFinishListener(FinishListener finishListener) {
        addFinishListener(finishListener);
        return this;
    }

    @Override
    public BaseDownloadTask addFinishListener(FinishListener finishListener) {
        if (finishListener == null) return this;
        if (finishListeners.contains(finishListener)) return this;
        finishListeners.add(finishListener);
        return this;
    }

    @Override
    public boolean removeFinishListener(FinishListener finishListener) {
        return finishListeners.remove(finishListener);
    }

    @Override
    public BaseDownloadTask setAutoRetryTimes(int autoRetryTimes) {
        this.autoRetryTimes = autoRetryTimes;
        if (autoRetryTimes > 0) {
            retryAssist = new RetryAssist(autoRetryTimes);
        }
        return this;
    }

    @Override
    public BaseDownloadTask addHeader(String name, String value) {
        builder.headerMap.put(name, value);
        return this;
    }

    @Override
    public BaseDownloadTask addHeader(String line) {
        String[] parsed = line.split(":");
        final String name = parsed[0].trim();
        final String value = parsed[1].trim();
        addHeader(name, value);
        return this;
    }

    @Override
    public BaseDownloadTask removeAllHeaders(String name) {
        builder.headerMap.remove(name);
        return this;
    }

    @Override
    public BaseDownloadTask setSyncCallback(boolean syncCallback) {
        builder.autoCallbackToUIThread = !syncCallback;
        return this;
    }

    @Override
    public BaseDownloadTask setWifiRequired(boolean isWifiRequired) {
        builder.isWifiRequired = isWifiRequired;
        return this;
    }

    @Override
    @Deprecated
    public int ready() {
        return asInQueueTask().enqueue();
    }

    @Override
    public InQueueTask asInQueueTask() {
        return new InQueueTaskImpl(this);
    }

    @Override
    public boolean reuse() {
        if (isRunning()) {
            Util.w(TAG, "This task[" + getId() + "] is running, if you want start the same task," + " please create a new one by FileDownloader#create");
            return false;
        }
        attachKey = 0;
        isAddedToList = false;
        return true;
    }

    @Override
    public boolean isUsing() {
        return statusAssist.isUsing();
    }

    @Override
    public boolean isRunning() {
        if (downloadTask == null) {
            return false;
        } else {
            return OkDownload.with().downloadDispatcher().isRunning(downloadTask);
        }
    }

    @Override
    public boolean isAttached() {
        return attachKey != 0;
    }

    @Override
    public int start() {
        insureAssembleDownloadTask();
        FileDownloadList.getImpl().addIndependentTask(this);
        downloadTask.enqueue(compatListener);
        return downloadTask.getId();
    }

    public void insureAssembleDownloadTask() {
        synchronized (insureDownloadTaskAssembledLock) {
            if (downloadTask != null) return;
        }
        downloadTask = builder.build();
        compatListener = CompatListenerAdapter.create(listener);
        if (progressAssist == null) {
            progressAssist = new ProgressAssist(callbackProgressCount);
        }
        statusAssist.setDownloadTask(downloadTask);
        downloadTask.addTag(KEY_TASK_ADAPTER, this);
    }

    @Override
    public boolean pause() {
        return cancel();
    }

    @Override
    public boolean cancel() {
        if (downloadTask == null) {
            return true;
        } else {
            return OkDownload.with().downloadDispatcher().cancel(downloadTask);
        }
    }

    @Override
    public int getId() {
        insureAssembleDownloadTask();
        return downloadTask.getId();
    }

    @Override
    public int getDownloadId() {
        return getId();
    }

    @Override
    public String getUrl() {
        return builder.url;
    }

    @Override
    public int getCallbackProgressTimes() {
        return callbackProgressCount;
    }

    @Override
    public int getCallbackProgressMinInterval() {
        return builder.minIntervalMillisCallbackProgress;
    }

    @Override
    public String getPath() {
        return builder.path;
    }

    @Override
    public boolean isPathAsDirectory() {
        return builder.pathAsDirectory;
    }

    @Override
    public String getFilename() {
        if (builder.pathAsDirectory) {
            return null;
        }
        return new File(builder.path).getName();
    }

    @Override
    public String getTargetFilePath() {
        return FileDownloadUtils.getTargetFilePath(builder.path, builder.pathAsDirectory, getFilename());
    }

    @Override
    public FileDownloadListener getListener() {
        return listener;
    }

    @Override
    public int getSoFarBytes() {
        return (int) getSoFarBytesInLong();
    }

    public long getSoFarBytesInLong() {
        if (downloadTask == null) {
            return 0L;
        }
        BreakpointInfo info = downloadTask.getInfo();
        if (info != null) {
            return info.getTotalOffset();
        }
        return 0L;
    }

    @Override
    public int getSmallFileSoFarBytes() {
        return (int) getLargeFileSoFarBytes();
    }

    @Override
    public long getLargeFileSoFarBytes() {
        if (progressAssist == null) {
            return 0;
        } else {
            return progressAssist.getSofarBytes();
        }
    }

    @Override
    public int getTotalBytes() {
        return (int) getTotalBytesInLong();
    }

    public long getTotalBytesInLong() {
        if (downloadTask == null) return 0L;
        BreakpointInfo info = downloadTask.getInfo();
        if (info != null) {
            return info.getTotalLength();
        }
        return 0L;
    }

    @Override
    public int getSmallFileTotalBytes() {
        return (int) getLargeFileTotalBytes();
    }

    @Override
    public long getLargeFileTotalBytes() {
        if (downloadTask == null) return 0L;
        BreakpointInfo info = downloadTask.getInfo();
        if (info != null) {
            return info.getTotalLength();
        }
        return 0;
    }

    @Override
    public int getSpeed() {
        return (int) progressAssist.getSpeed();
    }

    @Override
    public byte getStatus() {
        return statusAssist.getStatus();
    }

    @Override
    public boolean isForceReDownload() {
        return builder.forceReDownload;
    }

    @Override
    public Throwable getEx() {
        return compatListener.getListenerAssist().getException();
    }

    @Override
    public Throwable getErrorCause() {
        return getEx();
    }

    @Override
    public boolean isReusedOldFile() {
        return compatListener.getListenerAssist().isReuseOldFile();
    }

    @Override
    public Object getTag() {
        return tag;
    }

    @Override
    public Object getTag(int key) {
        if (tagWithKey == null) {
            return null;
        } else {
            return tagWithKey.get(key);
        }
    }

    @Override
    public boolean isContinue() {
        return isResuming();
    }

    @Override
    public boolean isResuming() {
        return compatListener.getListenerAssist().isResumable();
    }

    @Override
    public String getEtag() {
        return compatListener.getListenerAssist().getEtag();
    }

    @Override
    public int getAutoRetryTimes() {
        return autoRetryTimes;
    }

    @Override
    public int getRetryingTimes() {
        if (retryAssist != null) return retryAssist.getRetriedTimes() + 1;
        return 0;
    }

    @Override
    public boolean isSyncCallback() {
        return !builder.autoCallbackToUIThread;
    }

    @Override
    public boolean isLargeFile() {
        return listener instanceof FileDownloadLargeFileListener;
    }

    @Override
    public boolean isWifiRequired() {
        return builder.isWifiRequired;
    }

    // implement BaseDownload.IRunningTask

    @Override
    public BaseDownloadTask getOrigin() {
        return this;
    }

    @Override
    public ITaskHunter.IMessageHandler getMessageHandler() {
        return null;
    }

    @Override
    public boolean is(int id) {
        return getId() == id;
    }

    @Override
    public boolean is(FileDownloadListener listener) {
        return this.listener == listener;
    }

    @Override
    public boolean isOver() {
        return statusAssist.isOver();
    }

    @Override
    public int getAttachKey() {
        return attachKey;
    }

    @Override
    public void setAttachKeyByQueue(int key) {
        this.attachKey = key;
    }

    @Override
    public void setAttachKeyDefault() {
        final int key;
        if (getListener() != null) {
            key = getListener().hashCode();
        } else {
            key = hashCode();
        }
        this.attachKey = key;
    }

    @Override
    public boolean isMarkedAdded2List() {
        return isAddedToList;
    }

    @Override
    public void markAdded2List() {
        isAddedToList = true;
    }

    @Override
    public void free() {
    }

    @Override
    public void startTaskByQueue() {
    }

    @Override
    public void startTaskByRescue() {
    }

    @Override
    @Nullable
    public Object getPauseLock() {
        return null;
    }

    @Override
    public boolean isContainFinishListener() {
        return !finishListeners.isEmpty();
    }

    public void replaceListener(FileDownloadListener fileDownloadListener) {
        setListener(fileDownloadListener);
        if (downloadTask == null) {
            return;
        }

        compatListener = CompatListenerAdapter.create(listener);
        downloadTask.replaceListener(compatListener);
    }

    static final class Builder {
        String path;
        boolean pathAsDirectory;
        Map<String, String> headerMap = new HashMap<>();
        private String url;
        private int minIntervalMillisCallbackProgress = DEFAULT_CALLBACK_PROGRESS_MIN_INTERVAL_MILLIS;
        private boolean forceReDownload;
        private boolean isWifiRequired;
        private boolean autoCallbackToUIThread = true;

        DownloadTask build() {
            if (path == null) {
                path = FileDownloadUtils.getDefaultSaveFilePath(url);
            }
            @NonNull DownloadTask.Builder builder;
            if (pathAsDirectory) {
                builder = new DownloadTask.Builder(url, path, null);
            } else {
                builder = new DownloadTask.Builder(url, new File(path));
            }
            builder.setMinIntervalMillisCallbackProcess(minIntervalMillisCallbackProgress);
            builder.setPassIfAlreadyCompleted(!forceReDownload);
            builder.setWifiRequired(isWifiRequired);
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
            builder.setAutoCallbackToUIThread(autoCallbackToUIThread);
            return builder.build();
        }
    }

    static final class InQueueTaskImpl implements InQueueTask {

        final DownloadTaskAdapter downloadTaskAdapter;

        InQueueTaskImpl(DownloadTaskAdapter downloadTaskAdapter) {
            this.downloadTaskAdapter = downloadTaskAdapter;
        }

        @Override
        public int enqueue() {
            FileDownloadList.getImpl().addQueueTask(downloadTaskAdapter);
            return downloadTaskAdapter.getId();
        }
    }
}
