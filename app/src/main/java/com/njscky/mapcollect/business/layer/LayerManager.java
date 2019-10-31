package com.njscky.mapcollect.business.layer;

import android.content.Context;
import android.util.Log;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.TiledLayer;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.symbol.TextSymbol;
import com.njscky.mapcollect.R;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.db.entitiy.JCJLineYS;
import com.njscky.mapcollect.db.entitiy.JCJLineYSDao;
import com.njscky.mapcollect.db.entitiy.JCJPointYS;
import com.njscky.mapcollect.db.entitiy.JCJPointYSDao;
import com.njscky.mapcollect.util.AppExecutors;
import com.njscky.mapcollect.util.ChineseSupportTextSymbol;
import com.njscky.mapcollect.util.NetworkUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LayerManager {

    private static final int PAGE_SIZE = 100;

    private static final String TAG = "LayerManager";
    private final Context context;

    private LayerConfig config;

    // 底图
    private TiledLayer baseMapLayer;
    // 管线layer
    private ArcGISDynamicMapServiceLayer gxLayer;
    // 雨水检查井点
    private GraphicsLayer ysjcjPointLayer;
    private GraphicsLayer ysjcjPointAnnotationLayer;
    // 雨水检查井线
    private GraphicsLayer ysjcjLineLayer;
    private GraphicsLayer ysjcjLineAnnotationLayer;
    private Graphic highlightedGraphic;


    public LayerManager(Context context, LayerConfig config) {
        this.config = config;
        this.context = context.getApplicationContext();
    }

    public void loadLayers(LayerListener callback) {
        loadBaseMapLayer(callback);
        loadGXLayer(callback);
        loadPointLayer(callback);
        loadLineLayer(callback);
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

    private void loadPointLayer(LayerListener callback) {
        GraphicLayerParameter parameter = config.pointParameter();
        if (ysjcjPointLayer == null || ysjcjPointLayer.isRecycled()) {
            ysjcjPointLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
            ysjcjPointLayer.setName(parameter.name);
            if (callback != null) {
                callback.onJCJPointLayerCreate(ysjcjPointLayer);
            }
        }
        if (ysjcjPointAnnotationLayer == null || ysjcjPointAnnotationLayer.isRecycled()) {
            ysjcjPointAnnotationLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
            ysjcjPointAnnotationLayer.setName(parameter.annotationName);
            if (callback != null) {
                callback.onJCJPointAnnotationLayerCreate(ysjcjPointAnnotationLayer);
            }
        }


    }

    private void loadLineLayer(LayerListener callback) {
        GraphicLayerParameter parameter = config.lineParameter();
        if (ysjcjLineLayer == null || ysjcjLineLayer.isRecycled()) {
            ysjcjLineLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
            ysjcjLineLayer.setName(parameter.name);
            if (callback != null) {
                callback.onJCJLineLayerCreate(ysjcjLineLayer);
            }
        }
        if (ysjcjLineAnnotationLayer == null || ysjcjLineAnnotationLayer.isRecycled()) {
            ysjcjLineAnnotationLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
            ysjcjLineAnnotationLayer.setName(parameter.annotationName);
            if (callback != null) {
                callback.onJCJLineAnnotationLayerCreate(ysjcjLineAnnotationLayer);
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

    public void loadData(LayerDataListener listener) {
        if (listener != null) {
            listener.onStart();
        }
        clearLayers();
        AppExecutors.DB.execute(() -> {

            loadPointData();
            //loadLineData();

            if (listener != null) {
                AppExecutors.MAIN.execute(listener::onFinish);
            }

        });

    }

    private void loadLineData() {
        GraphicLayerParameter parameter = config.lineParameter();
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(parameter.symbolColor, parameter.symbolSize, SimpleLineSymbol.STYLE.SOLID);

        JCJLineYSDao lineDao = DbManager.getInstance(context).getDaoSession().getJCJLineYSDao();
        int pageIndex = 0;
        List<JCJLineYS> jcjLineYSList;

        for (; ; pageIndex++) {

            jcjLineYSList = lineDao.queryBuilder().offset(pageIndex * PAGE_SIZE).limit(PAGE_SIZE).list();
            if (jcjLineYSList.isEmpty()) {
                break;
            }

            ArrayList<Graphic> graphics = new ArrayList<>();
            ArrayList<Graphic> graphics2 = new ArrayList<>();
            for (JCJLineYS jcjLineYS : jcjLineYSList) {
                // FIXME swap XZB and YZB
                Point startPoint = new Point(jcjLineYS.QDYZB, jcjLineYS.QDXZB);

                Point endPoint = new Point(jcjLineYS.ZDYZB, jcjLineYS.ZDXZB);

                if (startPoint.isValid() && endPoint.isValid()) {
                    Line line = new Line();
                    line.setStart(startPoint);
                    line.setEnd(endPoint);

                    Polyline polyline = new Polyline();
                    polyline.addSegment(line, true);

                    Map<String, Object> attributes = new HashMap<String, Object>();
                    attributes.put("jcjLineYS", jcjLineYS);


                    Graphic polylineGraphic = new Graphic(polyline, lineSymbol, attributes);
                    ysjcjLineLayer.addGraphic(polylineGraphic);

                    TextSymbol textSymbol = createTextSymbolInLineCenter(jcjLineYS.GJ + " " + jcjLineYS.CZ, parameter.annotationLayerSymbolColor, parameter.annotationLayerSymbolSize, startPoint, endPoint);
                    Graphic textSymbolGraphic = new Graphic(new Point((startPoint.getX() + endPoint.getX()) / 2, (startPoint.getY() + endPoint.getY()) / 2), textSymbol);
                    ysjcjLineAnnotationLayer.addGraphic(textSymbolGraphic);

                }

            }

            ysjcjLineLayer.addGraphics(graphics.toArray(new Graphic[graphics.size()]));
            ysjcjLineAnnotationLayer.addGraphics(graphics2.toArray(new Graphic[graphics2.size()]));
        }

    }

    private TextSymbol createTextSymbolInLineCenter(String txt, int colorSymbol, int sizeSymbol, Point startPoint, Point endPoint) {
        double angle = 0;
        double k = 0;
        float fAngle = 0;
        double x1 = startPoint.getX();
        double x2 = endPoint.getX();
        double y1 = startPoint.getY();
        double y2 = endPoint.getY();

        if (x2 - x1 == 0) {
            fAngle = 90;
        } else if (x2 > x1 && y2 > y1) {
            //1.X2>X1,Y2>Y1 k=Y2-Y1/X2-X1 α＝360-arctank
            k = (y2 - y1) / (x2 - x1);   //斜率
            angle = Math.atan(k);
            fAngle = (float) (360 - Math.toDegrees(angle));
        } else if (x2 > x1 && y2 < y1) {
            //2.X2>X1,Y2<Y1 k=Y1-Y2/X2-X1 α＝arctank
            k = (y1 - y2) / (x2 - x1);   //斜率
            angle = Math.atan(k);
            fAngle = (float) (Math.toDegrees(angle));
        } else if (x2 < x1 && y2 < y1) {
            //3.X2<X1,Y2<Y1 k=Y1-Y2/X1-X2 α＝360-arctank
            k = (y1 - y2) / (x1 - x2);   //斜率
            angle = Math.atan(k);
            fAngle = (float) (360 - Math.toDegrees(angle));
        } else if (x2 < x1 && y2 > y1) {
            //4.X2<X1,Y2>Y1 k=Y2-Y1/X1-X2 α＝arctank
            k = (y2 - y1) / (x1 - x2);   //斜率
            angle = Math.atan(k);
            fAngle = (float) (Math.toDegrees(angle));
        }

        TextSymbol textSymbol = new ChineseSupportTextSymbol(sizeSymbol, txt, colorSymbol);
        textSymbol.setOffsetX(5);
        textSymbol.setOffsetY(5);
        textSymbol.setAngle(fAngle);

        return textSymbol;
    }

    private void loadPointData() {
        GraphicLayerParameter parameter = config.pointParameter();
        JCJPointYSDao pointDao = DbManager.getInstance(context).getDaoSession().getJCJPointYSDao();
        int pageIndex = 0;
        SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(parameter.symbolColor, parameter.symbolSize, SimpleMarkerSymbol.STYLE.CIRCLE);

        for (; ; pageIndex++) {
            List<JCJPointYS> points = pointDao.queryBuilder().offset(pageIndex * PAGE_SIZE).limit(PAGE_SIZE).list();
            if (points.isEmpty()) {
                break;
            }

            for (JCJPointYS pointYS : points) {
                // FIXME swap XZB and YZB
                Point point = new Point(pointYS.YZB, pointYS.XZB);
                ysjcjPointLayer.addGraphic(getPointGraphic(pointYS, point, markerSymbol));
                ysjcjPointAnnotationLayer.setMinScale(5000);
                ysjcjPointAnnotationLayer.addGraphic(getPointAnnotationGraphic(pointYS, point));
                ysjcjPointAnnotationLayer.setMinScale(5000);
            }
        }

    }

    private Graphic getPointGraphic(JCJPointYS pointYS, Point point, SimpleMarkerSymbol markerSymbol) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("JCJBH", pointYS.JCJBH);
        return new Graphic(point, markerSymbol, attributes);
    }

    private Graphic getPointAnnotationGraphic(JCJPointYS pointYS, Point point) {
        GraphicLayerParameter parameter = config.pointParameter();
        TextSymbol textSymbol = new ChineseSupportTextSymbol(parameter.annotationLayerSymbolSize, pointYS.JCJBH, parameter.annotationLayerSymbolColor);
        textSymbol.setOffsetX(5);
        textSymbol.setOffsetY(5);
        return new Graphic(point, textSymbol);
    }

    private void clearLayers() {
        if (ysjcjPointLayer != null) {
            ysjcjPointLayer.removeAll();
        }
        if (ysjcjPointAnnotationLayer != null) {
            ysjcjPointAnnotationLayer.removeAll();
        }

        if (ysjcjLineLayer != null) {
            ysjcjLineLayer.removeAll();
        }
        if (ysjcjLineAnnotationLayer != null) {
            ysjcjLineAnnotationLayer.removeAll();
        }

    }

    public Layer getYSPointLayer() {
        return ysjcjPointLayer;
    }

    public void unHighlightGraphic() {
        if (highlightedGraphic != null) {
            ysjcjPointLayer.updateGraphic((int) highlightedGraphic.getId(), highlightedGraphic);
            highlightedGraphic = null;
        }
    }

    public void highLightGraphic(Graphic graphic) {
        ysjcjPointLayer.updateGraphic((int) graphic.getId(), getHighlightGraphic(graphic));
        highlightedGraphic = graphic;
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


    public interface LayerListener {

        void onBaseLayerLoaded(Layer baseMapLayer);

        void onGXLayerLoaded(Layer gxLayer);

        void onJCJPointLayerCreate(GraphicsLayer ysjcjPointLayer);

        void onJCJPointAnnotationLayerCreate(GraphicsLayer ysjcjPointAnnotationLayer);

        void onJCJLineLayerCreate(GraphicsLayer ysjcjLineLayer);

        void onJCJLineAnnotationLayerCreate(GraphicsLayer ysjcjLineAnnotationLayer);
    }

    public interface LayerDataListener {
        void onStart();

        void onFinish();
    }


}
