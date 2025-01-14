package com.liulishuo.filedownloader.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;

/**
 * No service in OkDownload, so this configuration class is useless.
 */
@Deprecated
@TargetApi(26)
public class ForegroundServiceConfig {

    private ForegroundServiceConfig() {
    }

    private Notification buildDefaultNotification(Context context) {
        return null;
    }

    public static class Builder {

        public Builder notificationId(int notificationId) {
            return this;
        }

        public Builder notificationChannelId(String notificationChannelId) {
            return this;
        }

        public Builder notificationChannelName(String notificationChannelName) {
            return this;
        }

        public Builder notification(Notification notification) {
            return this;
        }

        public Builder needRecreateChannelId(boolean needRecreateChannelId) {
            return this;
        }

        public ForegroundServiceConfig build() {
            return new ForegroundServiceConfig();
        }
    }
}