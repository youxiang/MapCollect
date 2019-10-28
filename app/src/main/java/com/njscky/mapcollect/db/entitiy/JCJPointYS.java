package com.njscky.mapcollect.db.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 雨水检查井点
 */
@Entity(nameInDb = "YS_POINT_JCJ")
public class JCJPointYS implements Parcelable {
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

    @Generated(hash = 1198408191)
    public JCJPointYS(
            String JCJBH, String JGCZ, String JGQK, String JSQK, String JSCZ, String JSCC,
            String FSWLX, String SZDL, String SFXG, String BZ, float XZB, float YZB
    ) {
        this.JCJBH = JCJBH;
        this.JGCZ = JGCZ;
        this.JGQK = JGQK;
        this.JSQK = JSQK;
        this.JSCZ = JSCZ;
        this.JSCC = JSCC;
        this.FSWLX = FSWLX;
        this.SZDL = SZDL;
        this.SFXG = SFXG;
        this.BZ = BZ;
        this.XZB = XZB;
        this.YZB = YZB;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
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

    public String getJCJBH() {
        return this.JCJBH;
    }

    public void setJCJBH(String JCJBH) {
        this.JCJBH = JCJBH;
    }

    public String getJGCZ() {
        return this.JGCZ;
    }

    public void setJGCZ(String JGCZ) {
        this.JGCZ = JGCZ;
    }

    public String getJGQK() {
        return this.JGQK;
    }

    public void setJGQK(String JGQK) {
        this.JGQK = JGQK;
    }

    public String getJSQK() {
        return this.JSQK;
    }

    public void setJSQK(String JSQK) {
        this.JSQK = JSQK;
    }

    public String getJSCZ() {
        return this.JSCZ;
    }

    public void setJSCZ(String JSCZ) {
        this.JSCZ = JSCZ;
    }

    public String getJSCC() {
        return this.JSCC;
    }

    public void setJSCC(String JSCC) {
        this.JSCC = JSCC;
    }

    public String getFSWLX() {
        return this.FSWLX;
    }

    public void setFSWLX(String FSWLX) {
        this.FSWLX = FSWLX;
    }

    public String getSZDL() {
        return this.SZDL;
    }

    public void setSZDL(String SZDL) {
        this.SZDL = SZDL;
    }

    public String getSFXG() {
        return this.SFXG;
    }

    public void setSFXG(String SFXG) {
        this.SFXG = SFXG;
    }

    public String getBZ() {
        return this.BZ;
    }

    public void setBZ(String BZ) {
        this.BZ = BZ;
    }

    public float getXZB() {
        return this.XZB;
    }

    public void setXZB(float XZB) {
        this.XZB = XZB;
    }

    public float getYZB() {
        return this.YZB;
    }

    public void setYZB(float YZB) {
        this.YZB = YZB;
    }
}
