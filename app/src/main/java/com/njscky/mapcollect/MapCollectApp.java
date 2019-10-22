package com.njscky.mapcollect;

import android.app.Application;

public class MapCollectApp extends Application {

    private static MapCollectApp app;

    public static MapCollectApp getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
