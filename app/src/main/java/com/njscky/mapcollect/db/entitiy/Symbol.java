package com.njscky.mapcollect.db.entitiy;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * 符号表
 */
@Entity(nameInDb = "SYMBOL")
public class Symbol {
    @Id
    @Property(nameInDb = "ID")
    public Long ID;
    // 图片类型
    public String IMGTYPE;
    // 块名
    public String ENTNAME;
    // 图片名称
    public String PICNAME;

    @Generated(hash = 599279664)
    public Symbol(Long ID, String IMGTYPE, String ENTNAME, String PICNAME) {
        this.ID = ID;
        this.IMGTYPE = IMGTYPE;
        this.ENTNAME = ENTNAME;
        this.PICNAME = PICNAME;
    }

    @Generated(hash = 460475327)
    public Symbol() {
    }

    public String getIMGTYPE() {
        return this.IMGTYPE;
    }

    public void setIMGTYPE(String IMGTYPE) {
        this.IMGTYPE = IMGTYPE;
    }

    public String getENTNAME() {
        return this.ENTNAME;
    }

    public void setENTNAME(String ENTNAME) {
        this.ENTNAME = ENTNAME;
    }

    public String getPICNAME() {
        return this.PICNAME;
    }

    public void setPICNAME(String PICNAME) {
        this.PICNAME = PICNAME;
    }

    public Long getID() {
        return this.ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }
}
