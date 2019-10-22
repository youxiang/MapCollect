package com.njscky.mapcollect.db.entitiy;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

public class Config {
    // 管线类别
    public String GXTYPE;

    // 编号
    @PrimaryKey
    public long ID;

    // 管线图层英文名
    public String GX;

    // 管线图层中文名
    public String GXAliases;

    // 管点图层英文名
    public String GD;

    // 管点图层中文名
    public String GDAliases;

    // 颜色
    public String ColorRGB;

    // 图片类别
    public String IMGTYPE;

    // 是否使用
    public int ISUSED;

    // 类型
    public String TYPE;

    // 管类代码
    public String GLDM;

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
