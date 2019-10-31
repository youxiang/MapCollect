package com.njscky.mapcollect.business.photo;

import androidx.annotation.NonNull;

public class PhotoTypeItem {
    public String typeName;
    public String typeShortName;

    public PhotoTypeItem(String rawPhotoType) {
        String[] parsed = rawPhotoType.split("\\#");
        if (parsed.length >= 2) {
            typeName = parsed[0];
            typeShortName = parsed[1];
        } else {
            throw new RuntimeException("Invalid photo type " + rawPhotoType);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return typeName;
    }
}
