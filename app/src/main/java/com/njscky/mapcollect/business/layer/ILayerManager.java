package com.njscky.mapcollect.business.layer;

import com.esri.android.map.Layer;

public interface ILayerManager {
    Layer[] getLayers();

    void loadLayers(LayerCallback callback);
}
