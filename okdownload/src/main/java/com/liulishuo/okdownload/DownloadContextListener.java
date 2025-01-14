package com.liulishuo.okdownload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liulishuo.okdownload.core.cause.EndCause;

public interface DownloadContextListener {
    void taskEnd(@NonNull DownloadContext context, @NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, int remainCount);

    void queueEnd(@NonNull DownloadContext context);
}
