package com.njscky.mapcollect;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.tasks.identify.IdentifyParameters;
import com.esri.core.tasks.identify.IdentifyResult;
import com.esri.core.tasks.identify.IdentifyTask;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.njscky.mapcollect.business.edit.AddLineFragment;
import com.njscky.mapcollect.business.edit.AddPointFragment;
import com.njscky.mapcollect.business.jcjinspect.GraphicListAdpater;
import com.njscky.mapcollect.business.jcjinspect.JcjInspectFragment;
import com.njscky.mapcollect.business.layer.LayerManager;
import com.njscky.mapcollect.business.project.ProjectActivity;
import com.njscky.mapcollect.business.query.Callout_Adapter;
import com.njscky.mapcollect.business.query.CalloutitemClass;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.db.entitiy.JCJLineYS;
import com.njscky.mapcollect.db.entitiy.JCJLineYSDao;
import com.njscky.mapcollect.db.entitiy.JCJPointYS;
import com.njscky.mapcollect.db.entitiy.JCJPointYSDao;
import com.njscky.mapcollect.util.AppExecutors;
import com.njscky.mapcollect.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;

public class MainActivity extends AppCompatActivity {

    private static final int STATE_INIT = 0;
    private static final int STATE_PROJECT = 1;
    private static final int STATE_INSPECT = 2;
    private static final int STATE_LAYER = 3;
    private static final int STATE_QUERY = 4;
    private static final int STATE_EDIT = 5;

    private static final int EDIT_TYPE_UNKNOWN = 0;
    private static final int EDIT_TYPE_ADD_POINT = 1;
    private static final int EDIT_TYPE_ADD_LINE = 2;
    private static final int EDIT_TYPE_DELETE_POINT = 3;
    private static final int EDIT_TYPE_DELETE_LINE = 4;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQ_PERMISSIONS = 0;

    private static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQ_PROJECT = 100;

    public static String strUserCode = "BH01"; //当前登录用户编号

    @BindView(R.id.map)
    MapView mMapView;
    Callout mcallout;
    IdentifyParameters params; //背景管线查询参数
    GraphicsLayer tempGraphicsLayer; //临时图层，主要用于存放临时绘制的要素，如高亮显示的要素

    @BindView(R.id.bottom_sheet)
    View mBottomSheetView;

    @BindView(R.id.btnGCGL)
    Button btnProject;
    @BindView(R.id.btnInspect)
    Button btnInspect;
    @BindView(R.id.btnLayer)
    Button btnLayer;
    @BindView(R.id.btnEdit)
    Button btnEdit;
    @BindView(R.id.btnQuery)
    Button btnQuery;
    @BindView(R.id.btn_add_point)
    ImageButton btnAddPoint;
    @BindView(R.id.btn_add_line)
    ImageButton btnAddLine;
    @BindView(R.id.btn_delete_point)
    ImageButton btnDeletePoint;
    @BindView(R.id.btn_delete_line)
    ImageButton btnDeleteLine;
    @BindView(R.id.editLayout)
    View editLayout;

    @BindView(R.id.bottom_menu)
    View bottomMenuLayout;

    private int state;

    private int editType;

    private Snackbar snackbar;

    BottomSheetBehavior bottomSheetBehavior;
    private AlertDialog choosePointsDialog;

