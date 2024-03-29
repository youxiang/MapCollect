package com.njscky.mapcollect.business.query;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;

import com.njscky.mapcollect.R;
import com.njscky.mapcollect.business.jcjinspect.OptionalProperty;
import com.njscky.mapcollect.business.jcjinspect.OptionalPropertyValueListAdapter;
import com.njscky.mapcollect.business.jcjinspect.PhotoProperty;
import com.njscky.mapcollect.business.jcjinspect.Property;

import java.util.List;
import java.util.Map;

public class QueryResultListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int NORMAL = 0;
    private static final int OPTIONAL = 1;
    private static final int PHOTO = 2;
    Map<String, Object> map;
    private List<Property> properties;
    private OnItemClickListener onItemClickListener;

    public QueryResultListAdapter(Map<String, Object> result) {
        this.map = result;
        String hint = (String) map.get("hint");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case PHOTO:
                View photoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_property_photo, parent, false);
                return new PhotoVH(photoView);
            case OPTIONAL:
                View optionalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_property_optional, parent, false);
                return new OptionalVH(optionalView);
            case NORMAL:
            default:
                View commonView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_property, parent, false);
                return new CommonVH(commonView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Property item = getItem(position);

        if (holder instanceof OptionalVH && item instanceof OptionalProperty) {
            ((OptionalVH) holder).bind((OptionalProperty) item);
        } else if (holder instanceof CommonVH) {
            ((CommonVH) holder).bind(item);
        }
    }

    private Property getItem(int position) {
        if (properties == null || position < 0 || position >= properties.size()) {
            return null;
        }
        return properties.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        Property property = getItem(position);
        if (property instanceof OptionalProperty) {
            return OPTIONAL;
        } else if (property instanceof PhotoProperty) {
            return PHOTO;
        }
        return NORMAL;
    }

    @Override
    public int getItemCount() {
        return properties == null ? 0 : properties.size();
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onAddPhoto();

        void onViewPhoto();
    }

    class PhotoVH extends CommonVH {

        ImageButton addPhoto;
        ImageButton viewPhoto;

        public PhotoVH(@NonNull View itemView) {
            super(itemView);
            addPhoto = itemView.findViewById(R.id.add_photo);
            viewPhoto = itemView.findViewById(R.id.view_photo);

            addPhoto.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onAddPhoto();
                }
            });

            viewPhoto.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onViewPhoto();
                }
            });
        }
    }

    class OptionalVH extends RecyclerView.ViewHolder {

        TextView name;
        AppCompatSpinner value;
        EditText otherValue;

        public OptionalVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_prop_name);
            value = itemView.findViewById(R.id.sp_prop_value);
            otherValue = itemView.findViewById(R.id.tv_other_value);
        }

        public void bind(@NonNull OptionalProperty item) {
            name.setText(item.name);
            value.setSelection(item.getSelection());
            value.setAdapter(new OptionalPropertyValueListAdapter(item.options));
            value.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (item.containsTextOptionIndex(position)) {
                        showOther();
                    } else {
                        hideOther();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        private void hideOther() {
            otherValue.setVisibility(View.GONE);
        }

        private void showOther() {
            otherValue.setVisibility(View.VISIBLE);
        }
    }

    class CommonVH extends RecyclerView.ViewHolder {

        TextView name;
        EditText value;

        public CommonVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_prop_name);
            value = itemView.findViewById(R.id.et_prop_value);
        }

        public void bind(@NonNull Property item) {
            name.setText(item.name);
            value.setText(item.value);
            value.setEnabled(item.enable);
        }
    }
}
