package com.liulishuo.okdownload.core.breakpoint;

import java.util.concurrent.atomic.AtomicLong;

import static com.liulishuo.okdownload.core.Util.CHUNKED_CONTENT_LENGTH;

import androidx.annotation.IntRange;

public class BlockInfo {
    @IntRange(from = 0)
    private final long startOffset;
    @IntRange(from = 0)
    private final long contentLength;
    private final AtomicLong currentOffset;

    public BlockInfo(long startOffset, long contentLength) {
        this(startOffset, contentLength, 0);
    }

    public BlockInfo(long startOffset, long contentLength, @IntRange(from = 0) long currentOffset) {
        if (startOffset < 0 || (contentLength < 0 && contentLength != CHUNKED_CONTENT_LENGTH)
                || currentOffset < 0) {
            throw new IllegalArgumentException();
        }

        this.startOffset = startOffset;
        this.contentLength = contentLength;
        this.currentOffset = new AtomicLong(currentOffset);
    }

    public long getCurrentOffset() {
        return this.currentOffset.get();
    }

    public long getStartOffset() {
        return startOffset;
    }

    public long getRangeLeft() {
        return startOffset + currentOffset.get();
    }

    public long getContentLength() {
        return contentLength;
    }

    public long getRangeRight() {
        return startOffset + contentLength - 1;
    }

    public void increaseCurrentOffset(@IntRange(from = 1) long increaseLength) {
        this.currentOffset.addAndGet(increaseLength);
    }

    public void resetBlock() {
        this.currentOffset.set(0);
    }

    public BlockInfo copy() {
        return new BlockInfo(startOffset, contentLength, currentOffset.get());
    }

    @Override
    public String toString() {
        return "[" + startOffset + ", " + getRangeRight() + ")" + "-current:" + currentOffset;
    }
}
