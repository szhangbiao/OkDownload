package com.liulishuo.okdownload.core.file;

import androidx.annotation.NonNull;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.breakpoint.DownloadStore;

import java.io.File;
import java.io.IOException;

public class ProcessFileStrategy {
    private final FileLock fileLock = new FileLock();

    @NonNull
    public MultiPointOutputStream createProcessStream(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @NonNull DownloadStore store) {
        return new MultiPointOutputStream(task, info, store);
    }

    public void completeProcessStream(@NonNull MultiPointOutputStream processOutputStream, @NonNull DownloadTask task) {
    }

    public void discardProcess(@NonNull DownloadTask task) throws IOException {
        // Remove target file.
        final File file = task.getFile();
        // Do nothing, because the filename hasn't found yet.
        if (file == null) return;

        if (file.exists() && !file.delete()) {
            throw new IOException("Delete file failed!");
        }
    }

    @NonNull
    public FileLock getFileLock() {
        return fileLock;
    }

    public boolean isPreAllocateLength(@NonNull DownloadTask task) {
        // if support seek, enable pre-allocate length.
        boolean supportSeek = OkDownload.with().outputStreamFactory().supportSeek();
        if (!supportSeek) return false;

        if (task.getSetPreAllocateLength() != null) return task.getSetPreAllocateLength();
        return true;
    }
}
