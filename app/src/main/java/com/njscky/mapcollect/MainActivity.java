package com.njscky.mapcollect;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.db.dao.ConfigDao;
import com.njscky.mapcollect.db.entitiy.Config;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    String dbPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName() + "/MapCollect.db";

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                boolean test = DbManager.getInstance().open(dbPath);
                Log.i(TAG, "onRequestPermissionsResult: " + test);
                ConfigDao configDao = DbManager.getInstance().getDao(ConfigDao.class);
                List<Config> configList = configDao.getConfigList();
                for (Config config : configList) {
                    Log.i(TAG, "onRequestPermissionsResult: " + config);
                }

            }
        }
    }
}
