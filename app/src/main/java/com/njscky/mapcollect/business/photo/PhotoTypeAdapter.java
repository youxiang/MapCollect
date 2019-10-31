package com.njscky.mapcollect.business.photo;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public class PhotoTypeAdapter extends ArrayAdapter<PhotoTypeItem> {
    public PhotoTypeAdapter(@NonNull Context context, int resource, @NonNull PhotoTypeItem[] objects) {
        super(context, resource, objects);
    }

    public PhotoTypeAdapter(@NonNull Context context, int resource, @NonNull List<PhotoTypeItem> objects) {
        super(context, resource, objects);
    }


}
