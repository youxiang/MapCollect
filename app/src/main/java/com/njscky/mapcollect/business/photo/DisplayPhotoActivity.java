package com.njscky.mapcollect.business.photo;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.njscky.mapcollect.R;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.db.entitiy.PhotoJCJ;
import com.njscky.mapcollect.db.entitiy.PhotoJCJDao;
import com.njscky.mapcollect.util.AppExecutors;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 查看照片(带删除功能)
 */
public class DisplayPhotoActivity extends AppCompatActivity {

    private static final String TAG = "DisplayPhotoActivity";
    @BindView(R.id.empty_view)
    View emptyView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.vp_photos)
    ViewPager vpPhotos;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    PhotoPagerAdapter adapter;
    private ArrayList<PhotoJCJ> photos;
    private int current;
    private PhotoJCJDao photoJCJDao;
    private DataSetObserver emptyObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (adapter != null && adapter.getCount() > 0) {
                emptyView.setVisibility(View.GONE);
                vpPhotos.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.VISIBLE);
                vpPhotos.setVisibility(View.GONE);
            }
        }
    };

    public static void start(Activity activity, ArrayList<PhotoJCJ> photos, int current) {
        Intent intent = new Intent(activity, DisplayPhotoActivity.class);
        intent.putParcelableArrayListExtra("photos", photos);
        intent.putExtra("current", current);
        activity.startActivity(intent);
    }

    public static void startForResult(Activity activity, ArrayList<PhotoJCJ> photos, int current, int requestCode) {
        Intent intent = new Intent(activity, DisplayPhotoActivity.class);
        intent.putParcelableArrayListExtra("photos", photos);
        intent.putExtra("current", current);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display_photo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.getItemId() == R.id.action_delete) {
                menuItem.setEnabled(photos != null && !photos.isEmpty());
            }
        }
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);
        extractExtras(getIntent());
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener((view) -> {
            onBackPressed();
        });
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    if (photos != null && !photos.isEmpty()) {
                        new AlertDialog.Builder(DisplayPhotoActivity.this)
                                .setTitle(R.string.dialog_title)
                                .setMessage(R.string.dialog_delete_photo)
                                .setNegativeButton(R.string.cancel, (dialog, which) -> {

                                })
                                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                                    deletePhoto();

                                })
                                .show();
                    }
                    break;
            }
            return false;
        });
        photoJCJDao = DbManager.getInstance(this).getDaoSession().getPhotoJCJDao();
        adapter = new PhotoPagerAdapter(photos);
        adapter.registerDataSetObserver(emptyObserver);
        vpPhotos.setAdapter(adapter);
        vpPhotos.setCurrentItem(current);
        vpPhotos.setPageTransformer(false, new DepthPageTransformer());
        vpPhotos.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected: " + position);
                current = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        tabLayout.setupWithViewPager(vpPhotos, true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.unregisterDataSetObserver(emptyObserver);
        }
    }

    private void deletePhoto() {
        AppExecutors.DB.execute(() -> {
            int current = vpPhotos.getCurrentItem();
            Log.i(TAG, "run: " + photos.size() + ", " + current);
            PhotoJCJ target = photos.remove(current);
            File targetFile = new File(target.ZPLJ);
            targetFile.delete();
            if (target.ID != null) {
                photoJCJDao.delete(target);
            }
            AppExecutors.MAIN.execute(() -> {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(targetFile)));
                adapter.notifyDataSetChanged();
                supportInvalidateOptionsMenu();
            });
        });


    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putParcelableArrayListExtra("photos", photos);
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }

    private void extractExtras(Intent intent) {
        photos = intent.getParcelableArrayListExtra("photos");
        current = intent.getIntExtra("current", 0);
    }
}
