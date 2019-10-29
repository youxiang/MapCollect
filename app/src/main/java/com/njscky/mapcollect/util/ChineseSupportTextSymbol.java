package com.njscky.mapcollect.util;

import android.os.Build;

import com.esri.core.symbol.TextSymbol;
import com.njscky.mapcollect.MapCollectApp;

public class ChineseSupportTextSymbol extends TextSymbol {
    public ChineseSupportTextSymbol(int size, String text, int color) {
        super(size, text, color);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setFontFamily(MapCollectApp.getApp().getFontFile().getAbsolutePath());
        } else {
            setFontFamily("DroidSansFallback.ttf");
        }
    }


}