    private LayerManager layerManager;
    private boolean isFirstInit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mcallout = mMapView.getCallout();
        layerManager = MapCollectApp.getApp().getLayerManager();
        snackbar = Snackbar.make(mMapView, "需要设置权限", Snackbar.LENGTH_INDEFINITE)
                .setAction("打开设置 ", v -> PermissionUtils.gotoSetting(this));
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_PERMISSIONS);
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == STATE_HIDDEN) {
            AlertDialog isExit = new AlertDialog.Builder(this)
                    .setTitle("系统提示")
                    .setMessage("确定要退出系统?")
                    .setNegativeButton("取消", (dialog, which) -> {
                        // Do nothing
                    })
                    .setPositiveButton("确定", (dialog, which) -> finish())
                    .create();
            isExit.show();
        } else {
            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(STATE_HIDDEN);
            if (state == STATE_EDIT) {
                if (editType == EDIT_TYPE_ADD_LINE) {
                    layerManager.removeUnSavedLine();
                } else if (editType == EDIT_TYPE_ADD_POINT) {
                    layerManager.removeUnSavedPoint();
                }
            }
        }
    }

    private void initMap() {
        mMapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(float x, float y) {
                switch (state) {
                    case STATE_INSPECT:
                        singleTapInspect(x, y);
                        break;
                    case STATE_QUERY:
                        singleTapQuery(x, y);
                        break;
                    case STATE_EDIT:
                        singleTapEdit(x, y);
                        break;
                    case STATE_INIT:
                    default:
                        Log.i(TAG, "onSingleTap: " + state);
                        break;
                }
            }
        });

        bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetView);

        android.graphics.Point outSize = new android.graphics.Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        bottomSheetBehavior.setPeekHeight(outSize.y / 2);
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
                        layerManager.unHighlightPointGraphic();
                        layerManager.clearLines();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        if (isFirstInit) {
            isFirstInit = false;
            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(STATE_HIDDEN);

        }
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

    private void singleTapEdit(float x, float y) {
        switch (editType) {
            case EDIT_TYPE_ADD_POINT:
                if (DbManager.getInstance(this).getDaoSession() == null) {
                    Toast.makeText(this, "先打开工程", Toast.LENGTH_SHORT).show();
                    return;
                }
                handleAddPoint(x, y);
                break;
            case EDIT_TYPE_ADD_LINE:
                if (DbManager.getInstance(this).getDaoSession() == null) {
                    Toast.makeText(this, "先打开工程", Toast.LENGTH_SHORT).show();
                    return;
                }
                handleAddLine(x, y);
                break;
            case EDIT_TYPE_DELETE_POINT:
                if (DbManager.getInstance(this).getDaoSession() == null) {
                    Toast.makeText(this, "先打开工程", Toast.LENGTH_SHORT).show();
                    return;
                }
                handleDeletePoint(x, y);
                break;
            case EDIT_TYPE_DELETE_LINE:
                if (DbManager.getInstance(this).getDaoSession() == null) {
                    Toast.makeText(this, "先打开工程", Toast.LENGTH_SHORT).show();
                    return;
                }
                handleDeleteLine(x, y);
                break;
            case EDIT_TYPE_UNKNOWN:
            default:
                Toast.makeText(this, "请先选择编辑功能", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void handleDeletePoint(float x, float y) {
        GraphicsLayer layer = layerManager.getYSPointLayer();
        int[] graphicIds = layer.getGraphicIDs(x, y, 15);
        if (graphicIds == null || graphicIds.length == 0) {
            return;
        }
        List<Graphic> graphics = new ArrayList<>(graphicIds.length);
        for (int graphicId : graphicIds) {
            Graphic graphic = layer.getGraphic(graphicId);
            graphics.add(graphic);
        }

        if (graphics.size() <= 1) {
            handleDeletePointWithGraphic(graphics.get(0));
        } else {
            choosePointsDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.choose_delete_point)
                    .setAdapter(
                            new GraphicListAdpater(graphics),
                            (dialog, which) -> {
                                handleDeletePointWithGraphic(graphics.get(which));
                            }
                    )
                    .create();

            choosePointsDialog.show();
        }

    }

    private void handleDeletePointWithGraphic(Graphic graphic) {
        Map<String, Object> attributes = graphic.getAttributes();
        String JCJBH = null;
        if (attributes != null) {
            JCJBH = (String) attributes.get("JCJBH");
        }
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setPositiveButton(R.string.confirm, (dialog1, which) -> {
                    deletePoint(graphic);
                })
                .setMessage("删除检查井点：" + JCJBH)
                .setNegativeButton(R.string.cancel, (dialog12, which) -> {

                })
                .create();

        dialog.show();
    }

    private void deletePoint(Graphic graphic) {
        Map<String, Object> attributes = graphic.getAttributes();
        String JCJBH = null;
        if (attributes != null) {
            JCJBH = (String) attributes.get("JCJBH");
        }

        String finalJCJBH = JCJBH;
        AppExecutors.DB.execute(() -> {
            JCJPointYSDao dao = DbManager.getInstance(this).getDaoSession().getJCJPointYSDao();
            JCJPointYS pointYS = dao.queryBuilder().where(JCJPointYSDao.Properties.JCJBH.eq(finalJCJBH)).list().get(0);
            dao.delete(pointYS);
            layerManager.getYSPointLayer().removeGraphic((int) graphic.getId());
        });
    }

    private void handleDeleteLine(float x, float y) {
        GraphicsLayer layer = layerManager.getYSLineLayer();
        int[] graphicIds = layer.getGraphicIDs(x, y, 15);
        if (graphicIds == null || graphicIds.length == 0) {
            return;
        }
        List<Graphic> graphics = new ArrayList<>(graphicIds.length);
        for (int graphicId : graphicIds) {
            Graphic graphic = layer.getGraphic(graphicId);
            graphics.add(graphic);
        }

        if (graphics.size() <= 1) {
            handleDeleteLineWithGraphic(graphics.get(0));
        } else {
            choosePointsDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.choose_delete_point)
                    .setAdapter(
                            new GraphicListAdpater(graphics),
                            (dialog, which) -> {
                                handleDeleteLineWithGraphic(graphics.get(which));
                            }
                    )
                    .create();

            choosePointsDialog.show();
        }
    }

    private void handleDeleteLineWithGraphic(Graphic graphic) {
        Map<String, Object> attributes = graphic.getAttributes();
        String JCJBH = null;
        if (attributes != null) {
            JCJBH = (String) attributes.get("JCJBH");
        }
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage("删除检查井线：" + JCJBH)
                .setPositiveButton(R.string.confirm, (dialog1, which) -> {
                    deleteLine(graphic);
                })
                .setNegativeButton(R.string.cancel, (dialog12, which) -> {

                })
                .create();

        dialog.show();
    }

    private void deleteLine(Graphic graphic) {
        Map<String, Object> attributes = graphic.getAttributes();
        String JCJBH = null;
        if (attributes != null) {
            JCJBH = (String) attributes.get("JCJBH");
        }

        String finalJCJBH = JCJBH;
        AppExecutors.DB.execute(() -> {
            JCJLineYSDao dao = DbManager.getInstance(this).getDaoSession().getJCJLineYSDao();
            JCJLineYS lineYS = dao.queryBuilder().where(JCJLineYSDao.Properties.JCJBH.eq(finalJCJBH)).list().get(0);
            dao.delete(lineYS);
            layerManager.getYSLineLayer().removeGraphic((int) graphic.getId());
        });
    }

    private void handleAddLine(float x, float y) {
        GraphicsLayer layer = layerManager.getYSPointLayer();
        int[] graphicIds = layer.getGraphicIDs(x, y, 15);
        if (graphicIds == null || graphicIds.length == 0) {
            return;
        }
        List<Graphic> graphics = new ArrayList<>(graphicIds.length);
        for (int graphicId : graphicIds) {
            Graphic graphic = layer.getGraphic(graphicId);
            graphics.add(graphic);
        }

        if (graphics.size() <= 1) {
            handleAddLineWithGraphic(graphics.get(0));
        } else {
            choosePointsDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.choose_start_or_end_point)
                    .setAdapter(
                            new GraphicListAdpater(graphics),
                            (dialog, which) -> {
                                handleAddLineWithGraphic(graphics.get(which));
                            }
                    )
                    .create();

            choosePointsDialog.show();
        }

    }

    private void handleAddLineWithGraphic(Graphic graphic) {
        List<Graphic> pointGraphicIds = layerManager.getLinePoints();
        int pointCount = pointGraphicIds.size();
        if (pointCount <= 0) {
            addStartPoint(graphic);
        } else if (pointCount <= 1) {
            addEndPoint(graphic);
        }
    }

    private void addStartPoint(Graphic graphic) {
        layerManager.addLinePoint(graphic);
        Toast.makeText(this, "请选择终点", Toast.LENGTH_SHORT).show();
    }

    private void addEndPoint(Graphic graphic) {
        layerManager.addLinePoint(graphic);
        List<Graphic> linePoints = layerManager.getLinePoints();
        AddLineFragment fragment = AddLineFragment.newInstance(linePoints.get(0), linePoints.get(1));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, JcjInspectFragment.class.getSimpleName())
                .commit();

        fragment.setBehaviorInstance(bottomSheetBehavior);

        if (bottomSheetBehavior != null) {
            int bottomSheetState = bottomSheetBehavior.getState();
            if (bottomSheetState == STATE_HIDDEN) {
                bottomSheetBehavior.setHideable(false);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    private void handleAddPoint(float x, float y) {

        layerManager.removeUnSavedPoint();
        Point pt = mMapView.toMapPoint(x, y);

        AddPointFragment fragment = AddPointFragment.newInstance(pt);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, AddPointFragment.class.getSimpleName())
                .commit();

        fragment.setBehaviorInstance(bottomSheetBehavior);

        if (bottomSheetBehavior != null) {
            int bottomSheetState = bottomSheetBehavior.getState();
            if (bottomSheetState == STATE_HIDDEN) {
                bottomSheetBehavior.setHideable(false);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    private void singleTapQuery(float x, float y) {
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

    private void singleTapInspect(float x, float y) {
        GraphicsLayer layer = (GraphicsLayer) layerManager.getYSPointLayer();
        int[] graphicIds = layer.getGraphicIDs(x, y, 15);
        if (graphicIds == null || graphicIds.length == 0) {
            return;
        }
        List<Graphic> graphics = new ArrayList<>(graphicIds.length);

        for (int graphicId : graphicIds) {
            Graphic graphic = layer.getGraphic(graphicId);
            Log.i(TAG, "initMapView: " + graphic.getGeometry());
            graphics.add(graphic);
        }

        if (graphics.size() == 1) {
            showInspect(graphics.get(0));
        } else {
            choosePoints(layer, graphics);
        }
    }

    private void showInspect(Graphic graphic) {
        layerManager.clearLines();
        Log.i(TAG, "showInspect: " + graphic.getAttributeValue("JCJBH"));
        JcjInspectFragment fragment = JcjInspectFragment.newInstance(graphic);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, JcjInspectFragment.class.getSimpleName())
                .commit();

        fragment.setBehaviorInstance(bottomSheetBehavior);

        if (bottomSheetBehavior != null) {
            int bottomSheetState = bottomSheetBehavior.getState();
            if (bottomSheetState == STATE_HIDDEN) {
                bottomSheetBehavior.setHideable(false);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    private void choosePoints(GraphicsLayer layer, List<Graphic> graphics) {
        choosePointsDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.choose_points)
                .setAdapter(
                        new GraphicListAdpater(graphics),
                        (dialog, which) -> {
                            showInspect(graphics.get(which));
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
        snackbar.dismiss();
        bottomMenuLayout.setVisibility(View.VISIBLE);
        initMap();
    }

    private void onPermissionDeny() {
        bottomMenuLayout.setVisibility(View.GONE);
        snackbar.show();
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
        if (state == STATE_PROJECT) {
            state = STATE_INIT;
        } else {
            state = STATE_PROJECT;
        }
        updateState();
        ProjectActivity.startForResult(this, REQ_PROJECT);
    }

    @OnClick(R.id.btnInspect)
    void onInspect() {
        if (state == STATE_INSPECT) {
            state = STATE_INIT;
        } else {
            state = STATE_INSPECT;
        }
        updateState();

    }

    @OnClick(R.id.btnLayer)
    void onLayer() {
        if (state == STATE_LAYER) {
            state = STATE_INIT;
        } else {
            state = STATE_LAYER;
        }
        updateState();
    }

    @OnClick(R.id.btnEdit)
    void onEdit() {
        if (state == STATE_EDIT) {
            state = STATE_INIT;
        } else {
            state = STATE_EDIT;
        }
        updateState();
    }

    @OnClick(R.id.btnQuery)
    void onQuery() {
        if (state == STATE_QUERY) {
            state = STATE_INIT;
        } else {
            state = STATE_QUERY;
        }
        updateState();
        if (mcallout != null) {
            mcallout.hide();
        }
        params = new IdentifyParameters();
        params.setTolerance(20); //设置容差
        params.setDPI(98);
        params.setLayers(new int[]{2, 3, 5, 6, 8, 9});
        params.setLayerMode(IdentifyParameters.TOP_MOST_LAYER);
    }

    @OnClick(R.id.btn_add_point)
    void onAddPoint() {
        Toast.makeText(this, "add point", Toast.LENGTH_SHORT).show();
        if (editType == EDIT_TYPE_ADD_POINT) {
            editType = EDIT_TYPE_UNKNOWN;
        } else {
            editType = EDIT_TYPE_ADD_POINT;
        }
        updateEditType();
    }

    @OnClick(R.id.btn_add_line)
    void onAddLine() {
        Toast.makeText(this, "add line", Toast.LENGTH_SHORT).show();
        if (editType == EDIT_TYPE_ADD_LINE) {
            editType = EDIT_TYPE_UNKNOWN;
        } else {
            editType = EDIT_TYPE_ADD_LINE;
        }
        updateEditType();
    }

    @OnClick(R.id.btn_delete_point)
    void onDeletePoint() {
        if (editType == EDIT_TYPE_DELETE_POINT) {
            editType = EDIT_TYPE_UNKNOWN;
        } else {
            editType = EDIT_TYPE_DELETE_POINT;
        }
        updateEditType();
    }

    @OnClick(R.id.btn_delete_line)
    void onDeleteLine() {
        if (editType == EDIT_TYPE_DELETE_LINE) {
            editType = EDIT_TYPE_UNKNOWN;
        } else {
            editType = EDIT_TYPE_DELETE_LINE;
        }
        updateEditType();
    }

    private void updateEditType() {
        btnAddPoint.setSelected(editType == EDIT_TYPE_ADD_POINT);
        btnAddLine.setSelected(editType == EDIT_TYPE_ADD_LINE);
        btnDeletePoint.setSelected(editType == EDIT_TYPE_DELETE_POINT);
        btnDeleteLine.setSelected(editType == EDIT_TYPE_DELETE_LINE);
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        layerManager.release();
        DbManager.getInstance(this).close();
    }

    private void updateState() {
        btnProject.setSelected(state == STATE_PROJECT);
        btnEdit.setSelected(state == STATE_EDIT);
        btnInspect.setSelected(state == STATE_INSPECT);
        btnLayer.setSelected(state == STATE_LAYER);
        btnQuery.setSelected(state == STATE_QUERY);

        if (state == STATE_EDIT) {
            layerManager.getYSLineLayer().setVisible(true);
            editLayout.setVisibility(View.VISIBLE);
        } else {
            if (state == STATE_INSPECT) {
                layerManager.getYSLineLayer().setVisible(true);
            } else {
                layerManager.getYSLineLayer().setVisible(false);
            }
            editLayout.setVisibility(View.GONE);
        }
    }


    private void ShowCallout(Point point, List dataList) {
        View mCalloutView = View.inflate(this, R.layout.layout_callout, null);
        ListView listView = mCalloutView.findViewById(R.id.listview_callout);
        Callout_Adapter calloutAdapter = new Callout_Adapter(this, dataList);
        listView.setAdapter(calloutAdapter);

        //获取Callout
        mcallout.setContent(mCalloutView);
        mcallout.setStyle(R.xml.callout_style);
        mcallout.show(point);
    }


    /*
     * 异步执行查询任务
     */
    private class MyIdentifyTask extends
            AsyncTask<IdentifyParameters, Void, IdentifyResult[]> {
        IdentifyTask mIdentifyTask;
        private GraphicsLayer mGraphicsLayer;
        Point identifyPoint;

        public MyIdentifyTask(GraphicsLayer graphicsLayer) {
            this.mGraphicsLayer = graphicsLayer;
        }

        @Override
        protected IdentifyResult[] doInBackground(IdentifyParameters... params) {
            IdentifyResult[] mResult = null;
            if (params != null && params.length > 0) {
                IdentifyParameters mParams = params[0];
                identifyPoint = (Point) mParams.getGeometry();
                try {
                    mResult = mIdentifyTask.execute(mParams);// 执行识别任务
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return mResult;
        }

        @Override
        protected void onPostExecute(IdentifyResult[] results) {
            if (results != null && results.length > 0) {
                IdentifyResult result = results[0];
                Map<String, Object> map = result.getAttributes();
                List<CalloutitemClass> dataList = new ArrayList();
                if (map.containsKey("材质")) {
                    String strCZ = map.get("材质").toString();
                    String strGJ = map.get("管径").toString();
                    String strQDMS = map.get("起点埋深").toString();
                    String strZDMS = map.get("终点埋深").toString();

                    CalloutitemClass mCalloutitemClass_cz = new CalloutitemClass();
                    mCalloutitemClass_cz.setName("材质：");
                    mCalloutitemClass_cz.setValue(strCZ);
                    dataList.add(mCalloutitemClass_cz);

                    CalloutitemClass mCalloutitemClass_gj = new CalloutitemClass();
                    mCalloutitemClass_gj.setName("管径：");
                    mCalloutitemClass_gj.setValue(strGJ);
                    dataList.add(mCalloutitemClass_gj);

                    CalloutitemClass mCalloutitemClass_qdms = new CalloutitemClass();
                    mCalloutitemClass_qdms.setName("起点埋深：");
                    mCalloutitemClass_qdms.setValue(strQDMS);
                    dataList.add(mCalloutitemClass_qdms);

                    CalloutitemClass mCalloutitemClass_zdms = new CalloutitemClass();
                    mCalloutitemClass_zdms.setName("终点埋深：");
                    mCalloutitemClass_zdms.setValue(strZDMS);
                    dataList.add(mCalloutitemClass_zdms);

                    ShowCallout(identifyPoint, dataList);
                } else if (map.containsKey("附属物")) {
                    String strDMGC = map.get("地面高程").toString();
                    String strTZD = map.get("特征点").toString();
                    String strFSW = map.get("附属物").toString();
                    String strJGCZ = map.get("井盖材质").toString();

                    CalloutitemClass mCalloutitemClass_dmgc = new CalloutitemClass();
                    mCalloutitemClass_dmgc.setName("地面高程：");
                    mCalloutitemClass_dmgc.setValue(strDMGC);
                    dataList.add(mCalloutitemClass_dmgc);

                    CalloutitemClass mCalloutitemClass_tzd = new CalloutitemClass();
                    mCalloutitemClass_tzd.setName("特征点：");
                    mCalloutitemClass_tzd.setValue(strTZD);
                    dataList.add(mCalloutitemClass_tzd);

                    CalloutitemClass mCalloutitemClass_fsw = new CalloutitemClass();
                    mCalloutitemClass_fsw.setName("附属物：");
                    mCalloutitemClass_fsw.setValue(strFSW);
                    dataList.add(mCalloutitemClass_fsw);

                    CalloutitemClass mCalloutitemClass_jgcz = new CalloutitemClass();
                    mCalloutitemClass_jgcz.setName("井盖材质：");
                    mCalloutitemClass_jgcz.setValue(strJGCZ);
                    dataList.add(mCalloutitemClass_jgcz);
                }
                ShowCallout(identifyPoint, dataList);
            } else {
                Toast.makeText(MainActivity.this, "没有拾取到对象！", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            mIdentifyTask = new IdentifyTask(
                    "http://58.213.48.109/arcgis/rest/services/PSGX/MapServer");
        }
    }
}
