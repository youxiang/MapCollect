package com.njscky.mapcollect.db.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

/**
 * 雨水检查井点
 */
public class JCJPointYS implements Parcelable {
    // 检查井编号
    public String JCJBH;
    // 井盖材质
    public String JGCZ;
    // 井盖情况
    public String JGQK;
    // 井室情况
    public String JSQK;
    // 井室材质
    public String JSCZ;
    // 井室尺寸
    public String JSCC;
    // 附属物类型
    public String FSWLX;
    // 所在道路
    public String SZDL;
    // 是否修改
    public String SFXG;
    // 备注
    public String BZ;
    // X坐标
    public float XZB;
    // Y坐标
    public float YZB;

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static final Parcelable.Creator<JCJPointYS> CREATOR = new Parcelable.Creator<JCJPointYS>() {
        @Override
        public JCJPointYS createFromParcel(Parcel source) {
            return new JCJPointYS(source);
        }

        @Override
        public JCJPointYS[] newArray(int size) {
            return new JCJPointYS[size];
        }
    };

    public JCJPointYS() {
    }

    protected JCJPointYS(Parcel in) {
        this.JCJBH = in.readString();
        this.JGCZ = in.readString();
        this.JGQK = in.readString();
        this.JSQK = in.readString();
        this.JSCZ = in.readString();
        this.JSCC = in.readString();
        this.FSWLX = in.readString();
        this.SZDL = in.readString();
        this.SFXG = in.readString();
        this.BZ = in.readString();
        this.XZB = in.readFloat();
        this.YZB = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.JCJBH);
        dest.writeString(this.JGCZ);
        dest.writeString(this.JGQK);
        dest.writeString(this.JSQK);
        dest.writeString(this.JSCZ);
        dest.writeString(this.JSCC);
        dest.writeString(this.FSWLX);
        dest.writeString(this.SZDL);
        dest.writeString(this.SFXG);
        dest.writeString(this.BZ);
        dest.writeFloat(this.XZB);
        dest.writeFloat(this.YZB);
    }
}
