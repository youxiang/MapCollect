package com.njscky.mapcollect.db.entitiy;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * 综合注记
 */
@Entity(nameInDb = "ANNOZH")
public class AnnoZH {

    @Id
    @Property(nameInDb = "ID")
    public Long ID;

    // 物探点号
    public String WTDH;

    // 管类代码
    public String GLDM;

    // X坐标
    public float XZB;

    // Y坐标
    public float YZB;

    // 旋转角
    public float XZJ;

    // 注记内容
    public String ZJNR;

    // 备注
    public String BZ;

    // 地上地下标识
    public String DSDXBS;

    // 公共非公共标识
    public String GGFGG;

    @Generated(hash = 233272714)
    public AnnoZH(Long ID, String WTDH, String GLDM, float XZB, float YZB,
            float XZJ, String ZJNR, String BZ, String DSDXBS, String GGFGG) {
        this.ID = ID;
        this.WTDH = WTDH;
        this.GLDM = GLDM;
        this.XZB = XZB;
        this.YZB = YZB;
        this.XZJ = XZJ;
        this.ZJNR = ZJNR;
        this.BZ = BZ;
        this.DSDXBS = DSDXBS;
        this.GGFGG = GGFGG;
    }

    @Generated(hash = 903266909)
    public AnnoZH() {
    }

    public String getWTDH() {
        return this.WTDH;
    }

    public void setWTDH(String WTDH) {
        this.WTDH = WTDH;
    }

    public String getGLDM() {
        return this.GLDM;
    }

    public void setGLDM(String GLDM) {
        this.GLDM = GLDM;
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

    public float getXZJ() {
        return this.XZJ;
    }

    public void setXZJ(float XZJ) {
        this.XZJ = XZJ;
    }

    public String getZJNR() {
        return this.ZJNR;
    }

    public void setZJNR(String ZJNR) {
        this.ZJNR = ZJNR;
    }

    public String getBZ() {
        return this.BZ;
    }

    public void setBZ(String BZ) {
        this.BZ = BZ;
    }

    public String getDSDXBS() {
        return this.DSDXBS;
    }

    public void setDSDXBS(String DSDXBS) {
        this.DSDXBS = DSDXBS;
    }

    public String getGGFGG() {
        return this.GGFGG;
    }

    public void setGGFGG(String GGFGG) {
        this.GGFGG = GGFGG;
    }

    public Long getID() {
        return this.ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

}
