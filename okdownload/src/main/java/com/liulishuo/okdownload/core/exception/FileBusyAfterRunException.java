package com.liulishuo.okdownload.core.exception;

import java.io.IOException;

public class FileBusyAfterRunException extends IOException {
    private FileBusyAfterRunException() {
        super("File busy after run");
    }

    public static final FileBusyAfterRunException SIGNAL = new FileBusyAfterRunException() {
    };
}
