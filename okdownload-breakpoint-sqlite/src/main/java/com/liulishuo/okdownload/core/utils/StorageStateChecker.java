package com.liulishuo.okdownload.core.utils;

import android.content.Context;
import android.os.Build;
import android.os.StatFs;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class StorageStateChecker {
    private static final String TAG = "StorageStateChecker";

    /**
     * 检查应用内部存储是否可用
     *
     * @param context Context
     * @return boolean 存储是否可用
     */
    public static boolean isInternalStorageAvailable(@NonNull Context context) {
        try {
            // 1. 检查应用内部存储目录是否存在且可访问
            File internalDir = context.getFilesDir();
            if (!internalDir.exists() || !internalDir.canRead() || !internalDir.canWrite()) {
                Log.e(TAG, "Internal storage directory is not accessible: " + internalDir.getAbsolutePath());
                return false;
            }

            // 2. 尝试写入测试文件
            File testFile = new File(internalDir, "storage_test.tmp");
            try {
                if (testFile.exists()) {
                    if (!testFile.delete()) {
                        Log.e(TAG, "Failed to delete existing test file");
                        return false;
                    }
                }

                if (!testFile.createNewFile()) {
                    Log.e(TAG, "Failed to create test file");
                    return false;
                }

                // 写入测试内容
                try (FileWriter writer = new FileWriter(testFile)) {
                    writer.write("test");
                }

                // 读取测试内容
                String content = "";
                try (BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
                    content = reader.readLine();
                }

                // 删除测试文件
                if (!testFile.delete()) {
                    Log.w(TAG, "Failed to delete test file after test");
                }

                if (!"test".equals(content)) {
                    Log.e(TAG, "Failed to verify written content");
                    return false;
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to write/read test file", e);
                return false;
            }

            // 3. 检查可用空间
            StatFs stats = new StatFs(internalDir.getPath());
            long freeSpace = stats.getAvailableBlocksLong() * stats.getBlockSizeLong();
            if (freeSpace <= 0) {
                Log.e(TAG, "No available space in internal storage");
                return false;
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error checking internal storage state", e);
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String getFilePermissions(File file) {
        try {
            Process process = new ProcessBuilder("ls", "-l", file.getAbsolutePath())
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start();
            if (process.waitFor(10, TimeUnit.SECONDS)) {
                try (BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                    String line = reader.readLine();
                    if (line != null) {
                        String[] parts = line.split(" ");
                        return parts.length > 0 ? parts[0] : "unknown";
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting file permissions", e);
        }
        return "unknown";
    }
}
