package com.njscky.mapcollect.db.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import androidx.annotation.NonNull;

/**
 * 雨水检查井点
 */
@Entity(nameInDb = "YS_POINT_JCJ")
public class JCJPointYS implements Parcelable {
    @Id
    @Property(nameInDb = "ID")
    public Long ID;
    // 检查井编号
    public String JCJBH;
    // 井盖材质
    public String JGCZ;

    public String JGCZ_extra;
    // 井盖情况
    public String JGQK;
    // 井室情况
    public String JSQK;
    // 井室材质
    public String JSCZ;

    public String JSCZ_extra;
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
    // 井类型
    public String JLX;

    // 井类型-额外字段
    public String JLX_extra;

    // 是否填写完成
    public Boolean SFTXWC;

    // 是否拍照完成
    public Boolean SFPZWC;

    public JCJPointYS() {
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
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

    public String getJLX() {
        return this.JLX;
    }

    public void setJLX(String JLX) {
        this.JLX = JLX;
    }

    public Long getID() {
        return this.ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    @Generated(hash = 1262054955)
    public JCJPointYS(Long ID, String JCJBH, String JGCZ, String JGCZ_extra, String JGQK,
                      String JSQK, String JSCZ, String JSCZ_extra, String JSCC, String FSWLX,
                      String SZDL, String SFXG, String BZ, float XZB, float YZB, String JLX,
                      String JLX_extra, Boolean SFTXWC, Boolean SFPZWC) {
        this.ID = ID;
        this.JCJBH = JCJBH;
        this.JGCZ = JGCZ;
        this.JGCZ_extra = JGCZ_extra;
        this.JGQK = JGQK;
        this.JSQK = JSQK;
        this.JSCZ = JSCZ;
        this.JSCZ_extra = JSCZ_extra;
        this.JSCC = JSCC;
        this.FSWLX = FSWLX;
        this.SZDL = SZDL;
        this.SFXG = SFXG;
        this.BZ = BZ;
        this.XZB = XZB;
        this.YZB = YZB;
        this.JLX = JLX;
        this.JLX_extra = JLX_extra;
        this.SFTXWC = SFTXWC;
        this.SFPZWC = SFPZWC;
    }

    protected JCJPointYS(Parcel in) {
        this.ID = (Long) in.readValue(Long.class.getClassLoader());
        this.JCJBH = in.readString();
        this.JGCZ = in.readString();
        this.JGCZ_extra = in.readString();
        this.JGQK = in.readString();
        this.JSQK = in.readString();
        this.JSCZ = in.readString();
        this.JSCZ_extra = in.readString();
        this.JSCC = in.readString();
        this.FSWLX = in.readString();
        this.SZDL = in.readString();
        this.SFXG = in.readString();
        this.BZ = in.readString();
        this.XZB = in.readFloat();
        this.YZB = in.readFloat();
        this.JLX = in.readString();
        this.JLX_extra = in.readString();
        this.SFTXWC = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.SFPZWC = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public String getJGCZ_extra() {
        return this.JGCZ_extra;
    }

    public void setJGCZ_extra(String JGCZ_extra) {
        this.JGCZ_extra = JGCZ_extra;
    }

    public String getJSCZ_extra() {
        return this.JSCZ_extra;
    }

    public void setJSCZ_extra(String JSCZ_extra) {
        this.JSCZ_extra = JSCZ_extra;
    }

    public String getJLX_extra() {
        return this.JLX_extra;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setJLX_extra(String JLX_extra) {
        this.JLX_extra = JLX_extra;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.ID);
        dest.writeString(this.JCJBH);
        dest.writeString(this.JGCZ);
        dest.writeString(this.JGCZ_extra);
        dest.writeString(this.JGQK);
        dest.writeString(this.JSQK);
        dest.writeString(this.JSCZ);
        dest.writeString(this.JSCZ_extra);
        dest.writeString(this.JSCC);
        dest.writeString(this.FSWLX);
        dest.writeString(this.SZDL);
        dest.writeString(this.SFXG);
        dest.writeString(this.BZ);
        dest.writeFloat(this.XZB);
        dest.writeFloat(this.YZB);
        dest.writeString(this.JLX);
        dest.writeString(this.JLX_extra);
        dest.writeValue(this.SFTXWC);
        dest.writeValue(this.SFPZWC);
    }

    public Boolean getSFTXWC() {
        return this.SFTXWC;
    }

    public void setSFTXWC(Boolean SFTXWC) {
        this.SFTXWC = SFTXWC;
    }

    public Boolean getSFPZWC() {
        return this.SFPZWC;
    }

    public void setSFPZWC(Boolean SFPZWC) {
        this.SFPZWC = SFPZWC;
    }

    public static final Creator<JCJPointYS> CREATOR = new Creator<JCJPointYS>() {
        @Override
        public JCJPointYS createFromParcel(Parcel source) {
            return new JCJPointYS(source);
        }

        @Override
        public JCJPointYS[] newArray(int size) {
            return new JCJPointYS[size];
        }
    };
}
