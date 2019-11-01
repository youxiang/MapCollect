package com.njscky.mapcollect.business.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.njscky.mapcollect.R;
import com.njscky.mapcollect.db.DbManager;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProjectActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.rv_project_list)
    RecyclerView projectListView;
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    DBFileListAdapter dbFileListAdapter;

    DbManager dbManager;

    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ProjectActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title_activity_project);

        dbFileListAdapter = new DBFileListAdapter();
        projectListView.setLayoutManager(new LinearLayoutManager(this));
        projectListView.setAdapter(dbFileListAdapter);

        dbManager = DbManager.getInstance(this);
        loadDBFiles();
    }

    private void loadDBFiles() {
        dbFileListAdapter.setData(dbManager.getDbFiles());
    }

    @OnClick(R.id.fab)
    void onFab() {
        Snackbar.make(fab, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @OnClick(R.id.btn_exit)
    void onExit() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @OnClick(R.id.btn_confirm)
    void onConfirm() {
        File dbFile = dbFileListAdapter.getSelectedItem();
        if (dbFile == null) {
            onError();
        } else {
            onResult(dbFile.getAbsolutePath());
        }
    }

    private void onResult(String dbFilePath) {
        Intent data = new Intent();
        data.putExtra("dbFilePath", dbFilePath);
        setResult(RESULT_OK, data);
        finish();
    }

    private void onError() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.open_project_file_fail)
                .show();
    }

}
