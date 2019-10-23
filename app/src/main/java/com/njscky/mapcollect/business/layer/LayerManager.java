package com.njscky.mapcollect.business.layer;

import android.content.Context;

import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.db.dao.JCJPointYSDao;

public class LayerManager {

    private volatile static LayerManager instance;
    private final Context context;

    private YSPointLayerManager ysPointLayerManager;

    private DbManager dbManager;

    private LayerManager(Context context) {
        this.context = context;
        dbManager = DbManager.getInstance(context);
        ysPointLayerManager = new YSPointLayerManager(dbManager.getDao(JCJPointYSDao.class), "雨水管点_检查井", "雨水管点_检查井注记");
    }

    public static LayerManager getInstance(Context context) {
        if (instance == null) {
            synchronized (LayerManager.class) {
                if (instance == null) {
                    instance = new LayerManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 加载雨水检查井点图层
     *
     * @param callback
     */
    public void loadYSPointLayer(LayerCallback callback) {
        ysPointLayerManager.loadLayer(callback);
    }
}
