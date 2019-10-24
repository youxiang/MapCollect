package com.njscky.mapcollect.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

public class MainThreadExecutor implements Executor {

    public static final MainThreadExecutor INSTANCE = new MainThreadExecutor();

    private Handler handler;

    private MainThreadExecutor() {
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void execute(Runnable command) {
        handler.post(command);
    }
}
