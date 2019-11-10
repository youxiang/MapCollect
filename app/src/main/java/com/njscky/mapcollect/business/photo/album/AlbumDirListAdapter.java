package com.njscky.mapcollect.business.photo.album;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.njscky.mapcollect.R;
import com.njscky.mapcollect.business.photo.PhotoHelper;
import com.njscky.mapcollect.db.entitiy.PhotoJCJ;
import com.njscky.mapcollect.util.GlideApp;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumDirListAdapter extends RecyclerView.Adapter<AlbumDirListAdapter.VH> {

    public static final int STATE_VIEW = 0;
    public static final int STATE_EDIT = 1;

    private List<AlbumDir> data;

    private int state = STATE_VIEW;

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
        if (data == null) {
            return;
        }
        AlbumDir album = data.get(position);
        File dir = PhotoHelper.getPhotoDir(holder.itemView.getContext(), album.dirName);
        holder.tvName.setText(dir.getName());
        int photoNum = album.getPhotoNum();
        holder.tvCount.setText(String.valueOf(photoNum));
        if (photoNum > 0) {
            PhotoJCJ photoJCJ = album.photos.get(0);
            if (photoJCJ != null) {
                GlideApp.with(holder.ivPic).load(photoJCJ.ZPLJ).into(holder.ivPic);
            }
        }

        if (state == STATE_EDIT) {
            holder.cbSelected.setVisibility(View.VISIBLE);
            holder.cbSelected.setChecked(album.selected);
        } else {
            holder.cbSelected.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setState(int stateView) {
        if (state != stateView) {
            state = stateView;
            if (state == STATE_VIEW) {
                unselectAll();
            } else {
                notifyDataSetChanged();
            }
        }
    }

    public void setData(Collection<AlbumDir> data) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.clear();
        if (data != null) {
            this.data.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public int getSelectedCount() {
        if (data == null || data.isEmpty()) {
            return 0;
        }
        int rst = 0;
        for (AlbumDir album : data) {
            if (album.selected) {
                rst++;
            }
        }
        return rst;
    }

    public void selectAll() {
        if (data == null || data.isEmpty()) {
            return;
        }
        for (AlbumDir album : data) {
            if (!album.selected) {
                album.selected = true;
            }
        }
        notifyDataSetChanged();
    }

    public void unselectAll() {
        if (data == null || data.isEmpty()) {
            return;
        }
        for (AlbumDir album : data) {
            if (album.selected) {
                album.selected = false;
            }
        }
        notifyDataSetChanged();
    }

    public AlbumDir getItem(int position) {
        return data.get(position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemSelectListener {
        void onItemClick(View view, boolean selected);
    }

    class VH extends RecyclerView.ViewHolder {

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
                    data.get(getAdapterPosition()).selected = isChecked;
                    if (onItemSelectListener != null) {
                        onItemSelectListener.onItemClick(itemView, isChecked);
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
