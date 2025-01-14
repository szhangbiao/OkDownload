package com.liulishuo.okdownload.core.exception;

import java.io.IOException;

/**
 * Throw this exception only if the {@link com.liulishuo.okdownload.DownloadTask#isWifiRequired} is
 * {@code true} but the current network type is not Wifi.
 */
public class NetworkPolicyException extends IOException {
    public NetworkPolicyException() {
        super("Only allows downloading this task on the wifi network type!");
    }
}
