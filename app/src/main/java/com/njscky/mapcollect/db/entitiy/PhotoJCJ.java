package com.njscky.mapcollect.db.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

public class PhotoJCJ implements Parcelable {

    // 检查井编号
    @PrimaryKey
    @NonNull
    public String JCJBH;

    // 照片编号
    public String ZPBH;

    // 照片类型
    public String ZPLX;

    // 照片路径
    public String ZPLJ;

    // 备注
    public String BZ;

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

    public PhotoJCJ() {
    }

    protected PhotoJCJ(Parcel in) {
        this.JCJBH = in.readString();
        this.ZPBH = in.readString();
        this.ZPLX = in.readString();
        this.ZPLJ = in.readString();
        this.BZ = in.readString();
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
}
