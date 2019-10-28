package com.njscky.mapcollect.business.layer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.TextSymbol;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.db.entitiy.JCJLineYS;
import com.njscky.mapcollect.db.entitiy.JCJLineYSDao;
import com.njscky.mapcollect.util.AppExecutors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class YSLineLayerManager implements ILayerManager {

    private static final String TAG = "YSLineLayerManager";

    private static final int PAGE_SIZE = 100;

    private final Executor dbExecutor;
    private final Executor postExecutor;
    private final Context context;

    private Layer[] layers;

    // 检查井连线图层
    private GraphicsLayer lineLayer;
    // 检查井连线的标注图层
    private GraphicsLayer annotationLayer;

    private int colorSymbol = Color.rgb(76, 0, 0);
    private int widthLineSymbol = 1;

    public YSLineLayerManager(Context context, String lineLayerName, String annotationLayerName) {
        this(context, lineLayerName, annotationLayerName, AppExecutors.MAIN, AppExecutors.DB);
    }

    public YSLineLayerManager(Context context, String lineLayerName, String annotationLayerName, Executor postExecutor, Executor dbExecutor) {
        this.context = context;
        this.postExecutor = postExecutor;
        this.dbExecutor = dbExecutor;

        lineLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        lineLayer.setName(lineLayerName);

        annotationLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        annotationLayer.setName(annotationLayerName);

        layers = new Layer[2];
        layers[0] = lineLayer;
        layers[1] = annotationLayer;
    }


    public void loadLayer(LayerCallback callback) {

    }

    @Override
    public Layer[] getLayers() {
        return layers;
    }

    @Override
    public void loadLayers(LayerCallback callback) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                notifyLayerLoading(callback);

                clearGraphicsFromLayers();

                JCJLineYSDao lineDao = DbManager.getInstance(context).getDaoSession().getJCJLineYSDao();
                int pageIndex = 0;
                List<JCJLineYS> jcjLineYSList;
                for (; ; pageIndex++) {

                    jcjLineYSList = lineDao.queryBuilder().offset(pageIndex * PAGE_SIZE).limit(PAGE_SIZE).list();
                    if (jcjLineYSList.isEmpty()) {
                        break;
                    }

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

                            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(colorSymbol, widthLineSymbol, SimpleLineSymbol.STYLE.SOLID);

                            Graphic polylineGraphic = new Graphic(polyline, lineSymbol, attributes);
                            lineLayer.addGraphic(polylineGraphic);

                            TextSymbol textSymbol = createTextSymbolInLineCenter(jcjLineYS.GJ + " " + jcjLineYS.CZ, colorSymbol, widthLineSymbol, startPoint, endPoint);
                            Graphic textSymbolGraphic = new Graphic(new Point((startPoint.getX() + endPoint.getX()) / 2, (startPoint.getY() + endPoint.getY()) / 2), textSymbol);
                            annotationLayer.addGraphic(textSymbolGraphic);

                        }

                    }
                }

                notifyLayerLoaded(callback);

            }
        });
    }

    private TextSymbol createTextSymbolInLineCenter(String txt, int colorSymbol, int widthLineSymbol, Point startPoint, Point endPoint) {

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

        TextSymbol textSymbol = new TextSymbol(widthLineSymbol, txt, colorSymbol);
        textSymbol.setFontFamily("DroidSansFallback.ttf");
        textSymbol.setOffsetX(5);
        textSymbol.setOffsetY(5);
        textSymbol.setAngle(fAngle);

        return textSymbol;
    }

    private void clearGraphicsFromLayers() {
        lineLayer.removeAll();
        annotationLayer.removeAll();
    }

    private void notifyLayerLoaded(LayerCallback callback) {
        Log.i(TAG, "notifyLayerLoaded: ");
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
}
