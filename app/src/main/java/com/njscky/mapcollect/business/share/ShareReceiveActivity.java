package com.njscky.mapcollect.business.share;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.njscky.mapcollect.R;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.util.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareReceiveActivity extends AppCompatActivity {

    private static final String TAG = "ShareReceiveActivity";

    Dialog loadingDialog;

    LoadingDialogHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Uri data = intent.getData();
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        Log.i(TAG, "onCreate: \nAction "
                + action + "\nType "
                + type + "\nData "
                + data + "\nUri "
                + uri);
        String path = null;
        if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
            path = AppUtils.getFileProviderUriToPath(this, data);
            Log.i(TAG, "onCreate: File path " + path);
        } else if (TextUtils.equals(action, Intent.ACTION_SEND)) {
            path = AppUtils.getRealPathFromUri(this, uri);
            Log.i(TAG, "onCreate: File path " + path);
        }
        if (!TextUtils.isEmpty(path)) {
            showImportDbDialog(path);
        }
    }

    private void showImportDbDialog(String path) {

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setMessage("是否导入数据库文件" + path)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    importDb(path);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    cancel(path);
                })
                .show();

    }

    private void cancel(String path) {
        finish();
    }

    private void importDb(String path) {
        DbManager.getInstance(this).importDb(path, new DbManager.ImportDbCallback() {
            @Override
            public void onImportSuccess(String outputDbFilePath) {
                Log.i(TAG, "onImportSuccess: " + outputDbFilePath);
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Toast.makeText(ShareReceiveActivity.this, "导入数据库成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImportError(Exception e) {
                Log.i(TAG, "onImportError: " + e);
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Toast.makeText(ShareReceiveActivity.this, "导入数据库错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImporting(long imported, long total) {
                Log.i(TAG, "onImporting: " + imported + ", " + total);
                if (loadingDialog == null) {
                    showLoadingDialog();
                }
                helper.progressBar.setProgress((int) (imported * 100 / total));
                helper.progressBar.setMax(100);

            }
        });
    }

    public void showLoadingDialog() {
        loadingDialog = new AlertDialog.Builder(this).setCancelable(false).create();
        View view = LayoutInflater.from(this).inflate(R.layout.layout_loading, null);
        loadingDialog.setContentView(view);
        helper = new LoadingDialogHelper();
        ButterKnife.bind(helper, view);
        loadingDialog.show();
    }

    class LoadingDialogHelper {
        @BindView(R.id.title)
        TextView title;

        @BindView(R.id.progress)
        ProgressBar progressBar;
    }
}
