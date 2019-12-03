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
 * 雨水检查井连线
 */
@Entity(nameInDb = "YS_LINE_JCJ")
public class JCJLineYS implements Parcelable {
    @Id
    @Property(nameInDb = "ID")
    public Long ID;
    // 检查井编号
    public String JCJBH;
    // 连接编号
    public String LJBH;
    // 起点埋深
    public float QDMS;
    // 终点埋深
    public float ZDMS;
    // 管径
    public String GJ;
    // 材质
    public String CZ;
    // 是否修改
    public String SFXG;
    // 是否与底图一致
    public String SFDTYZ;
    // 是否混接
    public String SFHJ;
    // 混接类型
    public String HJLX;

    public String HJLX_extra;
    // 类型
    public String LX;
    // 备注
    public String BZ;
    // 起点X坐标
    public float QDXZB;
    // 起点Y坐标
    public float QDYZB;
    // 终点X坐标
    public float ZDXZB;
    // 终点Y坐标
    public float ZDYZB;
    // 调查人
    public String DCR;
    // 调查时间
    public Long DCSJ;

    public JCJLineYS() {
    }

    public String getJCJBH() {
        return this.JCJBH;
    }

    public void setJCJBH(String JCJBH) {
        this.JCJBH = JCJBH;
    }

    public String getLJBH() {
        return this.LJBH;
    }

    public void setLJBH(String LJBH) {
        this.LJBH = LJBH;
    }

    public void setQDMS(float QDMS) {
        this.QDMS = QDMS;
    }

    @Generated(hash = 790838737)
    public JCJLineYS(Long ID, String JCJBH, String LJBH, float QDMS, float ZDMS, String GJ, String CZ,
                     String SFXG, String SFDTYZ, String SFHJ, String HJLX, String HJLX_extra, String LX,
                     String BZ, float QDXZB, float QDYZB, float ZDXZB, float ZDYZB, String DCR, Long DCSJ) {
        this.ID = ID;
        this.JCJBH = JCJBH;
        this.LJBH = LJBH;
        this.QDMS = QDMS;
        this.ZDMS = ZDMS;
        this.GJ = GJ;
        this.CZ = CZ;
        this.SFXG = SFXG;
        this.SFDTYZ = SFDTYZ;
        this.SFHJ = SFHJ;
        this.HJLX = HJLX;
        this.HJLX_extra = HJLX_extra;
        this.LX = LX;
        this.BZ = BZ;
        this.QDXZB = QDXZB;
        this.QDYZB = QDYZB;
        this.ZDXZB = ZDXZB;
        this.ZDYZB = ZDYZB;
        this.DCR = DCR;
        this.DCSJ = DCSJ;
    }

    public float getQDMS() {
        return this.QDMS;
    }

    public String getGJ() {
        return this.GJ;
    }

    public void setGJ(String GJ) {
        this.GJ = GJ;
    }

    public String getCZ() {
        return this.CZ;
    }

    public void setCZ(String CZ) {
        this.CZ = CZ;
    }

    public String getSFXG() {
        return this.SFXG;
    }

    public void setSFXG(String SFXG) {
        this.SFXG = SFXG;
    }

    public String getSFDTYZ() {
        return this.SFDTYZ;
    }

    public void setSFDTYZ(String SFDTYZ) {
        this.SFDTYZ = SFDTYZ;
    }

    public String getSFHJ() {
        return this.SFHJ;
    }

    public void setSFHJ(String SFHJ) {
        this.SFHJ = SFHJ;
    }

    public String getHJLX() {
        return this.HJLX;
    }

    public void setHJLX(String HJLX) {
        this.HJLX = HJLX;
    }

    public String getLX() {
        return this.LX;
    }

    public void setLX(String LX) {
        this.LX = LX;
    }

    public String getBZ() {
        return this.BZ;
    }

    public void setBZ(String BZ) {
        this.BZ = BZ;
    }

    public float getQDXZB() {
        return this.QDXZB;
    }

    public void setQDXZB(float QDXZB) {
        this.QDXZB = QDXZB;
    }

    public float getQDYZB() {
        return this.QDYZB;
    }

    public void setQDYZB(float QDYZB) {
        this.QDYZB = QDYZB;
    }

