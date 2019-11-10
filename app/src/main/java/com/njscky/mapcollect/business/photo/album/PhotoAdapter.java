package com.njscky.mapcollect.business.photo.album;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.njscky.mapcollect.R;
import com.njscky.mapcollect.db.entitiy.PhotoJCJ;
import com.njscky.mapcollect.util.GlideApp;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.njscky.mapcollect.business.photo.album.AlbumListActivity.STATE_EDIT;
import static com.njscky.mapcollect.business.photo.album.AlbumListActivity.STATE_VIEW;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.VH> {

    private List<PhotoJCJ> photos;

    private int state;

    private OnItemClickListener onItemClickListener;

    private OnItemLongClickListener onItemLongClickListener;

    private OnItemSelectListener onItemSelectListener;


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_album, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (photos == null) {
            return;
        }
        PhotoJCJ photoJCJ = photos.get(position);
        holder.tvName.setText(photoJCJ.getName());
        GlideApp.with(holder.ivPic).load(photoJCJ.ZPLJ).into(holder.ivPic);
        if (state == STATE_EDIT) {
            holder.cbSelected.setVisibility(View.VISIBLE);
            holder.cbSelected.setChecked(photoJCJ.isSelected);
        } else {
            holder.cbSelected.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return photos == null ? 0 : photos.size();
    }

    public void setData(List<PhotoJCJ> albums) {
        this.photos = albums;
        notifyDataSetChanged();
    }

    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            if (state == STATE_VIEW) {
                unselectAll();
            } else {
                notifyDataSetChanged();
            }
        }
    }

    void selectAll() {
        if (photos == null || photos.isEmpty()) {
            return;
        }
        for (PhotoJCJ photo : photos) {
            if (!photo.isSelected) {
                photo.isSelected = true;
            }
        }
        notifyDataSetChanged();
    }

    void unselectAll() {
        if (photos == null || photos.isEmpty()) {
            return;
        }
        for (PhotoJCJ photo : photos) {
            if (photo.isSelected) {
                photo.isSelected = false;
            }
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        if (photos == null) {
            return 0;
        }
        int rst = 0;
        for (PhotoJCJ photo : photos) {
            if (photo.isSelected) {
                rst++;
            }
        }
        return rst;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public List<PhotoJCJ> getSelectedPhotos() {
        List<PhotoJCJ> photoJCJList = new ArrayList<>();
        for (PhotoJCJ photo : photos) {
            if (photo.isSelected) {
                photoJCJList.add(photo);
            }
        }
        return photoJCJList;
    }

    public ArrayList<PhotoJCJ> getPhotos() {
        return (ArrayList<PhotoJCJ>) photos;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemSelectListener {
        void onItemSelect(View view, boolean selected);
    }

    public class VH extends RecyclerView.ViewHolder {
        ImageView ivPic;
        TextView tvName;
        TextView tvCount;
        CheckBox cbSelected;

        public VH(@NonNull View itemView) {
            super(itemView);
            ivPic = itemView.findViewById(R.id.iv_pic);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCount = itemView.findViewById(R.id.tv_count);
            cbSelected = itemView.findViewById(R.id.cb_selected);
            cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    photos.get(getAdapterPosition()).isSelected = isChecked;
                    if (onItemSelectListener != null) {
                        onItemSelectListener.onItemSelect(itemView, isChecked);
                    }
                }
            });
            itemView.setOnLongClickListener(v -> {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onItemLongClick(v, getAdapterPosition());
                }
                return false;
            });

            itemView.setOnClickListener(v -> {
                if (state == STATE_EDIT) {
                    cbSelected.setChecked(!cbSelected.isChecked());
                } else {

                }
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }
}
