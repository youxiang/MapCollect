package com.njscky.mapcollect.db.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "ZP_JCJ")
public class PhotoJCJ implements Parcelable {

    public static final Parcelable.Creator<PhotoJCJ> CREATOR = new Parcelable.Creator<PhotoJCJ>() {
        @Override
        public PhotoJCJ createFromParcel(Parcel source) {
            return new PhotoJCJ(source);
        }

        @Override
        public PhotoJCJ[] newArray(int size) {
            return new PhotoJCJ[size];
        }
    };
    // 检查井编号
    public String JCJBH;
    // 照片编号
    public String ZPBH;
    // 照片类型
    public String ZPLX;
    // 照片路径
    public String ZPLJ;
    // 备注
    public String BZ;

    public PhotoJCJ() {
    }

    protected PhotoJCJ(Parcel in) {
        this.JCJBH = in.readString();
        this.ZPBH = in.readString();
        this.ZPLX = in.readString();
        this.ZPLJ = in.readString();
        this.BZ = in.readString();
    }

    @Generated(hash = 2015541024)
    public PhotoJCJ(String JCJBH, String ZPBH, String ZPLX, String ZPLJ, String BZ) {
        this.JCJBH = JCJBH;
        this.ZPBH = ZPBH;
        this.ZPLX = ZPLX;
        this.ZPLJ = ZPLJ;
        this.BZ = BZ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.JCJBH);
        dest.writeString(this.ZPBH);
        dest.writeString(this.ZPLX);
        dest.writeString(this.ZPLJ);
        dest.writeString(this.BZ);
    }

    public String getJCJBH() {
        return this.JCJBH;
    }

    public void setJCJBH(String JCJBH) {
        this.JCJBH = JCJBH;
    }

    public String getZPBH() {
        return this.ZPBH;
    }

    public void setZPBH(String ZPBH) {
        this.ZPBH = ZPBH;
    }

    public String getZPLX() {
        return this.ZPLX;
    }

    public void setZPLX(String ZPLX) {
        this.ZPLX = ZPLX;
    }

    public String getZPLJ() {
        return this.ZPLJ;
    }

    public void setZPLJ(String ZPLJ) {
        this.ZPLJ = ZPLJ;
    }

    public String getBZ() {
        return this.BZ;
    }

    public void setBZ(String BZ) {
        this.BZ = BZ;
    }
}
