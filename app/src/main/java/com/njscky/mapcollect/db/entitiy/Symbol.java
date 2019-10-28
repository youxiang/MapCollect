package com.njscky.mapcollect.db.entitiy;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 符号表
 */
@Entity(nameInDb = "SYMBOL")
public class Symbol {
    // 图片类型
    public String IMGTYPE;
    // 块名
    public String ENTNAME;
    // 图片名称
    public String PICNAME;

    @Generated(hash = 497244416)
    public Symbol(String IMGTYPE, String ENTNAME, String PICNAME) {
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
}
