package com.njscky.mapcollect;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

import com.esri.android.runtime.ArcGISRuntime;
import com.njscky.mapcollect.business.layer.DefaultLayerConfig;
import com.njscky.mapcollect.business.layer.LayerManager;
import com.njscky.mapcollect.util.AppExecutors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MapCollectApp extends Application {

    private static final String TAG = "MapCollectApp";
    private static MapCollectApp app;

    private LayerManager layerManager;

    public static MapCollectApp getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        ArcGISRuntime.setClientId("yhMRjFXTz6F38XD9");
        layerManager = new LayerManager(this, new DefaultLayerConfig());
        copyFontFileToApp();
    }

    public File getFontFile() {
        File fontDir = getDir("font", Context.MODE_PRIVATE);
        File outFile = new File(fontDir, "DroidSansFallback.ttf");
        return outFile;
    }

    private void copyFontFileToApp() {
        AppExecutors.DB.execute(new Runnable() {
            @Override
            public void run() {
                File fontFile = getFontFile();
                if (fontFile.exists()) {
                    return;
                } else {
                    try {
                        fontFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                InputStream inputStream = null;
                OutputStream outputStream = null;
                AssetManager assetManager = getAssets();
                try {
                    outputStream = new FileOutputStream(fontFile);
                    inputStream = assetManager.open("DroidSansFallback.ttf");
                    int data = -1;
                    while ((data = inputStream.read()) != -1) {
                        outputStream.write(data);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    assetManager.close();
                }
            }
        });
    }

    public LayerManager getLayerManager() {
        return layerManager;
    }
}
