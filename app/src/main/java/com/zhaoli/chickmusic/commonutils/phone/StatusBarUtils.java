package com.zhaoli.chickmusic.commonutils.phone;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

/**
 * Created by zhaoli on 2015/10/26.
 * 状态栏工具，处理魅族（FlymeOS4.x/Android4.4.4）特殊情况
 */
public class StatusBarUtils {
    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        if (isFlymeOs4x()) {
            statusBarHeight = 2 * statusBarHeight;
        }
        return statusBarHeight;
    }

    private static boolean isFlymeOs4x() {
        String sysVersion = Build.VERSION.RELEASE;
        if ("4.4.4".equals(sysVersion)) {
            String sysIncrement = Build.VERSION.INCREMENTAL;
            String displayId = Build.DISPLAY;
            if (!TextUtils.isEmpty(sysIncrement)) {
                return sysIncrement.contains("Flyme_OS_4");
            } else {
                return displayId.contains("Flyme OS 4");
            }
        }
        return false;
    }
}
