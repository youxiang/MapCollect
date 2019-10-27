package com.njscky.mapcollect.db.entitiy;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

public class PhotoJCJ {

    // 检查井编号
    @PrimaryKey
    @NonNull
    public String JCJBH;

    // 照片编号
    public String ZPBH;

    // 照片类型
    public String ZPLX;

    // 照片路径
    public String ZPLJ;

    // 备注
    public String BZ;
}
