package com.njscky.mapcollect.db.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 雨水检查井连线
 */
@Entity(nameInDb = "YS_LINE_JCJ")
public class JCJLineYS implements Parcelable {
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
    // 检查井编号
    public String JCJBH;
    // 连接编号
    public String LJBH;
    // 起点埋深
    public float QDMS;
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

    public JCJLineYS() {
    }

    @Id
    public String BSM;

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

    public float getQDMS() {
        return this.QDMS;
    }

    public void setQDMS(float QDMS) {
        this.QDMS = QDMS;
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

    @Generated(hash = 886645630)
    public JCJLineYS(
            String BSM, String JCJBH, String LJBH, float QDMS, String GJ, String CZ,
            String SFXG, String SFDTYZ, String SFHJ, String HJLX, String LX, String BZ, float QDXZB,
            float QDYZB, float ZDXZB, float ZDYZB
    ) {
        this.BSM = BSM;
        this.JCJBH = JCJBH;
        this.LJBH = LJBH;
        this.QDMS = QDMS;
        this.GJ = GJ;
        this.CZ = CZ;
        this.SFXG = SFXG;
        this.SFDTYZ = SFDTYZ;
        this.SFHJ = SFHJ;
        this.HJLX = HJLX;
        this.LX = LX;
        this.BZ = BZ;
        this.QDXZB = QDXZB;
        this.QDYZB = QDYZB;
        this.ZDXZB = ZDXZB;
        this.ZDYZB = ZDYZB;
    }

    protected JCJLineYS(Parcel in) {
        this.BSM = in.readString();
        this.JCJBH = in.readString();
        this.LJBH = in.readString();
        this.QDMS = in.readFloat();
        this.GJ = in.readString();
        this.CZ = in.readString();
        this.SFXG = in.readString();
        this.SFDTYZ = in.readString();
        this.SFHJ = in.readString();
        this.HJLX = in.readString();
        this.LX = in.readString();
        this.BZ = in.readString();
        this.QDXZB = in.readFloat();
        this.QDYZB = in.readFloat();
        this.ZDXZB = in.readFloat();
        this.ZDYZB = in.readFloat();
    }

    public String getBSM() {
        return this.BSM;
    }

    public void setBSM(String BSM) {
        this.BSM = BSM;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.BSM);
        dest.writeString(this.JCJBH);
        dest.writeString(this.LJBH);
        dest.writeFloat(this.QDMS);
        dest.writeString(this.GJ);
        dest.writeString(this.CZ);
        dest.writeString(this.SFXG);
        dest.writeString(this.SFDTYZ);
        dest.writeString(this.SFHJ);
        dest.writeString(this.HJLX);
        dest.writeString(this.LX);
        dest.writeString(this.BZ);
        dest.writeFloat(this.QDXZB);
        dest.writeFloat(this.QDYZB);
        dest.writeFloat(this.ZDXZB);
        dest.writeFloat(this.ZDYZB);
    }
}
