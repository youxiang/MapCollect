package com.njscky.mapcollect.business.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WatermarkUtils {

    private static final float MAX_X_PERCENT = 0.3F;
    private static final float MAX_Y_PERCENT = 0.1F;
    private static String TAG = "WatermarkUtils";
    private static Paint jcjbhPaint = new Paint();
    private static Paint timePaint = new Paint();
    private static Paint jcjbhBackgroundPaint = new Paint();
    private static Paint backgroundPaint = new Paint();

    private static int WIDTH = 1080;
    private static int HEIGHT = 1920;

    static {
        jcjbhPaint.setColor(Color.WHITE);
        jcjbhPaint.setAlpha(255);
        timePaint.setColor(Color.BLACK);
        timePaint.setAlpha(255);
        jcjbhBackgroundPaint.setColor(Color.parseColor("#00FFCC"));
        jcjbhBackgroundPaint.setAlpha((int) (0.4 * 255));
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setAlpha((int) (0.4 * 255));
    }

    public static Bitmap mark(Bitmap original, Bitmap mark, int x, int y) {
        if (original == null) {
            return null;
        }
        Bitmap result = original.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        canvas.drawBitmap(original, 0, 0, paint);
        canvas.drawBitmap(mark, x, y, paint);
        return result;
    }

    public static Bitmap mark(Context context, File originalPicFile, Point screenSize, String JCJBH, long time) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = screenSize.x;
        options.outHeight = screenSize.y;
        Bitmap src = BitmapFactory.decodeFile(originalPicFile.getAbsolutePath(), options);
        String timeStr = new SimpleDateFormat("拍摄时间：yyyy.MM.dd HH:mm").format(new Date(time));
        return mark(context, src, JCJBH, timeStr);
    }

    public static Bitmap mark(Context context, Bitmap original, String JCJBH, String timeStr) {
        int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics());
        Log.i(TAG, "mark: textSize1 " + textSize);
        return mark(original, JCJBH, timeStr, textSize, Color.BLACK, 255);
    }

    public static Bitmap mark(Bitmap original,
                              String JCJBH,
                              String timeStr, int textSize, int textColor,
                              int alpha) {
        int w = original.getWidth();
        int h = original.getHeight();

        textSize = textSize * w / WIDTH;
        Log.i(TAG, "mark: textSize2 " + textSize);

        Bitmap result = original.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(original, 0, 0, paint);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setAlpha(alpha);


        Rect jcjbhRect = new Rect();
        jcjbhPaint.setTextSize(textSize);
        jcjbhPaint.getTextBounds(JCJBH, 0, JCJBH.length(), jcjbhRect);

        Rect timeRect = new Rect();
        timePaint.setTextSize(textSize);
        timePaint.getTextBounds(timeStr, 0, timeStr.length(), timeRect);

        int padding = 60;
        int x, y;
        int left = 30;
        int right = left + Math.min(Math.max(timeRect.width(), jcjbhRect.width()) + padding * 2, w);
        int bottom = h - jcjbhRect.height() * 2 - timeRect.height() - 30;
        int top = bottom - 30 - timeRect.height();

        // draw jcjbh background
        Rect jcjbhBackgound = new Rect(left, top, right, bottom);
        canvas.drawRect(jcjbhBackgound, jcjbhBackgroundPaint);

        // draw jcjbh
        x = left + (jcjbhBackgound.width() - jcjbhRect.width()) / 2;
        y = bottom - (jcjbhBackgound.height() - jcjbhRect.height()) / 2;
        canvas.drawText(JCJBH, x, y, jcjbhPaint);
        // draw background
        top = bottom;
        bottom = top + timeRect.height() + 30;

        Rect backgroundRect = new Rect(left, top, right, bottom);
        canvas.drawRect(backgroundRect, backgroundPaint);

        // draw time
        x = left + padding;
        y = jcjbhBackgound.bottom + timeRect.height();
        canvas.drawText(timeStr, x, y, timePaint);
        return result;
    }

}
