package com.njscky.mapcollect.business.jcjinspect;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.esri.core.map.Graphic;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.njscky.mapcollect.MapCollectApp;
import com.njscky.mapcollect.R;
import com.njscky.mapcollect.business.layer.LayerManager;
import com.njscky.mapcollect.business.photo.AddPhotoActivity;
import com.njscky.mapcollect.business.photo.album.AlbumDirListActivity;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.db.entitiy.JCJLineYS;
import com.njscky.mapcollect.db.entitiy.JCJLineYSDao;
import com.njscky.mapcollect.db.entitiy.JCJPointYS;
import com.njscky.mapcollect.db.entitiy.JCJPointYSDao;
import com.njscky.mapcollect.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 检查井调查
 */
public class JcjInspectFragment extends Fragment {

    private static final String TAG = "JcjInspectFragment";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_jcjbh)
    EditText etJCJBH;
    @BindView(R.id.ib_add_photo)
    ImageButton ibAddPhoto;
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
    private List<ConnectPointFragment> fragments;

    private Unbinder unbiner;

    private Graphic graphic;
    private String JCJBH;

    private JCJPointYSDao pointYSDao;
    private JCJLineYSDao lineYSDao;

    private LayerManager layerManager;
    private BottomSheetBehavior behavior;

    private JCJPointYS pointYS;
    private List<JCJLineYS> lineYSList;
    private String[] arrJGCZ;
    private String[] arrJGQK;
    private String[] arrJSCZ;
    private String[] arrJSQK;
    private String[] arrFSWLX;
    private String[] arrJLX;
    private long graphicId;

    public static JcjInspectFragment newInstance(Graphic graphic) {

        Bundle args = new Bundle();
        args.putLong("graphicId", graphic.getId());
        Map<String, Object> attributes = graphic.getAttributes();
        String JCJBH = null;
        if (attributes != null) {
            JCJBH = (String) attributes.get("JCJBH");
            args.putString("JCJBH", JCJBH);
        }


        JcjInspectFragment fragment = new JcjInspectFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> {
            hideJcjInspectLayout();
        });
        graphicId = getArguments().getLong("graphicId");
        layerManager = MapCollectApp.getApp().getLayerManager();
        graphic = layerManager.getPointGraphicById(graphicId);
        JCJBH = getArguments().getString("JCJBH");

        highlightGraphic();


        pointYSDao = DbManager.getInstance(getContext()).getDaoSession().getJCJPointYSDao();
        lineYSDao = DbManager.getInstance(getContext()).getDaoSession().getJCJLineYSDao();

        arrJGCZ = getResources().getStringArray(R.array.jgcz);
        arrJGQK = getResources().getStringArray(R.array.jgqk);
        arrJSCZ = getResources().getStringArray(R.array.jscz);
        arrJSQK = getResources().getStringArray(R.array.jsqk);
        arrFSWLX = getResources().getStringArray(R.array.fswlx);
        arrJLX = getResources().getStringArray(R.array.jlx);
        loadInfo();
    }

    private void highlightGraphic() {
        if (graphic != null) {
            layerManager.highLightPointGraphic(graphic);
        }
    }

    private void unHighlightGraphic() {
        if (graphic != null) {
            layerManager.unHighlightPointGraphic();
        }
    }

    private void loadInfo() {
        AppExecutors.DB.execute(() -> {
            pointYS = pointYSDao.queryBuilder().where(JCJPointYSDao.Properties.JCJBH.eq(JCJBH)).list().get(0);
            lineYSList = lineYSDao.queryBuilder().whereOr(JCJLineYSDao.Properties.JCJBH.eq(JCJBH), JCJLineYSDao.Properties.LJBH.eq(JCJBH)).list();
            updateInfo();
        });
    }

    private void updateInfo() {
        AppExecutors.MAIN.execute(() -> {

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
                    Log.i(TAG, "onItemSelected: " + position);
                    if (TextUtils.equals((String) spJGCZ.getSelectedItem(), "其他")) {
                        etJGCZ.setVisibility(View.VISIBLE);
                    } else {
                        etJGCZ.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                    Log.i(TAG, "onNothingSelected: ");
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

            if (fragments == null) {
                fragments = new ArrayList<>();
            } else {
                fragments.clear();
            }

            layerManager.drawLines(lineYSList);
            int pipeLineCount = lineYSList.size();
            for (int i = 0; i < pipeLineCount; i++) {
                if (i == 0) {
                    layerManager.highLightLine(lineYSList.get(i));
                }
                fragments.add(ConnectPointFragment.newInstance(lineYSList.get(i), pointYS));
            }

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    layerManager.highLightLine(lineYSList.get(position));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                @Override
                public Fragment getItem(int position) {
                    return fragments.get(position);
                }

                @Override
                public int getCount() {
                    return fragments.size();
                }

                @Nullable
                @Override
                public CharSequence getPageTitle(int position) {
                    return "连接点" + (position + 1);
                }
            });

            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(fragments.size());
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

            cbSFTXWC.setChecked(pointYS.SFTXWC != null ? pointYS.SFTXWC : false);
            cbSFPZWC.setChecked(pointYS.SFPZWC != null ? pointYS.SFPZWC : false);
        });
    }

    @OnClick(R.id.ll_check)
    void onCheckLayout() {
        // Do nothing
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbiner != null) {
            unbiner.unbind();
        }

        unHighlightGraphic();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            unHighlightGraphic();
        } else {
            highlightGraphic();
        }
    }

    @OnClick(R.id.ib_add_photo)
    void onAddPhoto() {
        AddPhotoActivity.start(getActivity(), JCJBH);
    }

    @OnClick(R.id.ib_photo_library)
    void onPhotoLibrary() {
        AlbumDirListActivity.start(getActivity(), JCJBH);
    }

    @OnClick(R.id.btn_cancel)
    void onCancel() {
        hideJcjInspectLayout();
    }

    private void hideJcjInspectLayout() {
        if (behavior != null) {
            behavior.setHideable(true);
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            behavior = null;
        }
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

        for (int i = 0; i < lineYSList.size(); i++) {
            ConnectPointFragment fragment = fragments.get(i);

            JCJLineYS lineYS = lineYSList.get(i);

            fragment.updateLineValue(lineYS);
        }

        AppExecutors.DB.execute(() -> {
            boolean result = false;
            try {
                result = DbManager.getInstance(getContext()).getDaoSession().callInTx(() -> {
                    pointYSDao.update(pointYS);
                    lineYSDao.updateInTx(lineYSList);
                    return true;
                });

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                boolean finalResult = result;
                AppExecutors.MAIN.execute(() -> {
                    hideJcjInspectLayout();
                    Toast.makeText(getContext(), finalResult ? "保存成功" : "保存失败", Toast.LENGTH_SHORT).show();

                    if (finalResult) {
                        layerManager.updatePoint(graphicId, pointYS, true);
                    }
                });
            }

        });
    }

    public void setBehaviorInstance(BottomSheetBehavior bottomSheetBehavior) {
        this.behavior = bottomSheetBehavior;
    }
}
