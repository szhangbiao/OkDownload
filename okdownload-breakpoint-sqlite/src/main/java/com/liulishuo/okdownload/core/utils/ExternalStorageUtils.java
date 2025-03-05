package com.liulishuo.okdownload.core.utils;

import android.content.Context;

import java.io.File;

public class ExternalStorageUtils {

    public static File getDatabasePath(Context context) {
        // 获取外部 files 目录
        File externalFilesDir = context.getExternalFilesDir(null);
        if (externalFilesDir == null) {
            throw new IllegalStateException("External storage is not available");
        }

        // 获取 files 的父目录，即应用专属存储根目录
        File appDataDir = externalFilesDir.getParentFile();
        if (appDataDir == null || !appDataDir.exists()) {
            throw new IllegalStateException("App data directory not found");
        }

        // 创建 databases 同级目录
        File customDir = new File(appDataDir, "databases");
        if (!customDir.exists()) {
            boolean created = customDir.mkdirs();
            if (!created) {
                throw new IllegalStateException("Failed to create databases directory");
            }
        }
        return customDir;
    }
}
