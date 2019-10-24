package com.njscky.mapcollect;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.google.android.material.snackbar.Snackbar;
import com.njscky.mapcollect.business.basemap.BaseMapManager;
import com.njscky.mapcollect.business.layer.LayerCallback;
import com.njscky.mapcollect.business.layer.YSPointLayerManager;
import com.njscky.mapcollect.business.project.ProjectActivity;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.util.PermissionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQ_PERMISSIONS = 0;

    private static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQ_PROJECT = 100;

    String dbName = "MapCollect.db";

    @BindView(R.id.map)
    MapView mMapView;

    @BindView(R.id.btnGCGL)
    Button btnProject;

    BaseMapManager baseMapManager;

    YSPointLayerManager ysPointLayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        baseMapManager = BaseMapManager.getInstance(this);
        ysPointLayerManager = new YSPointLayerManager(
                this,
                "雨水管点_检查井",
                "雨水管点_检查井注记"
        );
        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQ_PERMISSIONS);


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
                Log.i(TAG, "onLayerLoaded: ");

                for (Layer layer : ysPointLayerManager.getLayers()) {
                    Log.i(TAG, "onLayerLoaded: " + ((GraphicsLayer
                            ) layer).getNumberOfGraphics());
                }
            }

            @Override
            public void onLayerLoading() {
                Log.i(TAG, "onLayerLoading: ");
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
