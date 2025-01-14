package com.liulishuo.filedownloader.progress;

import com.liulishuo.filedownloader.CompatListenerAssist;
import com.liulishuo.filedownloader.DownloadTaskAdapter;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.core.Util;

import java.util.concurrent.atomic.AtomicLong;

public class ProgressAssist {

    static final int CALLBACK_SAFE_MIN_INTERVAL_BYTES = 1;
    static final int NO_ANY_PROGRESS_CALLBACK = -1;
    private static final long TOTAL_VALUE_IN_CHUNKED_RESOURCE = -1;
    private static final String TAG = "ProgressAssist";
    final AtomicLong sofarBytes;
    final AtomicLong incrementBytes;
    final SpeedCalculator speedCalculator;
    private final int maxProgressCount;
    long callbackMinIntervalBytes = CALLBACK_SAFE_MIN_INTERVAL_BYTES;

    public ProgressAssist(int maxProgressCount) {
        this(maxProgressCount, new SpeedCalculator());
    }

    public ProgressAssist(int maxProgressCount, SpeedCalculator speedCalculator) {
        this.maxProgressCount = maxProgressCount;
        this.speedCalculator = speedCalculator;
        sofarBytes = new AtomicLong(0);
        incrementBytes = new AtomicLong(0);
    }

    public void calculateCallbackMinIntervalBytes(final long contentLength) {
        if (maxProgressCount <= 0) {
            callbackMinIntervalBytes = NO_ANY_PROGRESS_CALLBACK;
        } else if (contentLength == TOTAL_VALUE_IN_CHUNKED_RESOURCE) {
            callbackMinIntervalBytes = CALLBACK_SAFE_MIN_INTERVAL_BYTES;
        } else {
            final long minIntervalBytes = contentLength / maxProgressCount;
            callbackMinIntervalBytes = minIntervalBytes <= 0 ? CALLBACK_SAFE_MIN_INTERVAL_BYTES : minIntervalBytes;
        }
        Util.d(TAG, "contentLength: " + contentLength + " callbackMinIntervalBytes: " + callbackMinIntervalBytes);
    }


    public void onProgress(DownloadTaskAdapter downloadTaskAdapter, long increaseBytes, CompatListenerAssist.CompatListenerAssistCallback callback) {
        speedCalculator.downloading(increaseBytes);
        final long sofar = sofarBytes.addAndGet(increaseBytes);
        if (canCallbackProgress(increaseBytes)) {
            callback.progress(downloadTaskAdapter, sofar, downloadTaskAdapter.getTotalBytesInLong());
        }
    }

    boolean canCallbackProgress(long increaseBytes) {
        if (callbackMinIntervalBytes == NO_ANY_PROGRESS_CALLBACK) return false;
        final long increment = incrementBytes.addAndGet(increaseBytes);
        if (increment >= callbackMinIntervalBytes) {
            incrementBytes.addAndGet(-callbackMinIntervalBytes);
            return true;
        }
        return false;
    }

    public long getSofarBytes() {
        return sofarBytes.get();
    }

    public void clearProgress() {
        Util.d(TAG, "clear progress, sofar: " + sofarBytes.get() + " increment: " + incrementBytes.get());
        sofarBytes.set(0);
        incrementBytes.set(0);
        speedCalculator.flush();
    }

    public void initSofarBytes(long soFarBytes) {
        Util.d(TAG, "init sofar: " + soFarBytes);
        sofarBytes.set(soFarBytes);
    }

    public long getSpeed() {
        return speedCalculator.getBytesPerSecondAndFlush() / 1024;
    }
}