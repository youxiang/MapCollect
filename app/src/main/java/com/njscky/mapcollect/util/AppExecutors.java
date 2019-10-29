package com.njscky.mapcollect.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    public static final Executor DB = Executors.newCachedThreadPool();

    public static final Executor NETWORK = Executors.newCachedThreadPool();

    public static final Executor MAIN = MainThreadExecutor.INSTANCE;
}
