package com.njscky.mapcollect;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.esri.android.map.MapView;
import com.njscky.mapcollect.business.basemap.BaseMapManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQ_PERMISSIONS = 0;

    private static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    String dbName = "MapCollect.db";

    @BindView(R.id.map)
    MapView mMapView;

    BaseMapManager baseMapManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        baseMapManager = BaseMapManager.getInstance(this);

        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQ_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSIONS) {
            if (isGranted(grantResults)) {
                baseMapManager.startLoad(mMapView);
            } else {
                Toast.makeText(this, "请到设置页面打开权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isGranted(int[] grantResults) {
        return grantResults[0] == PackageManager.PERMISSION_GRANTED;
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
