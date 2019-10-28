package com.njscky.mapcollect.db.entitiy;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "CONFIG")
public class Config {
    // 管线类别
    public String GXTYPE;

    // 编号
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

    @Generated(hash = 344990451)
    public Config(
            String GXTYPE, long ID, String GX, String GXAliases, String GD,
            String GDAliases, String ColorRGB, String IMGTYPE, int ISUSED,
            String TYPE, String GLDM
    ) {
        this.GXTYPE = GXTYPE;
        this.ID = ID;
        this.GX = GX;
        this.GXAliases = GXAliases;
        this.GD = GD;
        this.GDAliases = GDAliases;
        this.ColorRGB = ColorRGB;
        this.IMGTYPE = IMGTYPE;
        this.ISUSED = ISUSED;
        this.TYPE = TYPE;
        this.GLDM = GLDM;
    }

    @Generated(hash = 589037648)
    public Config() {
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getGXTYPE() {
        return this.GXTYPE;
    }

    public void setGXTYPE(String GXTYPE) {
        this.GXTYPE = GXTYPE;
    }

    public long getID() {
        return this.ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getGX() {
        return this.GX;
    }

    public void setGX(String GX) {
        this.GX = GX;
    }

    public String getGXAliases() {
        return this.GXAliases;
    }

    public void setGXAliases(String GXAliases) {
        this.GXAliases = GXAliases;
    }

    public String getGD() {
        return this.GD;
    }

    public void setGD(String GD) {
        this.GD = GD;
    }

    public String getGDAliases() {
        return this.GDAliases;
    }

    public void setGDAliases(String GDAliases) {
        this.GDAliases = GDAliases;
    }

    public String getColorRGB() {
        return this.ColorRGB;
    }

    public void setColorRGB(String ColorRGB) {
        this.ColorRGB = ColorRGB;
    }

    public String getIMGTYPE() {
        return this.IMGTYPE;
    }

    public void setIMGTYPE(String IMGTYPE) {
        this.IMGTYPE = IMGTYPE;
    }

    public int getISUSED() {
        return this.ISUSED;
    }

    public void setISUSED(int ISUSED) {
        this.ISUSED = ISUSED;
    }

    public String getTYPE() {
        return this.TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getGLDM() {
        return this.GLDM;
    }

    public void setGLDM(String GLDM) {
        this.GLDM = GLDM;
    }
}
