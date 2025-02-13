package com.yunqinglai.okdownload.util;

import android.os.Build;
import android.widget.ProgressBar;

public class ProgressUtil {

    public static void updateProgressToViewWithMark(ProgressBar bar, long currentOffset) {
        updateProgressToViewWithMark(bar, currentOffset, true);
    }

    public static void updateProgressToViewWithMark(ProgressBar bar, long currentOffset,
                                                    boolean anim) {
        if (bar.getTag() == null) return;

        final int shrinkRate = (int) bar.getTag();
        final int progress = (int) ((currentOffset) / shrinkRate);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bar.setProgress(progress, anim);
        } else {
            bar.setProgress(progress);
        }
    }

    public static void calcProgressToViewAndMark(ProgressBar bar, long offset, long total) {
        calcProgressToViewAndMark(bar, offset, total, true);
    }

    public static void calcProgressToViewAndMark(ProgressBar bar, long offset, long total,
                                                 boolean anim) {
        final int contentLengthOnInt = reducePrecision(total);
        final int shrinkRate = contentLengthOnInt == 0
                ? 1 : (int) (total / contentLengthOnInt);
        bar.setTag(shrinkRate);
        final int progress = (int) (offset / shrinkRate);


        bar.setMax(contentLengthOnInt);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bar.setProgress(progress, anim);
        } else {
            bar.setProgress(progress);
        }
    }

    private static int reducePrecision(long origin) {
        if (origin <= Integer.MAX_VALUE) return (int) origin;

        int shrinkRate = 10;
        long result = origin;
        while (result > Integer.MAX_VALUE) {
            result /= shrinkRate;
            shrinkRate *= 5;
        }

        return (int) result;
    }
}
