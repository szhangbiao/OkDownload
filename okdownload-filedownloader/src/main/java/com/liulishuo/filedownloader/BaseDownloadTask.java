package com.liulishuo.filedownloader;

import androidx.annotation.Nullable;

import com.liulishuo.filedownloader.model.FileDownloadStatus;

/**
 * An atom download task.
 *
 * @see FileDownloader
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public interface BaseDownloadTask {

    int DEFAULT_CALLBACK_PROGRESS_MIN_INTERVAL_MILLIS = 10;

    /**
     * @param minIntervalUpdateSpeedMs The minimum interval millisecond for updating the downloading
     *                                 speed in downloading process(Status equal to progress).
     *                                 <p>
     *                                 Default: 5 ms.
     *                                 <p>
     *                                 If the value is less than or equal to 0, will not calculate
     *                                 the download speed in process.
     */
    BaseDownloadTask setMinIntervalUpdateSpeed(int minIntervalUpdateSpeedMs);

    /**
     * @param path            The absolute path for saving the download file.
     * @param pathAsDirectory {@code true}: if the {@code path} is absolute directory to store the
     *                        downloading file, and the {@code filename} will be found in
     *                        contentDisposition from the response as default, if can't find
     *                        contentDisposition,the {@code filename} will be generated by
     *                        with {@code url}.
     *                        </p>
     *                        {@code false}: if the {@code path} = (absolute directory/filename).
     * @see #isPathAsDirectory()
     * @see #getFilename()
     */
    BaseDownloadTask setPath(String path, boolean pathAsDirectory);

    /**
     * Ignore all callbacks of {@link FileDownloadListener#progress(BaseDownloadTask, int, int)}
     * during the entire process of downloading. This will optimize the performance.
     */
    BaseDownloadTask setCallbackProgressIgnored();

    /**
     * Set a tag associated with this task, not be used by internal.
     *
     * @param key The key of identifying the tag.
     *            If the key already exists, the old data will be replaced.
     * @param tag An Object to tag the task with
     */
    BaseDownloadTask setTag(int key, Object tag);

    /**
     * @deprecated Replace with {@link #addFinishListener(FinishListener)}
     */
    BaseDownloadTask setFinishListener(FinishListener finishListener);

    /**
     * Add the finish listener to listen when the task is finished.
     * <p>
     * This listener's method {@link FinishListener#over(BaseDownloadTask)} will be invoked in
     * Internal-Flow-Thread directly
     *
     * @param finishListener The finish listener.
     * @see FileDownloadStatus#isOver(int)
     */
    BaseDownloadTask addFinishListener(FinishListener finishListener);

    /**
     * Remove the finish listener from this task.
     *
     * @param finishListener The finish listener.
     * @return {@code true} if remove the {@code finishListener} successfully.
     * {@code false} otherwise.
     */
    boolean removeFinishListener(FinishListener finishListener);

    /**
     * Add the params to the request header.
     * <p>
     * <strong>Note:</strong> We have already handled Etag internal for guaranteeing tasks resuming
     * from the breakpoint, in other words, if the task has downloaded and got Etag, we will add the
     * 'If-Match' and the 'Range' K-V to its request header automatically.
     */
    BaseDownloadTask addHeader(String name, String value);

    /**
     * Add a field with the specified value to the request header.
     */
    BaseDownloadTask addHeader(String line);

    /**
     * Remove all fields in the request header.
     */
    BaseDownloadTask removeAllHeaders(String name);

    /**
     * Ready this task(For the task in a queue).
     * <p>
     * <strong>Note:</strong> If this task doesn't belong to a queue, what is just an isolated task,
     * you just need to invoke {@link #start()} to start this task, that's all. In other words, If
     * this task doesn't belong to a queue, you must not invoke this method or
     * {@link InQueueTask#enqueue()} method before invoke {@link #start()}, If you do that and if
     * there is the same listener object to start a queue in another thread, this task may be
     * assembled by the queue, in that case, when you invoke {@link #start()} manually to start this
     * task or this task is started by the queue, there is an exception buried in there, because
     * this task object is started two times without declare {@link #reuse()} : 1. you invoke
     * {@link #start()} manually; 2. the queue start this task automatically.
     *
     * @return downloadId the download identify.
     * @see FileDownloader#start(FileDownloadListener, boolean)
     * @deprecated please use {@link #asInQueueTask()} first and when you need to enqueue this task
     * to the global queue to make this task is ready to be assembled by the queue which makes up of
     * the same listener task, just invoke {@link InQueueTask#enqueue()}.
     */
    @Deprecated
    int ready();

    /**
     * Declare the task will be assembled by a queue which makes up of the same listener task.
     * <p>
     * <strong>Note:</strong> If you use {@link FileDownloadQueueSet} to start this task in a queue,
     * you don't need to invoke this method manually, it has been handled by
     * {@link FileDownloadQueueSet}.
     *
     * @return the task which is in a queue and exposes method {@link InQueueTask#enqueue()} to
     * enqueue this task to the global queue to ready for being assembled by the queue.
     */
    InQueueTask asInQueueTask();

    /**
     * Reuse this task withhold request params: path、url、header、isForceReDownloader、etc.
     * <p>
     * <strong>Note:</strong>If the task has been over({@link FileDownloadStatus#isOver(int)}), but
     * the over-message has not been handover to the listener, since the callback is asynchronous,
     * once your invoke this 'reuse' method, that message would be discard, for free the messenger.
     *
     * @return {@code true} if reuse this task successfully. {@code false} otherwise.
     */
    boolean reuse();

    /**
     * @return {@code true} if the this task already has downloading data, it means that this task
     * is running or has ran.
     * @see #isRunning()
     * @see #start()
     * @see #reuse()
     */
    boolean isUsing();

    /**
     * @return {@code true} if this task is running, in this case, this task isn't allow
     * to {@link #start()} again for this task object, and even not allow to {@link #reuse()}.
     * {@code false} this task maybe {@link #isUsing()} or in idle.
     * @see #isUsing()
     * @see #start()
     */
    boolean isRunning();

    /**
     * Whether this task has already attached to a listener / a serial-queue.
     *
     * @return {@code true} if this task is running, and it has already attached to the listener or
     * has already assembled to a serial-queue and would be started automatically when it is come to
     * its turn.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isAttached();

    /**
     * Start this task in parallel.
     *
     * @return The download id.
     */
    int start();

    /**
     * Why pause? not stop/cancel? because invoke this method(pause) will clear all data about this
     * task in memory, and stop the total processing about this task. but when you start the paused
     * task, it would be continue downloading from the breakpoint as default.
     *
     * @return {@code true}, if pause this task successfully, {@code false} otherwise this task has
     * already in over status before invoke this method(this case maybe occurred in high concurrent
     * situation).
     */
    boolean pause();

    /**
     * The {@link #pause()} also clear all data relate with this task in the memory, so please use
     * {@link #pause()} instead.
     *
     * @return {@code true} if cancel this task successfully.
     * @deprecated replace with {@link #pause()}
     */
    boolean cancel();

    /**
     * The downloading identify of this task, what is generated by {@link #getUrl()} and
     * {@link #getPath()} and {@link #isPathAsDirectory()} from
     *
     * @return The downloading identify of this task.
     */
    int getId();

    /**
     * @return The downloading identify of this task.
     * @deprecated Used {@link #getId()} instead.
     */
    int getDownloadId();

    /**
     * @return The Url.
     */
    String getUrl();

    /**
     * @return The maximum callback count of
     * {@link FileDownloadListener#progress(BaseDownloadTask, int, int)} during the entire process
     * of downloading.
     */
    int getCallbackProgressTimes();

    /**
     * Set the maximum callback count of
     * {@link FileDownloadListener#progress(BaseDownloadTask, int, int)} during the entire process
     * of downloading.
     * <p>
     * <strong>Note:</strong> this function will not work if the URL is refer to 'chucked' resource.
     *
     * @param callbackProgressCount The maximum callback count of
     *                              {@link FileDownloadListener#progress}
     *                              during the entire process of downloading.
     *                              <p>
     *                              Default value is 100, If the value less than or equal to 0, you
     *                              will not receive any callback of
     *                              {@link FileDownloadListener#progress}
     *                              .
     * @see #setCallbackProgressMinInterval(int)
     */
    BaseDownloadTask setCallbackProgressTimes(int callbackProgressCount);

    /**
     * @return The minimum time interval between each callback of
     * {@link FileDownloadListener#progress(BaseDownloadTask, int, int)} .
     */
    int getCallbackProgressMinInterval();

    /**
     * Set the minimum time interval between each callback of
     * {@link FileDownloadListener#progress(BaseDownloadTask, int, int)}.
     *
     * @param minIntervalMillis The minimum interval between each callback of
     *                          {@link FileDownloadListener#progress(BaseDownloadTask, int, int)}.
     *                          <p>
     *                          Unit: millisecond.
     *                          <p>
     *                          Default value is
     *                          {@link #DEFAULT_CALLBACK_PROGRESS_MIN_INTERVAL_MILLIS}.
     *                          <p>
     *                          Scope: [5, {@link Integer#MAX_VALUE}.
     * @see #setCallbackProgressTimes(int)
     */
    BaseDownloadTask setCallbackProgressMinInterval(int minIntervalMillis);

    // -------------- Another Operations ---------------------

    /**
     * @return If {@link #isPathAsDirectory()} is {@code true}: {@code path} is a absolute directory
     * to store the downloading file, and the {@code filename} will be found in contentDisposition
     * from the response as default, if can't find contentDisposition, the {@code filename} will be
     * generated with {@code url}. otherwise
     * {@code path} is the absolute path of the target file.
     */
    String getPath();

    /**
     * @param path {@code path} = (absolute directory/filename); and {@link #isPathAsDirectory()}
     *             assign to {@code false}.
     */
    BaseDownloadTask setPath(String path);
    // ------------------- get -----------------------

    /**
     * @return Whether the result of {@link #getPath()} is a directory.
     * @see #getPath()
     */
    boolean isPathAsDirectory();

    /**
     * @return If {@link #isPathAsDirectory()} is {@code true}, the {@code filename} will be found
     * in contentDisposition from the response as default, if can't find contentDisposition, the
     * {@code filename} will be generated with
     * {@code url}. It will be found before the callback of
     * {@link FileDownloadListener#connected(BaseDownloadTask, String, boolean, int, int)}.
     * </p>
     * If {@link #isPathAsDirectory()} is {@code false}, the {@code filename} will be found
     * immediately when you invoke {@link #setPath(String, boolean)} .
     */
    String getFilename();

    /**
     * @return The target file path to store the file.
     */
    String getTargetFilePath();

    /**
     * @return the downloading listener.
     */
    FileDownloadListener getListener();

    /**
     * @param listener For callback download status(pending,connected,progress,
     *                 blockComplete,retry,error,paused,completed,warn)
     */
    BaseDownloadTask setListener(FileDownloadListener listener);

    /**
     * @return The has already downloaded bytes so far.
     * @deprecated replace with {@link #getSmallFileSoFarBytes()}.
     */
    int getSoFarBytes();

    /**
     * This method will be used when the length of target file is less than or equal to 1.99G.
     *
     * @return The has already downloaded bytes so far.
     */
    int getSmallFileSoFarBytes();

    /**
     * This method will be used when the length of target file is more than 1.99G.
     *
     * @return The has already downloaded bytes so far.
     */
    long getLargeFileSoFarBytes();

    /**
     * @return The total bytes of the target file.
     * <p>
     * <strong>Note:</strong> this value will be valid
     * after {@link FileDownloadListener#connected(BaseDownloadTask, String, boolean, int, int)} or
     * it has already have in the database.
     * @deprecated replace with {@link #getSmallFileTotalBytes()}}
     */
    int getTotalBytes();

    /**
     * This method will be used when the length of target file is less than or equal to 1.99G.
     *
     * @return The total bytes of the target file.
     */
    int getSmallFileTotalBytes();

    /**
     * This method will be used when the length of target file is more than 1.99G.
     *
     * @return The total bytes of the target file.
     */
    long getLargeFileTotalBytes();

    /**
     * Get the downloading speed.
     * <p>
     * If the task is in the downloading process(status equal {@link FileDownloadStatus#progress}) :
     * The value is a real-time speed. it is calculated when the interval from the last calculation
     * more than {@link #setMinIntervalUpdateSpeed(int)} before each
     * {@link FileDownloadListener#progress(BaseDownloadTask, int, int)} call-back method.
     * <p/>
     * If this task is finished({@link FileDownloadStatus#isOver(int)}): The value is a average
     * speed. it is calculated from the entire downloading travel(connected, over).
     *
     * @return The downloading speed, Unit: KB/s.
     * @see #setMinIntervalUpdateSpeed(int)
     */
    int getSpeed();

    /**
     * @return The downloading status.
     * @see FileDownloadStatus
     */
    byte getStatus();

    /**
     * @return {@code true} if this task force re-download regard less the target file has already
     * exist.
     */
    boolean isForceReDownload();

    /**
     * Force re-downloading the file regardless the target file is exist.
     *
     * @param isForceReDownload If set to true, will not check whether the target file is exist.
     *                          <p>
     *                          Default: {@code false}.
     */
    BaseDownloadTask setForceReDownload(boolean isForceReDownload);

    /**
     * @deprecated Replaced with {@link #getErrorCause()}
     */
    Throwable getEx();

    /**
     * @return The error cause.
     */
    Throwable getErrorCause();

    /**
     * @return {@code true} if this task didn't start downloading really, because the target file
     * has already exist. {@code false} otherwise.
     * @see #isForceReDownload()
     */
    boolean isReusedOldFile();

    /**
     * @return The tag.
     */
    Object getTag();

    /**
     * Sets the tag associated with this task, not be used by internal.
     */
    BaseDownloadTask setTag(Object tag);

    /**
     * Returns the tag associated with this task and the specified key.
     *
     * @param key The key identifying the tag.
     * @return the object stored in this take as a tag, or {@code null} if not
     * set.
     * @see #setTag(int, Object)
     * @see #getTag()
     */
    Object getTag(int key);

    /**
     * @deprecated Use {@link #isResuming()} instead.
     */
    boolean isContinue();

    /**
     * @return {@code true} if this task is resuming from the breakpoint, this value is valid
     * after {@link FileDownloadListener#connected(BaseDownloadTask, String, boolean, int, int)}.
     */
    boolean isResuming();

    /**
     * @return The ETag from the response's header, this value is valid
     * after {@link FileDownloadListener#connected(BaseDownloadTask, String, boolean, int, int)}
     */
    String getEtag();

    /**
     * @return The number of times has set to retry when occur any error.
     * @see #setAutoRetryTimes(int)
     */
    int getAutoRetryTimes();

    /**
     * Set the number of times to retry when encounter any error, except
     * {@link com.liulishuo.filedownloader.exception.FileDownloadGiveUpRetryException}.
     *
     * @param autoRetryTimes The retry times, default 0.
     */
    BaseDownloadTask setAutoRetryTimes(int autoRetryTimes);

    /**
     * @return The currently number of times of retry.this value is valid
     * after {@link FileDownloadListener#retry(BaseDownloadTask, Throwable, int, int)}
     */
    int getRetryingTimes();

    /**
     * @return {@code true} if the methods of {@link FileDownloadListener} will be invoked directly
     * in message-thread for this task, {@code false} all methods of {@link FileDownloadListener}
     * will
     * be post to the UI thread for this task.
     * @see #setSyncCallback(boolean)
     */
    boolean isSyncCallback();

    /**
     * @param syncCallback {@code true} FileDownloader will invoke methods of
     *                     {@link FileDownloadListener} directly on the download thread(isn't in the
     *                     main thread).
     */
    BaseDownloadTask setSyncCallback(boolean syncCallback);

    /**
     * @return {@code true} if the length of target file is more than or equal to 2G.
     * @see #getLargeFileSoFarBytes()
     * @see #getLargeFileTotalBytes()
     */
    boolean isLargeFile();

    /**
     * @return {@code true} if this task has been set only allows downloading on the wifi network
     * type.
     */
    boolean isWifiRequired();

    /**
     * Set whether this task only allows downloading on the wifi network type. Default {@code false}
     * <p>
     * <strong>Note:</strong> If {@code isWifiRequired} is {@code true}, FileDownloader will check
     * the network type every time after fetch less than or equal to 4096 bytes data from the
     * network, what will result in slowing the download speed slightly.
     * <p>
     * <strong>Permission:</strong> If {@code isWifiRequired} is {@code true}, You need declare the
     * permission {@link android.Manifest.permission#ACCESS_NETWORK_STATE} in your manifest, let
     * FileDownloader has permission to check the network type in downloading, if you start this
     *
     * @param isWifiRequired {@code true} This task only allow to download on the wifi network type.
     */
    BaseDownloadTask setWifiRequired(boolean isWifiRequired);

    /**
     * Declare the task will be assembled by a queue which makes up of the same listener task.
     */
    interface InQueueTask {
        /**
         * Enqueue the task to the global queue, what is the only way for the task to ready to be
         * assembled by a queue.
         * <p>
         * <strong>Note:</strong> Only if this task belongs to a queue, you need to invoke this
         * method.
         *
         * @return the download task identify.
         */
        int enqueue();
    }

    @SuppressWarnings("UnusedParameters")
    interface FinishListener {
        /**
         * Will be invoked when the {@code task} is over({@link FileDownloadStatus#isOver(int)}).
         * This method will be invoked in Non-UI-Thread and this thread is controlled by
         *
         * @param task is over, the status would be one of below:
         *             {@link FileDownloadStatus#completed}、{@link FileDownloadStatus#warn}、
         *             {@link FileDownloadStatus#error}、{@link FileDownloadStatus#paused}.
         * @see FileDownloadStatus#isOver(int)
         */
        void over(BaseDownloadTask task);
    }

    /**
     * The running task.
     * <p>
     * Used in internal.
     */
    interface IRunningTask {
        /**
         * @return The origin one.
         */
        BaseDownloadTask getOrigin();

        /**
         * @return The message handler of this task.
         */
        ITaskHunter.IMessageHandler getMessageHandler();

        /**
         * @return {@code true} the id of the task is equal to the {@code id}.
         */
        boolean is(int id);

        /**
         * @return {@code true} the listener of the task is equal to the {@code listener}.
         */
        boolean is(FileDownloadListener listener);

        /**
         * @return {@code true} if the task has already finished.
         */
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        boolean isOver();

        /**
         * When the task is running, it must attach a key. if this task is running in a queue
         * downloading tasks serial, the attach key is equal to the hash code of the callback of
         * queue's handler, otherwise the attach key is equal to the hash code of the listener.
         *
         * @return The attached key, if this task in a queue, the attached key is the hash code of
         * the listener.
         */
        int getAttachKey();

        /**
         * Set this task attach to the {@code key} by the queue. In this case, this task must be a
         * task which belong to a queue, and will be started automatically by the queue.
         *
         * @param key The attached key for this task.
         */
        void setAttachKeyByQueue(int key);

        /**
         * Set this task to the default key. In this case, this task must be a task which is a
         * isolated task.
         */
        void setAttachKeyDefault();

        /**
         * @return {@code true} the task has already added to the downloading list.
         */
        boolean isMarkedAdded2List();

        /**
         * Mark the task has already added to the downloading list.
         */
        void markAdded2List();

        /**
         * Free the task.
         */
        void free();

        /**
         * Start the task by the queue handler.
         */
        void startTaskByQueue();

        /**
         * Start the task just because this task can't started by pass, and now, we try to rescue
         * this task and start it.
         * <p>
         * Currently, this rescue is occurred when the filedownloader service connected.
         */
        void startTaskByRescue();

        /**
         * Get the object as a lock for synchronized with the pause area.
         *
         * @return the object as a lock for synchronized with the pause area.
         */
        @Nullable
        Object getPauseLock();

        /**
         * Whether contain finish listener or not.
         *
         * @return {@code true} if there is finish listener on the task.
         */
        boolean isContainFinishListener();
    }

    /**
     * The callback for the life cycle of the task.
     */
    interface LifeCycleCallback {
        /**
         * The task begin working.
         */
        void onBegin();

        /**
         * The task is running, and during the downloading processing, when the status of the task
         * is changed will trigger to callback this method.
         */
        void onIng();

        /**
         * The task is end.
         */
        void onOver();
    }
}
