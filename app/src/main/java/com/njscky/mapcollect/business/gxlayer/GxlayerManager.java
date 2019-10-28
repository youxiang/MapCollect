package com.njscky.mapcollect.business.gxlayer;

import android.content.Context;

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.njscky.mapcollect.util.NetworkUtil;


public class GxlayerManager {
    private static final String SERVER_URL = "http://58.213.48.109/arcgis/rest/services/PSGX/MapServer";
    private volatile static GxlayerManager instance;
    private final Context context;
    ArcGISDynamicMapServiceLayer networkGxLayer;

    private boolean isLoaded;

    //管线背景图
    private GxlayerManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static GxlayerManager getInstance(Context context) {
        if (instance == null) {
            synchronized (GxlayerManager.class) {
                if (instance == null) {
                    instance = new GxlayerManager(context);
                }
            }
        }
        return instance;
    }

    public void startLoad(MapView mapView) {
        if (isLoaded) {
            return;
        }
        loadFromNetwork(mapView);
    }

    public void loadFromNetwork(MapView mapView) {
        if (NetworkUtil.hasNetwork(context)) {
            networkGxLayer = new ArcGISDynamicMapServiceLayer(SERVER_URL);
            networkGxLayer.setName("管线背景图");
            mapView.addLayer(networkGxLayer);
            isLoaded = true;
        }
    }


}
