package com.njscky.mapcollect.business.layer;

import android.content.Context;
import android.util.Log;

import com.esri.android.map.Layer;
import com.esri.android.map.TiledLayer;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.njscky.mapcollect.util.NetworkUtil;

import java.io.File;

public class LayerManager {

    private static final String TAG = "LayerManager";
    private final Context context;

    private LayerConfig config;

    // 底图
    private TiledLayer baseMapLayer;
    // 管线layer
    private ArcGISDynamicMapServiceLayer gxLayer;

    public LayerManager(Context context, LayerConfig config) {
        this.config = config;
        this.context = context.getApplicationContext();
    }

    public void load(LayerListener callback) {
        loadBaseMapLayer(callback);
//        loadGXLayer(callback);
    }


    private void loadBaseMapLayer(LayerListener callback) {
        if (baseMapLayer == null || baseMapLayer.isRecycled()) {
            LayerParameter parameter = config.baseMapParameter();
            if (hasNoLocalTpks(parameter.localDir)) {
                loadFromNetwork(callback);
            } else {
                loadLocalTpks(callback);
            }

        }
    }

    private void loadLocalTpks(LayerListener callback) {
        // TODO
    }

    private void loadFromNetwork(LayerListener callback) {
        if (NetworkUtil.hasNetwork(context)) {
            baseMapLayer = new ArcGISTiledMapServiceLayer(config.baseMapParameter().serverUrl);
            baseMapLayer.setName(config.baseMapParameter().name);
            baseMapLayer.setOnStatusChangedListener(new OnStatusChangedListener() {
                @Override
                public void onStatusChanged(Object o, STATUS status) {
                    Log.i(TAG, "baseMapLayer#onStatusChanged: " + status);
                }
            });
            if (callback != null) {
                callback.onBaseLayerLoaded(baseMapLayer);
            }
        }
    }

    private boolean hasNoLocalTpks(File dir) {
        if (dir == null || !dir.isDirectory()) {
            return true;
        }
        for (File file : dir.listFiles()) {
            if (file.getAbsolutePath().endsWith(".tpk")) {
                return false;
            }
        }
        return true;
    }

    private void loadGXLayer(LayerListener callback) {
        if (gxLayer == null || gxLayer.isRecycled()) {
            if (NetworkUtil.hasNetwork(context)) {
                gxLayer = new ArcGISDynamicMapServiceLayer(config.gxParameter().serverUrl);
                gxLayer.setName(config.gxParameter().name);
                gxLayer.setOnStatusChangedListener(new OnStatusChangedListener() {
                    @Override
                    public void onStatusChanged(Object o, STATUS status) {
                        Log.i(TAG, "gxLayer#onStatusChanged: " + status);
                    }
                });
                if (callback != null) {
                    callback.onGXLayerLoaded(gxLayer);
                }
            }
        }
    }

    public void release() {
        if (baseMapLayer != null) {
            if (!baseMapLayer.isRecycled()) {
                baseMapLayer.recycle();
            }
            baseMapLayer = null;
        }

        if (gxLayer != null) {
            if (!gxLayer.isRecycled()) {
                gxLayer.recycle();
            }
            gxLayer = null;
        }
    }


    public interface LayerListener {

        void onBaseLayerLoaded(Layer baseMapLayer);

        void onGXLayerLoaded(Layer gxLayer);
    }


}
