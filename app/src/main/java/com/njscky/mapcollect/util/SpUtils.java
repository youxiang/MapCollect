package com.njscky.mapcollect.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {
    private volatile static SpUtils instance;

    private SharedPreferences sp;

    private SpUtils(Context context) {
        sp = context.getSharedPreferences("mapcollect", Context.MODE_PRIVATE);
    }

    public static SpUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (SpUtils.class) {
                if (instance == null) {
                    instance = new SpUtils(context);
                }
            }
        }
        return instance;
    }

    /**
     * 根据类型获取流水号
     *
     * @param type
     * @return
     */
    public long getSerialNumber(String type) {
        long rst = sp.getLong(type, 0L);
        putSerialNumber(type, rst + 1);
        return rst;
    }

    private void putSerialNumber(String type, long number) {
        sp.edit().putLong(type, number).apply();
    }
}
