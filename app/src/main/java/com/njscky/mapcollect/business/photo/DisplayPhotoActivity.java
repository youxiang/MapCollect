package com.njscky.mapcollect.business.photo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.njscky.mapcollect.R;
import com.njscky.mapcollect.db.entitiy.PhotoJCJ;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 查看照片
 */
public class DisplayPhotoActivity extends AppCompatActivity {

    @BindView(R.id.vp_photos)
    ViewPager vpPhotos;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    private List<PhotoJCJ> photos;
    private int current;

    public static void start(Activity activity, ArrayList<PhotoJCJ> photos, int current) {
        Intent intent = new Intent(activity, DisplayPhotoActivity.class);

        intent.putParcelableArrayListExtra("photos", photos);
        intent.putExtra("current", current);

        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);
        extractExtras(getIntent());
        ButterKnife.bind(this);

        vpPhotos.setAdapter(new PhotoPagerAdapter(photos));
        vpPhotos.setCurrentItem(current);
        vpPhotos.setPageTransformer(false, new DepthPageTransformer());

        tabLayout.setupWithViewPager(vpPhotos, true);
    }

    private void extractExtras(Intent intent) {
        photos = intent.getParcelableArrayListExtra("photos");
        current = intent.getIntExtra("current", 0);
    }
}
