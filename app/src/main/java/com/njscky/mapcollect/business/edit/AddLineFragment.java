package com.njscky.mapcollect.business.edit;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.esri.core.map.Graphic;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.njscky.mapcollect.MapCollectApp;
import com.njscky.mapcollect.R;
import com.njscky.mapcollect.business.account.UserManager;
import com.njscky.mapcollect.business.layer.LayerManager;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.db.entitiy.JCJLineYS;
import com.njscky.mapcollect.db.entitiy.JCJLineYSDao;
import com.njscky.mapcollect.db.entitiy.JCJPointYS;
import com.njscky.mapcollect.db.entitiy.JCJPointYSDao;
import com.njscky.mapcollect.util.AppExecutors;
import com.njscky.mapcollect.util.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddLineFragment extends Fragment {

    private static final String TAG = AddLineFragment.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_jcjbh)
    EditText etJCJBH;
    @BindView(R.id.et_ljdh)
    EditText etLJDH;
    @BindView(R.id.et_ms)
    EditText etMS;
    @BindView(R.id.et_gj)
    EditText etGJ;
    @BindView(R.id.sp_gc)
    Spinner spGC;
    @BindView(R.id.sp_sfdtyz)
    Spinner spSFDTYZ;
    @BindView(R.id.sp_sfhj)
    Spinner spSFHJ;
    @BindView(R.id.sp_hjlx)
    Spinner spHJLX;
    @BindView(R.id.et_hjlx)
    EditText etHJLX;
    @BindView(R.id.et_bz)
    EditText etBZ;

    private Unbinder unbinder;
    private String[] arrSFDTYZ;
    private String[] arrSFHJ;
    private String[] arrHJLX;
    private String[] arrGC;

    private JCJPointYS startPoint;
    private JCJPointYS endPoint;
    private JCJLineYS line;
    private boolean isLineExists;

    private Long startGraphicId;
    private Long endGraphicId;
    private String startJCJBH;
    private String endJCJBH;
    private JCJPointYSDao pointDao;
    private JCJLineYSDao lineDao;
    private BottomSheetBehavior behavior;
    private LayerManager layoutManager;

    public static AddLineFragment newInstance(Graphic startPoint, Graphic endPoint) {

        Bundle args = new Bundle();
        args.putLong("startGraphicId", startPoint.getId());
        args.putLong("endGraphicId", endPoint.getId());
        Map<String, Object> attributes = startPoint.getAttributes();
        String startJCJBH = null;
        if (attributes != null) {
            startJCJBH = (String) attributes.get("JCJBH");
            args.putString("startJCJBH", startJCJBH);
        }

        attributes = endPoint.getAttributes();
        String endJCJBH;
        if (attributes != null) {
            endJCJBH = (String) attributes.get("JCJBH");
            args.putString("endJCJBH", endJCJBH);
        }

        AddLineFragment fragment = new AddLineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_line, container, false);
        Log.i(TAG, "onCreateView: ");
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            startGraphicId = args.getLong("startPoint");
            endGraphicId = args.getLong("endPoint");
            startJCJBH = args.getString("startJCJBH");
            endJCJBH = args.getString("endJCJBH");
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        layoutManager = MapCollectApp.getApp().getLayerManager();
        pointDao = DbManager.getInstance(getContext()).getDaoSession().getJCJPointYSDao();
        lineDao = DbManager.getInstance(getContext()).getDaoSession().getJCJLineYSDao();

        toolbar.setNavigationOnClickListener(v -> {
            hideAddLineLayout();
        });
        arrSFDTYZ = getResources().getStringArray(R.array.sfdtyz);
        arrSFHJ = getResources().getStringArray(R.array.sfhj);
        arrHJLX = getResources().getStringArray(R.array.hjlx);
        arrGC = getResources().getStringArray(R.array.gc);
        loadData();
    }

    private void loadData() {
        AppExecutors.DB.execute(() -> {
            startPoint = pointDao.queryBuilder().where(JCJPointYSDao.Properties.JCJBH.eq(startJCJBH)).list().get(0);
            endPoint = pointDao.queryBuilder().where(JCJPointYSDao.Properties.JCJBH.eq(endJCJBH)).list().get(0);

            Log.i(TAG, "loadData: startPoint " + startPoint);
            Log.i(TAG, "loadData: endPoint " + endPoint);
            List<JCJLineYS> queryResult = lineDao.queryBuilder()
                    .where(JCJLineYSDao.Properties.JCJBH.eq(startJCJBH), JCJLineYSDao.Properties.LJBH.eq(endJCJBH))
                    .list();
            Log.i(TAG, "loadData: queryResult " + queryResult);
            isLineExists = !queryResult.isEmpty();
            if (isLineExists) {
                line = queryResult.get(0);
            } else {
                line = new JCJLineYS();
                line.JCJBH = startPoint.JCJBH;
                line.LJBH = endPoint.JCJBH;
                line.QDXZB = startPoint.XZB;
                line.QDYZB = startPoint.YZB;
                line.ZDXZB = endPoint.XZB;
                line.ZDYZB = endPoint.YZB;
            }
            List<JCJLineYS> lineList = new ArrayList<>();
            lineList.add(line);
            layoutManager.drawLines(lineList);
            Log.i(TAG, "loadData: line " + line);
            AppExecutors.MAIN.execute(() -> {
                updateUI();
            });
        });
    }

    private void updateUI() {

        etJCJBH.setText(line.JCJBH);
        etLJDH.setText(line.LJBH);
        if (TextUtils.equals(line.LX, "0")) {
            etMS.setText(String.valueOf(line.ZDMS));
        } else {
            etMS.setText(String.valueOf(line.QDMS));
        }
        etGJ.setText(line.GJ);
        spGC.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrGC));
        spGC.setSelection(getSelectIndex(arrGC, line.CZ));

        spSFDTYZ.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrSFDTYZ));
        spSFDTYZ.setSelection(getSelectIndex(arrSFDTYZ, line.SFDTYZ));
        //若原始数据中没有值，是否混接默认为否
        String strSFDTYZ = (String) spSFDTYZ.getSelectedItem();
        if (TextUtils.equals(strSFDTYZ.trim(), "")) {
            spSFDTYZ.setSelection(1);
        }

        spSFHJ.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrSFHJ));
        spSFHJ.setSelection(getSelectIndex(arrSFHJ, line.SFHJ));
        //若原始数据中没有值，是否混接默认为否
        String strSFHJ = (String) spSFHJ.getSelectedItem();
        if (TextUtils.equals(strSFHJ.trim(), "")) {
            spSFHJ.setSelection(2);
        }

        spHJLX.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrHJLX));
        spHJLX.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) spHJLX.getSelectedItem();
                if (TextUtils.equals(item, "其他") || TextUtils.equals(item, "片区")) {
                    etHJLX.setVisibility(View.VISIBLE);
                } else {
                    etHJLX.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        int hjlxIndex = getSelectIndex(arrHJLX, line.HJLX);
        spHJLX.setSelection(hjlxIndex);
        etHJLX.setText(line.HJLX_extra);
        etBZ.setText(line.BZ);
    }

    private int getSelectIndex(String[] list, String item) {
        if (TextUtils.isEmpty(item)) {
            return 0;
        }
        for (int i = 0; i < list.length; i++) {
            if (TextUtils.equals(list[i], item)) {
                return i;
            }
        }

        return list.length - 1;
    }

    public void setBehaviorInstance(BottomSheetBehavior bottomSheetBehavior) {
        this.behavior = bottomSheetBehavior;
    }

    @OnClick(R.id.btn_cancel)
    void onCancel() {
        hideAddLineLayout();
    }

    @OnClick(R.id.btn_save)
    void onSave() {
        float QDMS = line.QDMS;
        float ZDMS = line.ZDMS;
        String GJ = line.GJ;
        String CZ = line.CZ;
        String LX = line.LX;  //增加流向的判断

        if (TextUtils.equals(etLJDH.getText().toString(), line.LJBH) && TextUtils.equals(LX, "1")) {
            line.QDMS = AppUtils.parseFloat(etMS.getText().toString());
        } else if (TextUtils.equals(etLJDH.getText().toString(), line.LJBH) && TextUtils.equals(LX, "0")) {
            line.ZDMS = AppUtils.parseFloat(etMS.getText().toString());
        } else if (TextUtils.equals(etLJDH.getText().toString(), line.JCJBH) && TextUtils.equals(LX, "1")) {
            line.ZDMS = AppUtils.parseFloat(etMS.getText().toString());
        } else if (TextUtils.equals(etLJDH.getText().toString(), line.JCJBH) && TextUtils.equals(LX, "0")) {
            line.QDMS = AppUtils.parseFloat(etMS.getText().toString());
        } else {
            line.QDMS = AppUtils.parseFloat(etMS.getText().toString());
        }
        //line.QDMS = AppUtils.parseFloat(etMS.getText().toString());
        line.GJ = etGJ.getText().toString();
        line.CZ = (String) spGC.getSelectedItem();
        line.SFDTYZ = (String) spSFDTYZ.getSelectedItem();
        line.SFHJ = (String) spSFHJ.getSelectedItem();
        line.HJLX = (String) spHJLX.getSelectedItem();
        line.DCR = UserManager.getInstance(getContext()).getUserId();
        line.DCSJ = System.currentTimeMillis();
        boolean sfxg = false;
        if (QDMS != line.QDMS || ZDMS != line.ZDMS || !TextUtils.equals(GJ, line.GJ) || !TextUtils.equals(CZ, line.CZ)) {
            sfxg = true;
        }
        line.SFXG = sfxg ? "是" : "否";
        if (TextUtils.equals(line.HJLX, "其他") || TextUtils.equals(line.HJLX, "片区")) {
            line.HJLX_extra = etHJLX.getText().toString();
        }

        line.BZ = etBZ.getText().toString();

        AppExecutors.DB.execute(() -> {
            if (isLineExists) {
                lineDao.update(line);
            } else {
                lineDao.insert(line);
            }

            AppExecutors.MAIN.execute(() -> {
                hideAddLineLayout();
            });
        });

    }

    private void hideAddLineLayout() {
        layoutManager.removeUnSavedLine();
        if (behavior != null) {
            behavior.setHideable(true);
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            behavior = null;
        }
    }
}
