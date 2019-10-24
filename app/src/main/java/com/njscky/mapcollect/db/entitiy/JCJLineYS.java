package com.njscky.mapcollect.db.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 雨水检查井连线
 */
public class JCJLineYS implements Parcelable {
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

    public static final Parcelable.Creator<JCJLineYS> CREATOR = new Parcelable.Creator<JCJLineYS>() {
        @Override
        public JCJLineYS createFromParcel(Parcel source) {
            return new JCJLineYS(source);
        }

        @Override
        public JCJLineYS[] newArray(int size) {
            return new JCJLineYS[size];
        }
    };

    public JCJLineYS() {
    }

    protected JCJLineYS(Parcel in) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
