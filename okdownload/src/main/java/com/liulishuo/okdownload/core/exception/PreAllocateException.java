package com.liulishuo.okdownload.core.exception;

import java.io.IOException;

public class PreAllocateException extends IOException {
    private final long requireSpace;
    private final long freeSpace;

    public PreAllocateException(long requireSpace, long freeSpace) {
        super("There is Free space less than Require space: " + freeSpace + " < " + requireSpace);
        this.requireSpace = requireSpace;
        this.freeSpace = freeSpace;
    }

    public long getRequireSpace() {
        return requireSpace;
    }

    public long getFreeSpace() {
        return freeSpace;
    }
}
