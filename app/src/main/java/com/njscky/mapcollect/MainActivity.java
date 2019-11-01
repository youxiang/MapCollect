package com.njscky.mapcollect;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.tasks.identify.IdentifyParameters;
import com.esri.core.tasks.identify.IdentifyResult;
import com.esri.core.tasks.identify.IdentifyTask;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.njscky.mapcollect.business.jcjinspect.GraphicListAdpater;
import com.njscky.mapcollect.business.jcjinspect.JcjInspectFragment;
import com.njscky.mapcollect.business.layer.LayerManager;
import com.njscky.mapcollect.business.project.ProjectActivity;
import com.njscky.mapcollect.business.query.QueryResultActivity;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQ_PERMISSIONS = 0;

    private static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQ_PROJECT = 100;

    String dbName = "MapCollect.db";
    @BindView(R.id.map)
    MapView mMapView;
    IdentifyParameters params; //背景管线查询参数
    GraphicsLayer tempGraphicsLayer; //临时图层，主要用于存放临时绘制的要素，如高亮显示的要素

    @BindView(R.id.bottom_sheet)
    View mBottomSheetView;

    @BindView(R.id.btnGCGL)
    Button btnProject;

    @BindView(R.id.bottom_menu)
    View bottomMenuLayout;

    BottomSheetBehavior bottomSheetBehavior;
    private AlertDialog choosePointsDialog;

    private LayerManager layerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        layerManager = MapCollectApp.getApp().getLayerManager();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_PERMISSIONS);
    }

    private void initMap() {

        mMapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(float x, float y) {
                if (bottomSheetBehavior != null) {
                    bottomSheetBehavior.setHideable(true);
                    bottomSheetBehavior.setState(STATE_HIDDEN);
                }

//                Point p = mMapView.toMapPoint(x, y);
//                mMapView.centerAt(p, true);

                GraphicsLayer layer = (GraphicsLayer) layerManager.getYSPointLayer();

                int[] graphicIds = layer.getGraphicIDs(x, y, 10);

                if (graphicIds == null || graphicIds.length == 0) {
                    Toast.makeText(MainActivity.this, "此处无检查井", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Graphic> graphics = new ArrayList<>(graphicIds.length);

                for (int graphicId : graphicIds) {
                    Graphic graphic = layer.getGraphic(graphicId);
                    Log.i(TAG, "initMapView: " + graphic.getGeometry());
                    graphics.add(graphic);
                }

                choosePoints(layer, graphics);
            }
        });

        bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetView);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int state) {
                View decorView = getWindow().getDecorView();
                if (state == STATE_EXPANDED) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
                    decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryTransparent));
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    if (state == STATE_HIDDEN) {
                        layerManager.unHighlightGraphic();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        layerManager.loadLayers(new LayerManager.LayerListener() {
            @Override
            public void onBaseLayerLoaded(Layer baseMapLayer) {
                mMapView.addLayer(baseMapLayer);
            }

            @Override
            public void onGXLayerLoaded(Layer gxLayer) {
                mMapView.addLayer(gxLayer);
            }

            @Override
            public void onJCJPointLayerCreate(GraphicsLayer ysjcjPointLayer) {
                mMapView.addLayer(ysjcjPointLayer);
            }

            @Override
            public void onJCJPointAnnotationLayerCreate(GraphicsLayer ysjcjPointAnnotationLayer) {
                mMapView.addLayer(ysjcjPointAnnotationLayer);
            }

            @Override
            public void onJCJLineLayerCreate(GraphicsLayer ysjcjLineLayer) {
                mMapView.addLayer(ysjcjLineLayer);
            }

            @Override
            public void onJCJLineAnnotationLayerCreate(GraphicsLayer ysjcjLineAnnotationLayer) {
                mMapView.addLayer(ysjcjLineAnnotationLayer);
            }
        });

        //添加tempGraphicsLayer
        tempGraphicsLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        tempGraphicsLayer.setName("tempGraphicsLayer");
        mMapView.addLayer(tempGraphicsLayer);
    }

    private void choosePoints(GraphicsLayer layer, List<Graphic> graphics) {
        choosePointsDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.choose_points)
                .setAdapter(
                        new GraphicListAdpater(graphics),
                        (dialog, which) -> {
                            Graphic graphic = graphics.get(which);
                            Log.i(TAG, "choosePoints: " + graphic.getAttributeValue("JCJBH"));
                            JcjInspectFragment fragment = JcjInspectFragment.newInstance(graphic);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fragment, JcjInspectFragment.class.getSimpleName())
                                    .commit();

                            fragment.setBehaviorInstance(bottomSheetBehavior);

                            bottomSheetBehavior.setHideable(false);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                )
                .create();

        choosePointsDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSIONS) {
            if (isGranted(grantResults)) {
                onPermissionGrant();
            } else {
                onPermissionDeny();
            }
        }
    }

    private void onPermissionGrant() {
        bottomMenuLayout.setVisibility(View.VISIBLE);
        initMap();
    }

    private void onPermissionDeny() {
        bottomMenuLayout.setVisibility(View.GONE);
        Snackbar.make(mMapView, "需要设置权限", Snackbar.LENGTH_INDEFINITE)
                .setAction("打开设置 ", v -> PermissionUtils.gotoSetting(this))
                .show();
    }

    private boolean isGranted(int[] grantResults) {
        return grantResults[0] == PERMISSION_GRANTED;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.unpause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            onPermissionDeny();
        } else {
            onPermissionGrant();
        }
    }

    @OnClick(R.id.btnGCGL)
    void onProject() {
        ProjectActivity.startForResult(this, REQ_PROJECT);
    }

    @OnClick(R.id.btnSJBJ)
    void onEdit() {
        // TODO 编辑
    }

    @OnClick(R.id.btnTCKZ)
    void onLayer() {
        // TODO 图层
    }

    @OnClick(R.id.btnXJ)
    void onPhoto() {
        // TODO 背景管线查询
        mMapView.setOnSingleTapListener(null);
        mMapView.setOnLongPressListener(null);

        params = new IdentifyParameters();
        params.setTolerance(20); //设置容差
        params.setDPI(98);
        params.setLayers(new int[]{2, 3, 5, 6, 8, 9});
        params.setLayerMode(IdentifyParameters.TOP_MOST_LAYER);

        mMapView.setOnSingleTapListener(new OnSingleTapListener() {
            private static final long serialVersionUID = 1L;

            public void onSingleTap(final float x, final float y) {
                if (!mMapView.isLoaded()) {
                    return;
                }
                // establish the identify parameters
                Point identifyPoint = mMapView.toMapPoint(x, y);
                params.setGeometry(identifyPoint);// 设置识别位置
                params.setSpatialReference(mMapView.getSpatialReference());// 设置坐标系
                params.setMapHeight(mMapView.getHeight());// 设置地图像素高
                params.setMapWidth(mMapView.getWidth());// 设置地图像素宽
                Envelope env = new Envelope();
                mMapView.getExtent().queryEnvelope(env);
                params.setMapExtent(env);// 设置当前地图范围

                MyIdentifyTask mTask = new MyIdentifyTask(tempGraphicsLayer);
                mTask.execute(params);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQ_PROJECT:
                handleProject(data.getStringExtra("dbFilePath"));
                break;
            default:
                break;
        }
    }

    private void handleProject(String dbFilePath) {
        if (TextUtils.isEmpty(dbFilePath)) {
            return;
        }
        DbManager dbManager = DbManager.getInstance(this);
        dbManager.open(dbFilePath);

        layerManager.loadData(new LayerManager.LayerDataListener() {
            @Override
            public void onStart() {
                Log.i(TAG, "loadData#onStart: ");
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "loadData#onFinish: ");
            }
        });

//        ysPointLayerManager.loadLayers(new LayerCallback() {
//            @Override
//            public void onLayerLoaded() {
//                Log.i(TAG, "onLayerLoaded: ");
//            }
//
//            @Override
//            public void onLayerLoading() {
//                Log.i(TAG, "onLayerLoading: ");
//            }
//        });
//        ysLineLayerManager.loadLayers(new LayerCallback() {
//            @Override
//            public void onLayerLoaded() {
//                Log.i(TAG, "onLayerLoaded: ");
//            }
//
//            @Override
//            public void onLayerLoading() {
//                Log.i(TAG, "onLayerLoading: ");
//            }
//        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
//        baseMapManager.release();
//        gxlayerManager.release();
        layerManager.release();
//        LayerHelper.getInstance(this).release();
        DbManager.getInstance(this).close();
    }

    /*
     * 异步执行查询任务
     */
    private class MyIdentifyTask extends
            AsyncTask<IdentifyParameters, Void, IdentifyResult[]> {
        IdentifyTask mIdentifyTask;
        private Graphic[] highlightGraphics;
        private Context mContext;
        private GraphicsLayer mGraphicsLayer;
        private Map<String, Object> map;

        public MyIdentifyTask(GraphicsLayer graphicsLayer) {
            this.mGraphicsLayer = graphicsLayer;
        }

        @Override
        protected IdentifyResult[] doInBackground(IdentifyParameters... params) {
            IdentifyResult[] mResult = null;
            if (params != null && params.length > 0) {
                IdentifyParameters mParams = params[0];
                try {
                    mResult = mIdentifyTask.execute(mParams);// 执行识别任务
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            return mResult;
        }

        @Override
        protected void onPostExecute(IdentifyResult[] results) {
            // TODO 显示查询结果
            if (results != null && results.length > 0) {
                IdentifyResult tempResult = results[0];
                String layerName = tempResult.getLayerName();
                highlightGraphics = new Graphic[results.length];
                Geometry geom = results[0].getGeometry();
                String typeName = geom.getType().name();
                map = results[0].getAttributes();
                //显示到查询结果页面

                QueryResultActivity.start(MainActivity.this, results);


//                int color = Color.rgb(255, 255, 0);
//                // 绘制点线面的要素
//                if (typeName.equalsIgnoreCase("point")) {
//                    SimpleMarkerSymbol sms = new SimpleMarkerSymbol(color, 20,
//                            SimpleMarkerSymbol.STYLE.CIRCLE);
//                    highlightGraphics[0] = new Graphic(geom, sms);
//                } else if (typeName.equalsIgnoreCase("polyline")) {
//                    SimpleLineSymbol sls = new SimpleLineSymbol(color, 5);
//                    highlightGraphics[0] = new Graphic(geom, sls);
//                }
//                mGraphicsLayer.addGraphic(highlightGraphics[0]);
            } else {
                Toast toast = Toast.makeText(mContext, "没有拾取到对象！",
                        Toast.LENGTH_SHORT
                );
                toast.show();
            }
        }

        @Override
        protected void onPreExecute() {
            mIdentifyTask = new IdentifyTask(
                    "http://58.213.48.109/arcgis/rest/services/PSGX/MapServer");
        }
    }
}
