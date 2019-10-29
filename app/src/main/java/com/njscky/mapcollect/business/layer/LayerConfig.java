package com.njscky.mapcollect.business.layer;

import androidx.annotation.NonNull;

public interface LayerConfig {

    /**
     * 服务器参数
     *
     * @return
     */
    @NonNull
    LayerParameter baseMapParameter();

    /**
     * 管线参数
     *
     * @return
     */
    @NonNull
    LayerParameter gxParameter();


}
