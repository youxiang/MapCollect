package com.njscky.mapcollect;

import android.app.Application;

import com.esri.android.runtime.ArcGISRuntime;


public class MapCollectApp extends Application {

    private static MapCollectApp app;

    public static MapCollectApp getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        ArcGISRuntime.setClientId("yhMRjFXTz6F38XD9");
    }
}
