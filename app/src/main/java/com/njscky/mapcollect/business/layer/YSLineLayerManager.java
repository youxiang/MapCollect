package com.njscky.mapcollect.business.layer;

import com.esri.android.map.GraphicsLayer;
import com.njscky.mapcollect.db.dao.JCJLineYSDao;
import com.njscky.mapcollect.util.AppExecutors;

import java.util.concurrent.Executor;

public class YSLineLayerManager {

    private final Executor dbExecutor;
    private final Executor postExecutor;
    private JCJLineYSDao lineDao;

    // 检查井连线图层
    private GraphicsLayer lineLayer;
    // 检查井连线的标注图层
    private GraphicsLayer annotationLayer;

    public YSLineLayerManager(JCJLineYSDao lineYSDao, String lineLayerName, String annotationLayerName) {
        this(lineYSDao, lineLayerName, annotationLayerName, AppExecutors.MAIN, AppExecutors.DB);
    }

    public YSLineLayerManager(JCJLineYSDao lineYSDao, String lineLayerName, String annotationLayerName, Executor postExecutor, Executor dbExecutor) {
        this.lineDao = lineYSDao;
        this.postExecutor = postExecutor;
        this.dbExecutor = dbExecutor;

        lineLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        lineLayer.setName(lineLayerName);

        annotationLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        annotationLayer.setName(annotationLayerName);
    }


    public void loadLayer(LayerCallback callback) {

    }
}
