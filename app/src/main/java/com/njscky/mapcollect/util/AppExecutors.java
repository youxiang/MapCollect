package com.njscky.mapcollect.util;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {

    public static final ExecutorService DB = Executors.newFixedThreadPool(4);

    public static final ExecutorService MULTI_TASK = Executors.newFixedThreadPool(4);

    public static final ExecutorService NETWORK = Executors.newFixedThreadPool(4);

    public static final Executor MAIN = MainThreadExecutor.INSTANCE;
}
