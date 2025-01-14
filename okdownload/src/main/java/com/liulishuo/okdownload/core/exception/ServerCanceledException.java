package com.liulishuo.okdownload.core.exception;

import java.io.IOException;

public class ServerCanceledException extends IOException {
    private final int responseCode;

    public ServerCanceledException(int responseCode, long currentOffset) {
        super("Response code can't handled on internal " + responseCode + " with current offset " + currentOffset);
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
