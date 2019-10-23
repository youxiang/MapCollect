package com.njscky.mapcollect.business.layer;

import android.graphics.Color;
import android.util.Log;

import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.njscky.mapcollect.db.dao.JCJPointYSDao;
import com.njscky.mapcollect.db.entitiy.JCJPointYS;
import com.njscky.mapcollect.util.MainThreadExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class YSPointLayerManager {

    private static final String TAG = "YSPointLayerManager";

    private static final int PAGE_SIZE = 100;
    private final Executor executor;
    private JCJPointYSDao pointDao;

    // 检查井点图层
    private GraphicsLayer pointLayer;
    // 检查井点的标注图层
    private GraphicsLayer annotationLayer;
    private int pointColor;
    private int pointSize;

    private MainThreadExecutor mainThreadExecutor;

    public YSPointLayerManager(JCJPointYSDao pointDao, String pointLayerName, String annotationLayerName) {
        this(pointDao, pointLayerName, annotationLayerName, Executors.newCachedThreadPool());
    }

    public YSPointLayerManager(JCJPointYSDao pointDao, String pointLayerName, String annotationLayerName, Executor executor) {
        this.pointDao = pointDao;
        this.executor = executor;
        this.mainThreadExecutor = new MainThreadExecutor();
        pointColor = Color.rgb(76, 0, 0);
        pointSize = 12;

        pointLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        pointLayer.setName(pointLayerName);
        //pointLayer.setMinScale(5000);

        annotationLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        annotationLayer.setName(annotationLayerName);
        //annotationLayer.setMinScale(5000);

    }

    public void loadLayer(LayerCallback layerCallback) {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                int pageIndex = 0;
                List<JCJPointYS> points;
                for (; ; pageIndex++) {
                    points = pointDao.queryPoints(pageIndex, PAGE_SIZE);
                    if (points.isEmpty()) {
                        break;
                    }

                    for (JCJPointYS pointYS : points) {
                        Log.i(TAG, "run: " + pointYS);
                        Point point = new Point(pointYS.YZB, pointYS.XZB);
                        pointLayer.addGraphic(getPointGraphic(pointYS, point));
                        annotationLayer.addGraphic(getPointAnnotationGraphic(pointYS, point));
                    }
                }

                if (layerCallback != null) {
                    mainThreadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            layerCallback.onLayerLoaded(pointLayer, annotationLayer);
                        }
                    });
                }


            }
        });

    }

    private Graphic getPointGraphic(JCJPointYS pointYS, Point point) {
        SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(pointColor, pointSize, SimpleMarkerSymbol.STYLE.TRIANGLE);
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("JCJBH", pointYS.JCJBH);
        return new Graphic(point, markerSymbol, attributes);
    }

    private Graphic getPointAnnotationGraphic(JCJPointYS pointYS, Point point) {
        TextSymbol textSymbol = new TextSymbol(pointSize, pointYS.JCJBH, pointColor);
        textSymbol.setFontFamily("DroidSansFallback.ttf");
        textSymbol.setOffsetX(5);
        textSymbol.setOffsetY(5);
        return new Graphic(point, textSymbol);
    }

}
