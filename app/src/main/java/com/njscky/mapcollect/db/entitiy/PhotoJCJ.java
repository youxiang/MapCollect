package com.njscky.mapcollect.db.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

@Entity(nameInDb = "ZP_JCJ")
public class PhotoJCJ implements Parcelable {

    @Id
    @Property(nameInDb = "ID")
    public Long ID;
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
    public static final Creator<PhotoJCJ> CREATOR = new Creator<PhotoJCJ>() {
        @Override
        public PhotoJCJ createFromParcel(Parcel source) {
            return new PhotoJCJ(source);
        }

        @Override
        public PhotoJCJ[] newArray(int size) {
            return new PhotoJCJ[size];
        }
    };
    // 地址
    public String ZPDZ;

    public PhotoJCJ() {
    }

    // 时间
    public Long ZPSJ;

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

    public Long getID() {
        return this.ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    @Transient
    public boolean isSelected;

    @Generated(hash = 1837184641)
    public PhotoJCJ(Long ID, String JCJBH, String ZPBH, String ZPLX, String ZPLJ, String BZ,
                    String ZPDZ, Long ZPSJ) {
        this.ID = ID;
        this.JCJBH = JCJBH;
        this.ZPBH = ZPBH;
        this.ZPLX = ZPLX;
        this.ZPLJ = ZPLJ;
        this.BZ = BZ;
        this.ZPDZ = ZPDZ;
        this.ZPSJ = ZPSJ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected PhotoJCJ(Parcel in) {
        this.ID = (Long) in.readValue(Long.class.getClassLoader());
        this.JCJBH = in.readString();
        this.ZPBH = in.readString();
        this.ZPLX = in.readString();
        this.ZPLJ = in.readString();
        this.BZ = in.readString();
        this.ZPDZ = in.readString();
        this.ZPSJ = (Long) in.readValue(Long.class.getClassLoader());
        this.isSelected = in.readByte() != 0;
    }

    public String getName() {
        if (ZPLJ == null) {
            return "";
        }
        String path = ZPLJ;
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.ID);
        dest.writeString(this.JCJBH);
        dest.writeString(this.ZPBH);
        dest.writeString(this.ZPLX);
        dest.writeString(this.ZPLJ);
        dest.writeString(this.BZ);
        dest.writeString(this.ZPDZ);
        dest.writeValue(this.ZPSJ);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    public String getZPDZ() {
        return this.ZPDZ;
    }

    public void setZPDZ(String ZPDZ) {
        this.ZPDZ = ZPDZ;
    }

    public Long getZPSJ() {
        return this.ZPSJ;
    }

    public void setZPSJ(Long ZPSJ) {
        this.ZPSJ = ZPSJ;
    }
}
