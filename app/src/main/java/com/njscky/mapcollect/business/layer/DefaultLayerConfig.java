package com.njscky.mapcollect.business.layer;

import android.graphics.Color;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;

public class DefaultLayerConfig implements LayerConfig {
    @NonNull
    @Override
    public LayerParameter baseMapParameter() {
        LayerParameter layerParameter = new LayerParameter();
        layerParameter.name = "南京基础底图";
        layerParameter.serverUrl = "http://58.213.48.104/arcgis/rest/services/NJ08/NJDXT20180830/MapServer";
        layerParameter.localDir = getBaseMapsDir();
        return layerParameter;
    }

    public File getBaseMapsDir() {
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            dir = Environment.getExternalStorageDirectory();
        }

        return dir == null ? null : new File(dir, "offlinemap");
    }

    @NonNull
    @Override
    public LayerParameter gxParameter() {
        LayerParameter layerParameter = new LayerParameter();
        layerParameter.name = "管线背景图";
        layerParameter.serverUrl = "http://58.213.48.109/arcgis/rest/services/PSGX/MapServer";
        layerParameter.localDir = null;
        return layerParameter;
    }

    @NonNull
    @Override
    public GraphicLayerParameter pointParameter() {
        GraphicLayerParameter parameter = new GraphicLayerParameter();
        parameter.name = "雨水管点_检查井";
        parameter.annotationName = "雨水管点_检查井注记";
        parameter.symbolColor = Color.rgb(255, 0, 0);
        parameter.symbolSize = 12;
        parameter.annotationLayerSymbolColor = Color.rgb(255, 0, 0);
        parameter.annotationLayerSymbolSize = 12;
        return parameter;
    }

    @NonNull
    @Override
    public GraphicLayerParameter lineParameter() {
        GraphicLayerParameter parameter = new GraphicLayerParameter();
        parameter.name = "雨水管线_检查井";
        parameter.annotationName = "雨水管线_检查井注记";
        parameter.symbolColor = Color.rgb(76, 0, 0);
        parameter.symbolSize = 2;
        parameter.annotationLayerSymbolColor = Color.rgb(76, 0, 0);
        parameter.annotationLayerSymbolSize = 12;
        return parameter;
    }

}
