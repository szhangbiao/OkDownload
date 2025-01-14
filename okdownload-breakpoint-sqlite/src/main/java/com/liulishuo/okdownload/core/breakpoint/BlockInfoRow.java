package com.liulishuo.okdownload.core.breakpoint;

import android.annotation.SuppressLint;
import android.database.Cursor;

import static com.liulishuo.okdownload.core.breakpoint.BreakpointSQLiteKey.CONTENT_LENGTH;
import static com.liulishuo.okdownload.core.breakpoint.BreakpointSQLiteKey.CURRENT_OFFSET;
import static com.liulishuo.okdownload.core.breakpoint.BreakpointSQLiteKey.HOST_ID;
import static com.liulishuo.okdownload.core.breakpoint.BreakpointSQLiteKey.START_OFFSET;


@SuppressLint("Range")
public class BlockInfoRow {
    private final int breakpointId;

    private final long startOffset;
    private final long contentLength;
    private final long currentOffset;

    public BlockInfoRow(Cursor cursor) {
        this.breakpointId = cursor.getInt(cursor.getColumnIndex(HOST_ID));
        this.startOffset = cursor.getInt(cursor.getColumnIndex(START_OFFSET));
        this.contentLength = cursor.getInt(cursor.getColumnIndex(CONTENT_LENGTH));
        this.currentOffset = cursor.getInt(cursor.getColumnIndex(CURRENT_OFFSET));
    }

    public int getBreakpointId() {
        return breakpointId;
    }

    public long getStartOffset() {
        return startOffset;
    }

    public long getContentLength() {
        return contentLength;
    }

    public long getCurrentOffset() {
        return currentOffset;
    }

    public BlockInfo toInfo() {
        return new BlockInfo(startOffset, contentLength, currentOffset);
    }
}
