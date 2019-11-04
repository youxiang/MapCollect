package com.njscky.mapcollect.business.layer;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.CompositeSymbol;
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

    private static final int PAGE_SIZE = 500;

    private static final String TAG = "LayerManager";
    private final Context context;

    private LayerConfig config;

    // 底图
    private ArcGISTiledMapServiceLayer baseMapLayer;
    private ArcGISLocalTiledLayer baseLocalMapLayer;

    // 管线layer
    private ArcGISDynamicMapServiceLayer gxLayer;
    // 雨水检查井点
    private GraphicsLayer ysjcjPointLayer;
    // REMOVED
    private GraphicsLayer ysjcjPointAnnotationLayer;
    // 雨水检查井线
    private GraphicsLayer ysjcjLineLayer;
    private GraphicsLayer ysjcjLineAnnotationLayer;
    private Graphic highlightedPointGraphic;
    private Graphic highlightedLineGraphic;

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
        LayerParameter parameter = config.baseMapParameter();
        baseLocalMapLayer = new ArcGISLocalTiledLayer(parameter.localDir.getAbsolutePath() + "/江东路以东地形.tpk");
        baseLocalMapLayer.setName("背景地形图");
        baseLocalMapLayer.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                Log.i(TAG, "localTiledLayer#onStatusChanged: " + status);
            }
        });
        if (callback != null) {
            callback.onBaseLayerLoaded(baseLocalMapLayer);
        }
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
//        if (ysjcjPointAnnotationLayer == null || ysjcjPointAnnotationLayer.isRecycled()) {
//            ysjcjPointAnnotationLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
//            ysjcjPointAnnotationLayer.setName(parameter.annotationName);
//            if (callback != null) {
//                callback.onJCJPointAnnotationLayerCreate(ysjcjPointAnnotationLayer);
//            }
//        }


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
        SimpleMarkerSymbol markerSymbolTXWC = new SimpleMarkerSymbol(context.getResources().getColor(R.color.colorSFTXWC), parameter.symbolSize, SimpleMarkerSymbol.STYLE.CIRCLE);
        SimpleMarkerSymbol markerSymbolPZWC = new SimpleMarkerSymbol(context.getResources().getColor(R.color.colorSFPZWC), parameter.symbolSize, SimpleMarkerSymbol.STYLE.CIRCLE);
        SimpleMarkerSymbol markerSymbolWC = new SimpleMarkerSymbol(context.getResources().getColor(R.color.colorWC), parameter.symbolSize, SimpleMarkerSymbol.STYLE.CIRCLE);
        for (; ; pageIndex++) {
            Log.i(TAG, "loadPointData: " + pageIndex);
            List<JCJPointYS> points = pointDao.queryBuilder().offset(pageIndex * PAGE_SIZE).limit(PAGE_SIZE).list();
            if (points.isEmpty()) {
                break;
            }
            AppExecutors.MULTI_TASK.submit(() -> {
                Point point = new Point();
                for (JCJPointYS pointYS : points) {
                    // FIXME swap XZB and YZB
                    point.setXY(pointYS.YZB, pointYS.XZB);

                    boolean sftxwc = pointYS.SFTXWC == null ? false : pointYS.SFTXWC;
                    boolean sfpzwc = pointYS.SFPZWC == null ? false : pointYS.SFPZWC;
                    SimpleMarkerSymbol symbol = markerSymbol;
                    if (sftxwc || sfpzwc) {
                        if (!sftxwc) {
                            symbol = markerSymbolPZWC;
                        } else if (!sfpzwc) {
                            symbol = markerSymbolTXWC;
                        } else {
                            symbol = markerSymbolWC;
                        }
                    }
                    ysjcjPointLayer.addGraphic(getPointGraphic(pointYS, point, symbol));
                }
                return true;
            });
        }

    }

    private Graphic getPointGraphic(JCJPointYS pointYS, Point point, SimpleMarkerSymbol markerSymbol) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("JCJBH", pointYS.JCJBH);
        GraphicLayerParameter parameter = config.pointParameter();
        TextSymbol textSymbol = new ChineseSupportTextSymbol(parameter.annotationLayerSymbolSize, pointYS.JCJBH, parameter.annotationLayerSymbolColor);
        textSymbol.setOffsetX(5);
        textSymbol.setOffsetY(5);
        CompositeSymbol compositeSymbol = new CompositeSymbol();
        compositeSymbol.add(textSymbol);
        compositeSymbol.add(markerSymbol);
        return new Graphic(point, compositeSymbol, attributes);
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

    public void unHighlightPointGraphic() {
        if (highlightedPointGraphic != null) {
            ysjcjPointLayer.updateGraphic((int) highlightedPointGraphic.getId(), highlightedPointGraphic);
            highlightedPointGraphic = null;
        }
    }

    public void highLightPointGraphic(Graphic graphic) {
        ysjcjPointLayer.updateGraphic((int) graphic.getId(), getHighlightGraphic(graphic));
        highlightedPointGraphic = graphic;
    }

    private Graphic getHighlightGraphic(Graphic graphic) {
        Symbol symbol = graphic.getSymbol();

        if (symbol instanceof SimpleMarkerSymbol) {
            setHighlightSimpleMarkerSymbol((SimpleMarkerSymbol) symbol);
        } else if (symbol instanceof CompositeSymbol) {
            CompositeSymbol compositeSymbol = (CompositeSymbol) symbol;
            for (Symbol childSymbol : compositeSymbol.getSymbols()) {
                if (childSymbol instanceof SimpleMarkerSymbol) {
                    setHighlightSimpleMarkerSymbol((SimpleMarkerSymbol) childSymbol);
                } else if (childSymbol instanceof TextSymbol) {
                    setHighlightTextSymbol((TextSymbol) childSymbol);
                } else if (childSymbol instanceof SimpleLineSymbol) {
                    setHighlightSimpleLineSymbol((SimpleLineSymbol) childSymbol);
                }
            }
        }

        Graphic rst = new Graphic(graphic.getGeometry(), symbol);

        return rst;
    }

    private void setHighlightSimpleLineSymbol(SimpleLineSymbol symbol) {
        symbol.setColor(context.getResources().getColor(R.color.colorHighlight));
        symbol.setWidth(symbol.getWidth() * 1.3f);
    }

    private void setHighlightTextSymbol(TextSymbol symbol) {
        symbol.setColor(context.getResources().getColor(R.color.colorHighlight));
        symbol.setSize(symbol.getSize() * 1.3f);
    }

    private void setHighlightSimpleMarkerSymbol(SimpleMarkerSymbol symbol) {
        symbol.setColor(context.getResources().getColor(R.color.colorHighlight));
        symbol.setSize(symbol.getSize() * 1.3f);
        symbol.setStyle(SimpleMarkerSymbol.STYLE.CIRCLE);
    }

    public Geometry getPointExtent() {
        Envelope rst = new Envelope();
        Envelope env = new Envelope();
        for (int i : ysjcjPointLayer.getGraphicIDs()) {
            try {
                Geometry p = ysjcjPointLayer.getGraphic(i).getGeometry();
                p.queryEnvelope(env);
                rst.merge(env);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rst;
    }

    public Geometry getGxExtent() {
        return gxLayer.getFullExtent();
    }

    public Graphic getPointGraphicById(long graphicId) {
        return ysjcjPointLayer.getGraphic((int) graphicId);
    }

    public void drawLines(List<JCJLineYS> lineYSList) {
        GraphicLayerParameter parameter = config.lineParameter();
        parameter.symbolColor = Color.rgb(255, 0, 0);
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(parameter.symbolColor, parameter.symbolSize, SimpleLineSymbol.STYLE.SOLID);
        for (JCJLineYS jcjLineYS : lineYSList) {
            Point startPoint = new Point(jcjLineYS.QDYZB, jcjLineYS.QDXZB);

            Point endPoint = new Point(jcjLineYS.ZDYZB, jcjLineYS.ZDXZB);

            if (startPoint.isValid() && endPoint.isValid()) {
                Line line = new Line();
                line.setStart(startPoint);
                line.setEnd(endPoint);

                Polyline polyline = new Polyline();
                polyline.addSegment(line, true);

                Map<String, Object> attributes = new HashMap<String, Object>();
                attributes.put("LJBH", jcjLineYS.LJBH);
                attributes.put("jcjLineYS", jcjLineYS);

                TextSymbol textSymbol = createTextSymbolInLineCenter(jcjLineYS.GJ + " " + jcjLineYS.CZ, parameter.symbolColor, parameter.annotationLayerSymbolSize, startPoint, endPoint);
                CompositeSymbol compositeSymbol = new CompositeSymbol();
                compositeSymbol.add(lineSymbol);
                compositeSymbol.add(textSymbol);

                Graphic polylineGraphic = new Graphic(polyline, compositeSymbol, attributes);
                ysjcjLineLayer.addGraphic(polylineGraphic);
            }

        }
    }

    public void highLightLine(JCJLineYS jcjLineYS) {
        unHighLightLines();
        Log.i(TAG, "highLightLine: " + jcjLineYS.LJBH);
        for (int graphicId : ysjcjLineLayer.getGraphicIDs()) {
            Graphic graphic = ysjcjLineLayer.getGraphic(graphicId);
            if (isGraphicMatched(graphic, jcjLineYS)) {
                ysjcjLineLayer.updateGraphic(graphicId, getHighlightGraphic(graphic));
                highlightedLineGraphic = graphic;
            }
        }
    }

    public void unHighLightLines() {
        if (highlightedLineGraphic != null) {
            Log.i(TAG, "unHighLightLines: ");
            ysjcjLineLayer.updateGraphic((int) highlightedLineGraphic.getId(), highlightedLineGraphic);
            highlightedLineGraphic = null;
        }
    }

    private boolean isGraphicMatched(Graphic graphic, JCJLineYS jcjLineYS) {
        Map<String, Object> attributes = graphic.getAttributes();
        String LJBH = null;
        if (attributes != null) {
            LJBH = (String) attributes.get("LJBH");
        }
        if (TextUtils.equals(LJBH, jcjLineYS.LJBH)) {
            Log.i(TAG, "isGraphicMatched: matched");
            return true;
        }
        Log.i(TAG, "isGraphicMatched: unmatched");
        return false;
    }

    public void clearLines() {
        ysjcjLineLayer.removeAll();
        highlightedLineGraphic = null;
    }

    public void updatePoint(long graphicId, JCJPointYS pointYS, boolean cancelHightlightPoint) {
        Log.i(TAG, "updatePoint: " + graphicId + ", " + pointYS.JCJBH);
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("JCJBH", pointYS.JCJBH);
        GraphicLayerParameter parameter = config.pointParameter();
        SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(parameter.symbolColor, parameter.symbolSize, SimpleMarkerSymbol.STYLE.CIRCLE);
        SimpleMarkerSymbol markerSymbolTXWC = new SimpleMarkerSymbol(context.getResources().getColor(R.color.colorSFTXWC), parameter.symbolSize, SimpleMarkerSymbol.STYLE.CIRCLE);
        SimpleMarkerSymbol markerSymbolPZWC = new SimpleMarkerSymbol(context.getResources().getColor(R.color.colorSFPZWC), parameter.symbolSize, SimpleMarkerSymbol.STYLE.CIRCLE);
        SimpleMarkerSymbol markerSymbolWC = new SimpleMarkerSymbol(context.getResources().getColor(R.color.colorWC), parameter.symbolSize, SimpleMarkerSymbol.STYLE.CIRCLE);

        boolean sftxwc = pointYS.SFTXWC == null ? false : pointYS.SFTXWC;
        boolean sfpzwc = pointYS.SFPZWC == null ? false : pointYS.SFPZWC;
        SimpleMarkerSymbol symbol = markerSymbol;
        if (sftxwc || sfpzwc) {
            if (!sftxwc) {
                symbol = markerSymbolPZWC;
            } else if (!sfpzwc) {
                symbol = markerSymbolTXWC;
            } else {
                symbol = markerSymbolWC;
            }
        }
        Graphic graphic = ysjcjPointLayer.getGraphic((int) graphicId);
        CompositeSymbol compositeSymbol = new CompositeSymbol();
        compositeSymbol.add(symbol);

        Symbol oldSymbol = graphic.getSymbol();
        if (oldSymbol instanceof CompositeSymbol) {
            for (Symbol oldItem : ((CompositeSymbol) oldSymbol).getSymbols()) {
                if (oldItem instanceof TextSymbol) {
                    ((TextSymbol) oldItem).setColor(parameter.annotationLayerSymbolColor);
                    compositeSymbol.add(oldItem);
                }
            }
        }
        ysjcjPointLayer.updateGraphic((int) graphicId, new Graphic(graphic.getGeometry(), compositeSymbol, attributes));
        if (cancelHightlightPoint) {
            highlightedPointGraphic = null;
        }
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
