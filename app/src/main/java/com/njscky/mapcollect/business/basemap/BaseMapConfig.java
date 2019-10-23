package com.njscky.mapcollect.business.basemap;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class BaseMapConfig {

    private static final String SERVER_URL = "http://58.213.48.108/arcgis/rest/services/%E5%8D%97%E4%BA%AC%E5%9F%BA%E7%A1%80%E5%BA%95%E5%9B%BE2016/MapServer";
    private static final String MAPS_DIR_NAME = "offlinemap";
    private final Context context;

    public BaseMapConfig(Context context) {
        this.context = context.getApplicationContext();
    }

    public String getBaseMapsDir() {
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            dir = Environment.getExternalStorageDirectory();
        }

        return dir == null ? null : dir.getPath() + File.separator + MAPS_DIR_NAME;
    }

    public String getName() {
        return "南京基础底图";
    }

    public String getServerUrl() {
        return SERVER_URL;
    }
}
