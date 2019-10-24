package com.njscky.mapcollect.business.layer;

import android.content.Context;

public class LayerHelper {

    private static LayerHelper instance;

    private YSPointLayerManager ysPointLayerManager;

    private YSLineLayerManager ysLineLayerManager;

    private LayerHelper(Context context) {
        init(context);
    }

    public static LayerHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (LayerHelper.class) {
                if (instance == null) {
                    instance = new LayerHelper(context);
                }
            }
        }
        return instance;
    }

    private void init(Context context) {

        ysPointLayerManager = new YSPointLayerManager(
                context,
                "雨水管点_检查井",
                "雨水管点_检查井注记"
        );

        ysLineLayerManager = new YSLineLayerManager(
                context,
                "雨水管线_检查井",
                "雨水管线_检查井注记"
        );
    }


    public YSPointLayerManager getYsPointLayerManager() {
        return ysPointLayerManager;
    }

    public YSLineLayerManager getYsLineLayerManager() {
        return ysLineLayerManager;
    }
}