    public float getZDXZB() {
        return this.ZDXZB;
    }

    public void setZDXZB(float ZDXZB) {
        this.ZDXZB = ZDXZB;
    }

    public float getZDYZB() {
        return this.ZDYZB;
    }

    public void setZDYZB(float ZDYZB) {
        this.ZDYZB = ZDYZB;
    }


//    @Generated(hash = 1848944537)
//    public JCJLineYS(Long ID, String JCJBH, String LJBH, float QDMS, float ZDMS,String GJ,
//                     String CZ, String SFXG, String SFDTYZ, String SFHJ, String HJLX,
//                     String HJLX_extra, String LX, String BZ, float QDXZB, float QDYZB,
//                     float ZDXZB, float ZDYZB) {
//        this.ID = ID;
//        this.JCJBH = JCJBH;
//        this.LJBH = LJBH;
//        this.QDMS = QDMS;
//        this.ZDMS = ZDMS;
//        this.GJ = GJ;
//        this.CZ = CZ;
//        this.SFXG = SFXG;
//        this.SFDTYZ = SFDTYZ;
//        this.SFHJ = SFHJ;
//        this.HJLX = HJLX;
//        this.HJLX_extra = HJLX_extra;
//        this.LX = LX;
//        this.BZ = BZ;
//        this.QDXZB = QDXZB;
//        this.QDYZB = QDYZB;
//        this.ZDXZB = ZDXZB;
//        this.ZDYZB = ZDYZB;
//    }

    public float getZDMS() {
        return this.ZDMS;
    }

    public void setZDMS(float ZDMS) {
        this.ZDMS = ZDMS;
    }

    public Long getID() {
        return this.ID;
    }

    protected JCJLineYS(Parcel in) {
        this.ID = (Long) in.readValue(Long.class.getClassLoader());
        this.JCJBH = in.readString();
        this.LJBH = in.readString();
        this.QDMS = in.readFloat();
        this.ZDMS = in.readFloat();
        this.GJ = in.readString();
        this.CZ = in.readString();
        this.SFXG = in.readString();
        this.SFDTYZ = in.readString();
        this.SFHJ = in.readString();
        this.HJLX = in.readString();
        this.HJLX_extra = in.readString();
        this.LX = in.readString();
        this.BZ = in.readString();
        this.QDXZB = in.readFloat();
        this.QDYZB = in.readFloat();
        this.ZDXZB = in.readFloat();
        this.ZDYZB = in.readFloat();
        this.DCR = in.readString();
        this.DCSJ = (Long) in.readValue(Long.class.getClassLoader());
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getHJLX_extra() {
        return this.HJLX_extra;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setHJLX_extra(String HJLX_extra) {
        this.HJLX_extra = HJLX_extra;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.ID);
        dest.writeString(this.JCJBH);
        dest.writeString(this.LJBH);
        dest.writeFloat(this.QDMS);
        dest.writeFloat(this.ZDMS);
        dest.writeString(this.GJ);
        dest.writeString(this.CZ);
        dest.writeString(this.SFXG);
        dest.writeString(this.SFDTYZ);
        dest.writeString(this.SFHJ);
        dest.writeString(this.HJLX);
        dest.writeString(this.HJLX_extra);
        dest.writeString(this.LX);
        dest.writeString(this.BZ);
        dest.writeFloat(this.QDXZB);
        dest.writeFloat(this.QDYZB);
        dest.writeFloat(this.ZDXZB);
        dest.writeFloat(this.ZDYZB);
        dest.writeString(this.DCR);
        dest.writeValue(this.DCSJ);
    }

    public String getDCR() {
        return this.DCR;
    }

    public void setDCR(String DCR) {
        this.DCR = DCR;
    }

    public Long getDCSJ() {
        return this.DCSJ;
    }

    public void setDCSJ(Long DCSJ) {
        this.DCSJ = DCSJ;
    }

    public static final Creator<JCJLineYS> CREATOR = new Creator<JCJLineYS>() {
        @Override
        public JCJLineYS createFromParcel(Parcel source) {
            return new JCJLineYS(source);
        }

        @Override
        public JCJLineYS[] newArray(int size) {
            return new JCJLineYS[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
