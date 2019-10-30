package com.njscky.mapcollect.business.photo;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.njscky.mapcollect.R;
import com.njscky.mapcollect.db.entitiy.PhotoJCJ;
import com.njscky.mapcollect.util.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.VH> {

    private List<PhotoJCJ> photos;
    private OnItemClickListener onItemClickListener;

    public PhotoListAdapter() {
    }

    public void setPhotos(List<PhotoJCJ> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_photo, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (position == getItemCount() - 1) {
            holder.bindAddBtn();
        } else {
            PhotoJCJ item = getItem(position);
            holder.bindPhotoInfo(item);
        }

    }

    private PhotoJCJ getItem(int position) {
        if (photos == null || position < 0 || position >= getPhotoSize()) {
            return null;
        }
        return photos.get(position);
    }

    @Override
    public int getItemCount() {
        int photoInfoListSize = getPhotoSize();
        return photoInfoListSize + 1;
    }

    private int getPhotoSize() {
        return photos == null ? 0 : photos.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * @param photo
     * @return true: add successfully. false:add fail, already exists
     */
    public boolean addPhoto(PhotoJCJ photo) {
        if (alreadyAdded(photo)) {
            return false;
        }
        if (photos == null) {
            photos = new ArrayList<>();
        }
        photos.add(photo);
        notifyDataSetChanged();
        return true;
    }

    private boolean alreadyAdded(PhotoJCJ photo) {
        if (photos == null || photos.isEmpty()) {
            return false;
        }
        for (PhotoJCJ photoAdded : photos) {
            if (TextUtils.equals(photo.ZPLJ, photoAdded.ZPLJ)) {
                return true;
            }
        }
        return false;
    }

    public List<PhotoJCJ> getData() {
        return photos;
    }

    public interface OnItemClickListener {
        void onAddPhoto();

        void onItemClick(int position);
    }

    class VH extends RecyclerView.ViewHolder {
        ImageView ivPhoto;

        public VH(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            ivPhoto.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos == getItemCount() - 1) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onAddPhoto();
                    }
                } else {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(pos);
                    }
                }
            });

        }

        public void bindPhotoInfo(PhotoJCJ item) {
            GlideApp.with(ivPhoto).load(item.ZPLJ).into(ivPhoto);
        }

        public void bindAddBtn() {
            ivPhoto.setImageResource(R.drawable.icon_addpic_unfocused);
        }
    }
}
