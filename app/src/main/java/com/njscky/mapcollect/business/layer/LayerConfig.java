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

    /**
     * 检查井点参数
     *
     * @return
     */
    @NonNull
    GraphicLayerParameter pointParameter();

    /**
     * 检查井线参数
     *
     * @return
     */
    @NonNull
    GraphicLayerParameter lineParameter();


}
