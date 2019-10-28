package com.njscky.mapcollect;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
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
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.map.Graphic;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.njscky.mapcollect.business.basemap.BaseMapManager;
import com.njscky.mapcollect.business.jcjinspect.GraphicListAdpater;
import com.njscky.mapcollect.business.jcjinspect.JcjInspectFragment;
import com.njscky.mapcollect.business.layer.LayerCallback;
import com.njscky.mapcollect.business.layer.LayerHelper;
import com.njscky.mapcollect.business.layer.YSLineLayerManager;
import com.njscky.mapcollect.business.layer.YSPointLayerManager;
import com.njscky.mapcollect.business.project.ProjectActivity;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

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

    @BindView(R.id.bottom_sheet)
    View mBottomSheetView;

    @BindView(R.id.btnGCGL)
    Button btnProject;

    BaseMapManager baseMapManager;

    YSPointLayerManager ysPointLayerManager;

    YSLineLayerManager ysLineLayerManager;
    BottomSheetBehavior bottomSheetBehavior;
    private AlertDialog choosePointsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        baseMapManager = BaseMapManager.getInstance(this);
        ysPointLayerManager = LayerHelper.getInstance(this).getYsPointLayerManager();
        ysLineLayerManager = LayerHelper.getInstance(this).getYsLineLayerManager();

        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQ_PERMISSIONS);

        mMapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(float x, float y) {
                if (bottomSheetBehavior != null) {
                    bottomSheetBehavior.setHideable(true);
                    bottomSheetBehavior.setState(STATE_HIDDEN);
                }
                ysPointLayerManager.unHighlightGraphic();

//                Point p = mMapView.toMapPoint(x, y);
//                mMapView.centerAt(p, true);

                GraphicsLayer layer = (GraphicsLayer) ysPointLayerManager.getLayers()[0];

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
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


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
                baseMapManager.startLoad(mMapView);
            }
        }
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
            Snackbar.make(mMapView, "需要设置权限", Snackbar.LENGTH_INDEFINITE)
                    .setAction("打开设置 ", v -> PermissionUtils.gotoSetting(MainActivity.this))
                    .show();
        } else {
            baseMapManager.startLoad(mMapView);

            // Add layers
            mMapView.addLayers(ysPointLayerManager.getLayers());

            mMapView.addLayers(ysLineLayerManager.getLayers());
        }
    }

    @OnClick(R.id.btnGCGL)
    void onProject() {
        ProjectActivity.startForResult(this, REQ_PROJECT);
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

        ysPointLayerManager.loadLayers(new LayerCallback() {
            @Override
            public void onLayerLoaded() {
            }

            @Override
            public void onLayerLoading() {
                Log.i(TAG, "onLayerLoading: ");
            }
        });
        ysLineLayerManager.loadLayers(new LayerCallback() {
            @Override
            public void onLayerLoaded() {
            }

            @Override
            public void onLayerLoading() {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baseMapManager.release();
        LayerHelper.getInstance(this).release();
        DbManager.getInstance(this).close();
    }
}
