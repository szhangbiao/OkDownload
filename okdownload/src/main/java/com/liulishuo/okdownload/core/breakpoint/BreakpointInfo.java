package com.liulishuo.okdownload.core.breakpoint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.Util;
import com.liulishuo.okdownload.core.download.DownloadStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BreakpointInfo {
    final int id;
    @NonNull
    final File parentFile;
    private final String url;
    private final DownloadStrategy.FilenameHolder filenameHolder;
    private final List<BlockInfo> blockInfoList;
    private final boolean taskOnlyProvidedParentPath;
    private String etag;
    @Nullable
    private File targetFile;
    private boolean chunked;

    public BreakpointInfo(int id, @NonNull String url, @NonNull File parentFile, @Nullable String filename) {
        this.id = id;
        this.url = url;
        this.parentFile = parentFile;
        this.blockInfoList = new ArrayList<>();

        if (Util.isEmpty(filename)) {
            filenameHolder = new DownloadStrategy.FilenameHolder();
            taskOnlyProvidedParentPath = true;
        } else {
            filenameHolder = new DownloadStrategy.FilenameHolder(filename);
            taskOnlyProvidedParentPath = false;
            targetFile = new File(parentFile, filename);
        }
    }

    BreakpointInfo(int id, @NonNull String url, @NonNull File parentFile, @Nullable String filename, boolean taskOnlyProvidedParentPath) {
        this.id = id;
        this.url = url;
        this.parentFile = parentFile;
        this.blockInfoList = new ArrayList<>();

        if (Util.isEmpty(filename)) {
            filenameHolder = new DownloadStrategy.FilenameHolder();
        } else {
            filenameHolder = new DownloadStrategy.FilenameHolder(filename);
        }
        this.taskOnlyProvidedParentPath = taskOnlyProvidedParentPath;
    }

    public int getId() {
        return id;
    }

    public void addBlock(BlockInfo blockInfo) {
        this.blockInfoList.add(blockInfo);
    }

    public boolean isChunked() {
        return this.chunked;
    }

    public void setChunked(boolean chunked) {
        this.chunked = chunked;
    }

    public boolean isLastBlock(int blockIndex) {
        return blockIndex == blockInfoList.size() - 1;
    }

    public boolean isSingleBlock() {
        return blockInfoList.size() == 1;
    }

    boolean isTaskOnlyProvidedParentPath() {
        return taskOnlyProvidedParentPath;
    }

    public BlockInfo getBlock(int blockIndex) {
        return blockInfoList.get(blockIndex);
    }

    public void resetInfo() {
        this.blockInfoList.clear();
        this.etag = null;
    }

    public void resetBlockInfos() {
        this.blockInfoList.clear();
    }

    public int getBlockCount() {
        return blockInfoList.size();
    }

    public long getTotalOffset() {
        long offset = 0;
        final Object[] blocks = blockInfoList.toArray();
        if (blocks != null) {
            for (Object block : blocks) {
                if (block instanceof BlockInfo) {
                    offset += ((BlockInfo) block).getCurrentOffset();
                }
            }
        }
        return offset;
    }

    public long getTotalLength() {
        if (isChunked()) return getTotalOffset();
        long length = 0;
        final Object[] blocks = blockInfoList.toArray();
        if (blocks != null) {
            for (Object block : blocks) {
                if (block instanceof BlockInfo) {
                    length += ((BlockInfo) block).getContentLength();
                }
            }
        }
        return length;
    }

    public @Nullable
    String getEtag() {
        return this.etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getUrl() {
        return url;
    }

    @Nullable
    public String getFilename() {
        return filenameHolder.get();
    }

    public DownloadStrategy.FilenameHolder getFilenameHolder() {
        return filenameHolder;
    }

    @Nullable
    public File getFile() {
        final String filename = this.filenameHolder.get();
        if (filename == null) return null;
        if (targetFile == null) targetFile = new File(parentFile, filename);
        return targetFile;
    }

    public BreakpointInfo copy() {
        final BreakpointInfo info = new BreakpointInfo(id, url, parentFile, filenameHolder.get(), taskOnlyProvidedParentPath);
        info.chunked = this.chunked;
        for (BlockInfo blockInfo : blockInfoList) {
            info.blockInfoList.add(blockInfo.copy());
        }
        return info;
    }

    public BreakpointInfo copyWithReplaceId(int replaceId) {
        final BreakpointInfo info = new BreakpointInfo(replaceId, url, parentFile, filenameHolder.get(), taskOnlyProvidedParentPath);
        info.chunked = this.chunked;
        for (BlockInfo blockInfo : blockInfoList) {
            info.blockInfoList.add(blockInfo.copy());
        }
        return info;
    }

    public void reuseBlocks(BreakpointInfo info) {
        blockInfoList.clear();
        blockInfoList.addAll(info.blockInfoList);
    }

    /**
     * You can use this method to replace url for using breakpoint info from another task.
     */
    public BreakpointInfo copyWithReplaceIdAndUrl(int replaceId, String newUrl) {
        final BreakpointInfo info = new BreakpointInfo(replaceId, newUrl, parentFile, filenameHolder.get(), taskOnlyProvidedParentPath);
        info.chunked = this.chunked;
        for (BlockInfo blockInfo : blockInfoList) {
            info.blockInfoList.add(blockInfo.copy());
        }
        return info;
    }

    public boolean isSameFrom(DownloadTask task) {
        if (!parentFile.equals(task.getParentFile())) {
            return false;
        }

        if (!url.equals(task.getUrl())) return false;

        final String otherFilename = task.getFilename();
        if (otherFilename != null && otherFilename.equals(filenameHolder.get())) return true;

        if (taskOnlyProvidedParentPath) {
            // filename is provided by response.
            if (!task.isFilenameFromResponse()) return false;

            return otherFilename == null || otherFilename.equals(filenameHolder.get());
        }

        return false;
    }

    @Override
    public String toString() {
        return "id[" + id + "]" + " url[" + url + "]" + " etag[" + etag + "]"
                + " taskOnlyProvidedParentPath[" + taskOnlyProvidedParentPath + "]"
                + " parent path[" + parentFile + "]" + " filename[" + filenameHolder.get() + "]"
                + " block(s):" + blockInfoList.toString();
    }
}
