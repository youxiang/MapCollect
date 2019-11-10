package com.njscky.mapcollect.business.photo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.njscky.mapcollect.db.entitiy.PhotoJCJ;

import java.io.File;

public class PhotoHelper {

    private PhotoHelper() {
    }

    public static File getPhotoRootDir(Context context) {
        String photoDirPath = Environment.getExternalStorageDirectory() + File.separator + context.getPackageName() + File.separator + "photos";
        File photoRootDir = new File(photoDirPath);
        if (!photoRootDir.exists()) {
            photoRootDir.mkdirs();
        }
        return photoRootDir;
    }

    public static File getPhotoDir(Context context, PhotoTypeItem photoTypeItem) {
        return getPhotoDir(context, photoTypeItem.typeName);
    }


    public static File getPhotoDir(Context context, String typeName) {
        File photoRootDir = getPhotoRootDir(context);
        File photoDir = new File(photoRootDir, typeName);
        if (!photoDir.exists()) {
            photoDir.mkdirs();
        }
        return photoDir;
    }

    public static boolean deletePhotoFile(Context context, PhotoJCJ photoJCJ) {
        File file = new File(photoJCJ.ZPLJ);
        boolean success = false;
        if (file.exists()) {
            success = file.delete();
            if (success) {
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            }
        }
        return success;
    }
}
