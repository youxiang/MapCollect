package com.njscky.mapcollect.business.layer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.symbol.TextSymbol;
import com.njscky.mapcollect.R;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.db.dao.JCJPointYSDao;
import com.njscky.mapcollect.db.entitiy.JCJPointYS;
import com.njscky.mapcollect.util.AppExecutors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class YSPointLayerManager implements ILayerManager {

    private static final String TAG = "YSPointLayerManager";

    private static final int PAGE_SIZE = 100;
    private final Executor dbExecutor;
    private final Context context;
    private Executor postExecutor;

    private Layer[] mLayers;

    // 检查井点图层
    private GraphicsLayer pointLayer;
    // 检查井点的标注图层
    private GraphicsLayer annotationLayer;

    private int pointColor;
    private int pointSize;

    private Graphic highlightedGraphic;


    public YSPointLayerManager(Context context, String pointLayerName, String annotationLayerName) {
        this(context, pointLayerName, annotationLayerName, AppExecutors.MAIN, AppExecutors.DB);
    }

    public YSPointLayerManager(Context context, String pointLayerName, String annotationLayerName, Executor postExecutor, Executor dbExecutor) {
        this.context = context.getApplicationContext();
        this.postExecutor = postExecutor;
        this.dbExecutor = dbExecutor;
        pointColor = Color.rgb(76, 0, 0);
        pointSize = 12;

        mLayers = new Layer[2];

        pointLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        pointLayer.setName(pointLayerName);

        annotationLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        annotationLayer.setName(annotationLayerName);

        mLayers[0] = pointLayer;
        mLayers[1] = annotationLayer;

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

    @Override
    public Layer[] getLayers() {
        return mLayers;
    }

    @Override
    public void loadLayers(LayerCallback callback) {

        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                notifyLayerLoading(callback);

                clearGraphicsFromLayers();

                JCJPointYSDao pointDao = DbManager.getInstance(context).getDao(JCJPointYSDao.class);
                int pageIndex = 0;
                List<JCJPointYS> points;
                for (; ; pageIndex++) {
                    points = pointDao.queryPoints(pageIndex, PAGE_SIZE);
                    if (points.isEmpty()) {
                        break;
                    }

                    for (JCJPointYS pointYS : points) {
                        Log.i(TAG, "run: " + pointYS);
                        // FIXME swap XZB and YZB
                        Point point = new Point(pointYS.YZB, pointYS.XZB);
                        pointLayer.addGraphic(getPointGraphic(pointYS, point));
                        annotationLayer.addGraphic(getPointAnnotationGraphic(pointYS, point));
                    }
                }

                notifyLayerLoaded(callback);

            }
        });
    }

    private void clearGraphicsFromLayers() {
        pointLayer.removeAll();
        annotationLayer.removeAll();
    }

    private void notifyLayerLoaded(LayerCallback callback) {
        if (callback != null) {
            postExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    callback.onLayerLoaded();
                }
            });
        }
    }

    private void notifyLayerLoading(LayerCallback callback) {
        if (callback != null) {
            postExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    callback.onLayerLoading();
                }
            });
        }
    }

    private Graphic getHighlightGraphic(Graphic graphic) {
        Symbol symbol = graphic.getSymbol();
        if (symbol instanceof SimpleMarkerSymbol) {
            SimpleMarkerSymbol simpleMarkerSymbol = (SimpleMarkerSymbol) symbol;
            simpleMarkerSymbol.setColor(context.getResources().getColor(R.color.colorPrimary));
            simpleMarkerSymbol.setSize(simpleMarkerSymbol.getSize() * 1.3f);
            simpleMarkerSymbol.setStyle(SimpleMarkerSymbol.STYLE.CIRCLE);
        }

        Graphic rst = new Graphic(graphic.getGeometry(), symbol);

        return rst;
    }

    public void highLightGraphic(Graphic graphic) {
        pointLayer.updateGraphic((int) graphic.getId(), getHighlightGraphic(graphic));
        highlightedGraphic = graphic;
    }

    public void unHighlightGraphic() {
        if (highlightedGraphic != null) {
            pointLayer.updateGraphic((int) highlightedGraphic.getId(), highlightedGraphic);
            highlightedGraphic = null;
        }
    }
}
