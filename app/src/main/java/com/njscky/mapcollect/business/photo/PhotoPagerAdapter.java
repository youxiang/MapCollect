package com.njscky.mapcollect.business.photo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.njscky.mapcollect.R;
import com.njscky.mapcollect.db.entitiy.PhotoJCJ;
import com.njscky.mapcollect.util.GlideApp;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class PhotoPagerAdapter extends PagerAdapter {

    List<PhotoJCJ> photos;

    public PhotoPagerAdapter(List<PhotoJCJ> photos) {
        this.photos = photos;
    }

    @Override
    public int getCount() {
        return photos == null ? 0 : photos.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoJCJ photoJCJ = photos.get(position);
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout__page_item_photo, null);
        container.addView(view);
        ImageView photo = view.findViewById(R.id.iv_photo);
        GlideApp.with(photo).load(photoJCJ.ZPLJ).into(photo);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
