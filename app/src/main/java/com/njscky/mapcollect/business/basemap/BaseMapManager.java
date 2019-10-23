package com.njscky.mapcollect.business.basemap;

import android.content.Context;

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.core.geometry.Envelope;
import com.njscky.mapcollect.util.NetworkUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BaseMapManager {

    private static final String MAP_FILE_SUFFIX = ".tpk";
    private volatile static BaseMapManager instance;
    ArcGISTiledMapServiceLayer networkBaseMapLayer;
    ArcGISLocalTiledLayer localBaseMapLayer;

    private BaseMapConfig config;
    private Context context;
    private boolean isLoaded;

    private BaseMapManager(Context context) {
        this.context = context.getApplicationContext();
        config = new BaseMapConfig(this.context);
    }

    public static BaseMapManager getInstance(Context context) {
        if (instance == null) {
            synchronized (BaseMapManager.class) {
                if (instance == null) {
                    instance = new BaseMapManager(context);
                }
            }
        }
        return instance;
    }

    public void startLoad(MapView mapView) {
        if (isLoaded) {
            return;
        }
        List<String> localBaseMapFiles = getLocalBaseMapFilePathList();

        if (localBaseMapFiles == null || localBaseMapFiles.isEmpty()) {
            loadFromNetwork(mapView);
            return;
        }

        int size = localBaseMapFiles.size();

        if (size == 1) {
            loadFromLocalFile(mapView, localBaseMapFiles.get(0));
        } else {
            // TODO choose
        }
    }

    private List<String> getLocalBaseMapFilePathList() {
        String baseMapsDirPath = config.getBaseMapsDir();
        if (baseMapsDirPath == null) {
            return null;
        }
        List<String> rst = new ArrayList<>();

        File dir = new File(baseMapsDirPath);
        if (dir.exists()) {

            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(MAP_FILE_SUFFIX)) {
                    rst.add(file.getAbsolutePath());
                }
            }
        }
        return rst;
    }

    public void loadFromNetwork(MapView mapView) {
        if (NetworkUtil.hasNetwork(context)) {
            networkBaseMapLayer = new ArcGISTiledMapServiceLayer(config.getServerUrl());
            networkBaseMapLayer.setName(config.getName());
            mapView.addLayer(networkBaseMapLayer);

            Envelope env = new Envelope();
            env.setCoords(119642.008560884, 139699.14891334, 130225.363060926, 144519.536158281);
            mapView.setExtent(env);
            isLoaded = true;
        }
    }

    public void loadFromLocalFile(MapView mapView, String tpkFilePath) {
        // TODO
        localBaseMapLayer = new ArcGISLocalTiledLayer(tpkFilePath);
        localBaseMapLayer.setName(config.getName());
        mapView.addLayer(localBaseMapLayer);

        Envelope env = new Envelope();
        env.setCoords(119642.008560884, 139699.14891334, 130225.363060926, 150519.536158281);
        mapView.setExtent(env);
        isLoaded = true;
    }

    public boolean isNetworkBaseMap() {
        return networkBaseMapLayer != null;
    }


}
