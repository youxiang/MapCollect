package com.njscky.mapcollect.business.layer;

import com.esri.android.map.GraphicsLayer;

public interface LayerCallback {
    void onLayerLoaded(GraphicsLayer... layers);
}
