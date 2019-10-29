package com.njscky.mapcollect.business.photo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.njscky.mapcollect.R;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.db.entitiy.PhotoJCJ;
import com.njscky.mapcollect.db.entitiy.PhotoJCJDao;
import com.njscky.mapcollect.util.AppExecutors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 查看照片(带删除功能)
 */
public class DisplayPhotoActivity extends AppCompatActivity {

    private static final String TAG = "DisplayPhotoActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.vp_photos)
    ViewPager vpPhotos;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    private List<PhotoJCJ> photos;
    private int current;
    PhotoPagerAdapter adapter;

    private PhotoJCJDao photoJCJDao;

    public static void start(Activity activity, ArrayList<PhotoJCJ> photos, int current) {
        Intent intent = new Intent(activity, DisplayPhotoActivity.class);

        intent.putParcelableArrayListExtra("photos", photos);
        intent.putExtra("current", current);

        activity.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display_photo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);
        extractExtras(getIntent());
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        deletePhoto();
                        break;
                }
                return false;
            }
        });

        photoJCJDao = DbManager.getInstance(this).getDaoSession().getPhotoJCJDao();
        adapter = new PhotoPagerAdapter(photos);
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

    private void deletePhoto() {
        AppExecutors.DB.execute(new Runnable() {
            @Override
            public void run() {
                int current = vpPhotos.getCurrentItem();
                Log.i(TAG, "run: " + photos.size() + ", " + current);
                PhotoJCJ target = photos.get(current);
                File targetFile = new File(target.ZPLJ);
                if (targetFile.exists() && targetFile.canWrite()) {
                    if (targetFile.delete()) {
//                photoJCJDao.delete(target);
                        AppExecutors.MAIN.execute(new Runnable() {
                            @Override
                            public void run() {
                                photos.remove(current);
                                adapter.notifyDataSetChanged();
                            }
                        });
                        Log.i(TAG, "delete successful: " + target.ZPLJ);
                    }
                }
            }
        });


    }

    private void extractExtras(Intent intent) {
        photos = intent.getParcelableArrayListExtra("photos");
        current = intent.getIntExtra("current", 0);
    }
}
