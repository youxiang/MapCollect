package com.njscky.mapcollect.business.photo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.njscky.mapcollect.db.entitiy.PhotoJCJ;
import com.njscky.mapcollect.util.AppExecutors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

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

    /**
     * 左下角添加水印（多行，图标 + 文字）
     * 参考资料：
     * Android 对Canvas的translate方法总结    https://blog.csdn.net/u013681739/article/details/49588549
     *
     * @param photo
     */
    public static Bitmap addWaterMark(Context context, Bitmap photo, List<String> textList) {
        Bitmap newBitmap = photo;
        try {
            if (null == photo) {
                return null;
            }
            int srcWidth = photo.getWidth();
            int srcHeight = photo.getHeight();

            int unitHeight = srcHeight > srcWidth ? srcWidth / 30 : srcHeight / 25;
            int padding = unitHeight;
            int marginLeft = padding;
            int marginBottom = padding;
            int textSize = unitHeight;

            //创建一个bitmap
            if (!newBitmap.isMutable()) {
                newBitmap = copy(context, photo);
            }
            //将该图片作为画布
            Canvas canvas = new Canvas(newBitmap);

            // 设置画笔
            TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
            textPaint.setTextSize(textSize);// 字体大小
            textPaint.setTypeface(Typeface.DEFAULT);// 采用默认的宽度
            textPaint.setColor(Color.WHITE);// 采用的颜色

            int maxTextWidth = srcWidth - padding * 3;//最大文字宽度

            for (int i = textList.size() - 1; i >= 0; i--) {
                String text = textList.get(i);
                canvas.save();//锁画布(为了保存之前的画布状态)

                //文字处理
                StaticLayout layout = new StaticLayout(text, textPaint, maxTextWidth, Layout.Alignment.ALIGN_NORMAL,
                        1.0f, 0.0f, true); // 确定换行
                //绘制文字
                canvas.translate(marginLeft, srcHeight - layout.getHeight() - marginBottom); // 设定画布位置
                layout.draw(canvas); // 绘制水印

                //marginBottom 更新
                marginBottom = marginBottom + (padding + layout.getHeight());

                canvas.restore();//把当前画布返回（调整）到上一个save()状态之前
            }

            // 保存
            canvas.save();
            // 存储
            canvas.restore();

        } catch (Exception e) {
            e.printStackTrace();
            return newBitmap;
        }
        return newBitmap;
    }

    /**
     * 根据原位图生成一个新的位图，并将原位图所占空间释放
     *
     * @param srcBmp 原位图
     * @return 新位图
     */
    public static Bitmap copy(Context context, Bitmap srcBmp) {
        Bitmap destBmp = null;
        try {
            // 创建一个临时文件
            String tmpDirPath = Environment.getExternalStorageDirectory() + File.separator + context.getPackageName() + File.separator + "temppic/tmp.txt";
            File file = new File(tmpDirPath);
            if (file.exists()) {// 临时文件 ， 用一次删一次
                file.delete();
            }
            file.getParentFile().mkdirs();
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            int width = srcBmp.getWidth();
            int height = srcBmp.getHeight();
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, width * height * 4);
            // 将位图信息写进buffer
            srcBmp.copyPixelsToBuffer(map);
            // 释放原位图占用的空间
            srcBmp.recycle();
            // 创建一个新的位图
            destBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            map.position(0);
            // 从临时缓冲中拷贝位图信息
            destBmp.copyPixelsFromBuffer(map);
            channel.close();
            randomAccessFile.close();
            file.delete();
        } catch (Exception ex) {
            ex.printStackTrace();
            destBmp = null;
            return srcBmp;
        }
        return destBmp;
    }
}
