package com.njscky.mapcollect.business.photo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.njscky.mapcollect.db.entitiy.PhotoJCJ;
import com.njscky.mapcollect.util.AppExecutors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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

    public static void copyPhoto(Context context, String srcPhotoPath, File targetPhotoFile, Callback callback) {
        File srcPhotoFile = new File(srcPhotoPath);

        AppExecutors.DB.execute(() -> {
            FileInputStream fis = null;
            FileOutputStream fos = null;
            try {
                if (!targetPhotoFile.exists()) {
                    targetPhotoFile.createNewFile();
                }

                fis = new FileInputStream(srcPhotoFile);
                fos = new FileOutputStream(targetPhotoFile);

                int length = -1;
                byte[] buffer = new byte[4096];
                while ((length = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
                }

                if (callback != null) {
                    AppExecutors.MAIN.execute(() -> {
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(targetPhotoFile)));
                        callback.onCopyFinished();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface Callback {
        void onCopyFinished();
    }
}
