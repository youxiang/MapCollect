package com.njscky.mapcollect.business.edit;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.CompositeSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.njscky.mapcollect.MapCollectApp;
import com.njscky.mapcollect.R;
import com.njscky.mapcollect.business.account.UserManager;
import com.njscky.mapcollect.business.layer.DefaultLayerConfig;
import com.njscky.mapcollect.business.layer.GraphicLayerParameter;
import com.njscky.mapcollect.business.layer.LayerManager;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.db.entitiy.JCJLineYSDao;
import com.njscky.mapcollect.db.entitiy.JCJPointYS;
import com.njscky.mapcollect.db.entitiy.JCJPointYSDao;
import com.njscky.mapcollect.util.AppExecutors;
import com.njscky.mapcollect.util.ChineseSupportTextSymbol;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddPointFragment extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_jcjbh)
    EditText etJCJBH;
    @BindView(R.id.ib_add_photo)
    ImageButton ibAddPhoto;
    @BindView(R.id.ib_photo_library)
    ImageButton ibPhotoLibrary;
    @BindView(R.id.sp_jgcz)
    Spinner spJGCZ;
    @BindView(R.id.et_jgcz)
    EditText etJGCZ;
    @BindView(R.id.sp_jgqk)
    Spinner spJGQK;
    @BindView(R.id.sp_jscz)
    Spinner spJSCZ;
    @BindView(R.id.et_jscz)
    EditText etJSCZ;
    @BindView(R.id.sp_jsqk)
    Spinner spJSQK;
    @BindView(R.id.et_jscc)
    EditText etJSCC;
    @BindView(R.id.sp_fswlx)
    Spinner spFSWLX;
    @BindView(R.id.sp_jlx)
    Spinner spJLX;
    @BindView(R.id.et_jlx)
    EditText etJLX;
    @BindView(R.id.et_jcjbz)
    EditText etJCJBZ;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.cb_sftxwc)
    CheckBox cbSFTXWC;
    @BindView(R.id.cb_sfpzwc)
    CheckBox cbSFPZWC;

    private Unbinder unbiner;
    private BottomSheetBehavior behavior;
    private JCJPointYSDao pointYSDao;
    private JCJLineYSDao lineYSDao;
    private String[] arrJGCZ;
    private String[] arrJGQK;
    private String[] arrJSCZ;
    private String[] arrJSQK;
    private String[] arrFSWLX;
    private String[] arrJLX;
    private JCJPointYS pointYS;

    private LayerManager layoutManager;
    private Point pt;
    private int graphicId;

    public static AddPointFragment newInstance(Point pt) {

        Bundle args = new Bundle();
        args.putSerializable("pt", pt);
        AddPointFragment fragment = new AddPointFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jcj_inspect, container, false);
        unbiner = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbiner != null) {
            unbiner.unbind();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ibAddPhoto.setVisibility(View.GONE);
        ibPhotoLibrary.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        cbSFTXWC.setVisibility(View.GONE);
        cbSFPZWC.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> {
            layoutManager.removeUnSavedPoint();
            hideAddPointLayout();
        });
        pt = (Point) getArguments().getSerializable("pt");
        pointYS = new JCJPointYS();
        pointYS.XZB = (float) pt.getY();
        pointYS.YZB = (float) pt.getX();
        layoutManager = MapCollectApp.getApp().getLayerManager();

        pointYSDao = DbManager.getInstance(getContext()).getDaoSession().getJCJPointYSDao();
        lineYSDao = DbManager.getInstance(getContext()).getDaoSession().getJCJLineYSDao();

        arrJGCZ = getResources().getStringArray(R.array.jgcz);
        arrJGQK = getResources().getStringArray(R.array.jgqk);
        arrJSCZ = getResources().getStringArray(R.array.jscz);
        arrJSQK = getResources().getStringArray(R.array.jsqk);
        arrFSWLX = getResources().getStringArray(R.array.fswlx);
        arrJLX = getResources().getStringArray(R.array.jlx);

        showPointInfo();
        loadInfo();
    }

    private void showPointInfo() {
        layoutManager.addUnsavedPoint(pointYS);
    }

    private void updatePointInfo() {
        layoutManager.updatePoint(graphicId, pointYS, false);
    }

    private Graphic getPointGraphic(JCJPointYS pointYS, Point pt, SimpleMarkerSymbol markerSymbol, DefaultLayerConfig config) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("JCJBH", pointYS.JCJBH);
        GraphicLayerParameter parameter = config.pointParameter();
        TextSymbol textSymbol = new ChineseSupportTextSymbol(parameter.annotationLayerSymbolSize, pointYS.JCJBH, parameter.annotationLayerSymbolColor);
        textSymbol.setOffsetX(5);
        textSymbol.setOffsetY(5);
        CompositeSymbol compositeSymbol = new CompositeSymbol();
        compositeSymbol.add(textSymbol);
        compositeSymbol.add(markerSymbol);
        return new Graphic(pt, compositeSymbol, attributes);
    }

    private void loadInfo() {
        etJCJBH.setText(pointYS.JCJBH);
        spJGCZ.setAdapter(new ArrayAdapter<>
                (
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        arrJGCZ
                )
        );

        spJGCZ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (TextUtils.equals((String) spJGCZ.getSelectedItem(), "其他")) {
                    etJGCZ.setVisibility(View.VISIBLE);
                } else {
                    etJGCZ.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int jgczIndex = getSelectIndex(arrJGCZ, pointYS.JGCZ);
        spJGCZ.setSelection(jgczIndex);

        etJGCZ.setText(pointYS.JGCZ_extra);

        spJGQK.setAdapter(new ArrayAdapter<>
                (
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        arrJGQK
                )
        );
        spJGQK.setSelection(getSelectIndex(arrJGQK, pointYS.JGQK));

        //若原始数据中没有值，井盖情况默认为正常
        String strJGQK = (String) spJGQK.getSelectedItem();
        if (TextUtils.equals(strJGQK.trim(), "")) {
            spJGQK.setSelection(1);
        }


        spJSCZ.setAdapter(new ArrayAdapter<>
                (
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        arrJSCZ
                )
        );
        spJSCZ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (TextUtils.equals((String) spJSCZ.getSelectedItem(), "其他")) {
                    etJSCZ.setVisibility(View.VISIBLE);
                } else {
                    etJSCZ.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        int jsczIndex = getSelectIndex(arrJSCZ, pointYS.JSCZ);
        spJSCZ.setSelection(jsczIndex);
        //若原始数据中没有值，井室材质默认为砖混
        String strJSCZ = (String) spJSCZ.getSelectedItem();
        if (TextUtils.equals(strJSCZ.trim(), "")) {
            spJSCZ.setSelection(1);
        }

        etJSCZ.setText(pointYS.JSCZ_extra);

        spJSQK.setAdapter(new ArrayAdapter<>
                (
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        arrJSQK
                )
        );
        spJSQK.setSelection(getSelectIndex(arrJSQK, pointYS.JSQK));
        //若原始数据中没有值，井室情况默认为正常
        String strJSQK = (String) spJSQK.getSelectedItem();
        if (TextUtils.equals(strJSQK.trim(), "")) {
            spJSQK.setSelection(1);
        }

        etJSCC.setText(pointYS.JSCC);

        spFSWLX.setAdapter(new ArrayAdapter<>
                (
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        arrFSWLX
                )
        );

        spFSWLX.setSelection(getSelectIndex(arrFSWLX, pointYS.FSWLX));
        //若原始数据中没有值，附属物类型默认为检查井
        String strFSWLX = (String) spFSWLX.getSelectedItem();
        if (TextUtils.equals(strFSWLX.trim(), "")) {
            spFSWLX.setSelection(2);
        }

        spJLX.setAdapter(new ArrayAdapter<>
                (
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        arrJLX
                )
        );
        spJLX.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (TextUtils.equals((String) spJLX.getSelectedItem(), "穿井")) {
                    etJLX.setVisibility(View.VISIBLE);
                } else {
                    etJLX.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        int jlxIndex = getSelectIndex(arrJLX, pointYS.JLX);
        spJLX.setSelection(jlxIndex);
        etJLX.setText(pointYS.JLX_extra);

        etJCJBZ.setText(pointYS.BZ); //增加备注
    }

    @OnClick(R.id.btn_cancel)
    void onCancel() {
        layoutManager.removeUnSavedPoint();
        hideAddPointLayout();
    }

    @OnClick(R.id.btn_save)
    void onSave() {
        String JGCZ = pointYS.JGCZ;
        String JSCZ = pointYS.JSCZ;

        pointYS.JCJBH = etJCJBH.getText().toString();
        pointYS.JGCZ = (String) spJGCZ.getSelectedItem();
        if (TextUtils.equals(pointYS.JGCZ, "其他")) {
            pointYS.JGCZ_extra = etJGCZ.getText().toString();
        }
        pointYS.JGQK = (String) spJGQK.getSelectedItem();
        pointYS.JSCZ = (String) spJSCZ.getSelectedItem();
        if (TextUtils.equals(pointYS.JSCZ, "其他")) {
            pointYS.JSCZ_extra = etJSCZ.getText().toString();
        }
        pointYS.JSQK = (String) spJSQK.getSelectedItem();
        pointYS.JSCC = etJSCC.getText().toString();
        pointYS.FSWLX = (String) spFSWLX.getSelectedItem();
        pointYS.JLX = (String) spJLX.getSelectedItem();
        pointYS.BZ = etJCJBZ.getText().toString();
        pointYS.SFTXWC = cbSFTXWC.isChecked();
        pointYS.SFPZWC = cbSFPZWC.isChecked();
        boolean sfxg = false;
        if (!TextUtils.equals(JGCZ, pointYS.JGCZ) || !TextUtils.equals(JSCZ, pointYS.JSCZ)) {
            sfxg = true;
        }
        pointYS.SFXG = sfxg ? "是" : "否";
        if (TextUtils.equals(pointYS.JLX, "穿井")) {
            pointYS.JLX_extra = etJLX.getText().toString();
        }
        pointYS.DCR = UserManager.getInstance(getContext()).getUserId();
        pointYS.DCSJ = System.currentTimeMillis();

        AppExecutors.DB.execute(() -> {
            boolean success = false;
            try {
                pointYSDao.insert(pointYS);
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                boolean finalSuccess = success;
                AppExecutors.MAIN.execute(() -> {
                    Toast.makeText(getContext(), finalSuccess ? "保存成功" : "保存失败", Toast.LENGTH_SHORT).show();
                    if (finalSuccess) {
                        updatePointInfo();
                        hideAddPointLayout();
                    }
                });
            }

        });
    }

    public void setBehaviorInstance(BottomSheetBehavior bottomSheetBehavior) {
        this.behavior = bottomSheetBehavior;
    }

    private void hideAddPointLayout() {
        if (behavior != null) {
            behavior.setHideable(true);
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            behavior = null;
        }
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
}
