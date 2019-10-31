package com.njscky.mapcollect.business.jcjinspect;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

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

    public static JcjInspectFragment newInstance(Graphic graphic) {

        Bundle args = new Bundle();
        args.putSerializable("graphic", graphic);

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
        graphic = (Graphic) getArguments().getSerializable("graphic");
        layerManager = MapCollectApp.getApp().getLayerManager();

        Map<String, Object> attributes = graphic.getAttributes();
        if (attributes != null) {
            JCJBH = (String) attributes.get("JCJBH");
        }

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
            layerManager.highLightGraphic(graphic);
        }
    }

    private void unHighlightGraphic() {
        if (graphic != null) {
            layerManager.unHighlightGraphic();
        }
    }

    private void loadInfo() {
        AppExecutors.DB.execute(() -> {
            pointYS = pointYSDao.queryBuilder().where(JCJPointYSDao.Properties.JCJBH.eq(JCJBH)).list().get(0);
            lineYSList = lineYSDao.queryBuilder().where(JCJLineYSDao.Properties.JCJBH.eq(JCJBH)).list();
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
            if (jgczIndex == arrJGCZ.length - 1) {
                etJGCZ.setText(pointYS.JGCZ);
            }

            spJGQK.setAdapter(new ArrayAdapter<>
                    (
                            getContext(),
                            android.R.layout.simple_list_item_1,
                            arrJGQK
                    )
            );

            spJGQK.setSelection(getSelectIndex(arrJGQK, pointYS.JGQK));

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
            if (jsczIndex == arrJSCZ.length - 1) {
                etJGCZ.setText(pointYS.JSCZ);
            }

            spJSQK.setAdapter(new ArrayAdapter<>
                    (
                            getContext(),
                            android.R.layout.simple_list_item_1,
                            arrJSQK
                    )
            );
            spJSQK.setSelection(getSelectIndex(arrJSQK, pointYS.JSQK));

            etJSCC.setText(pointYS.JSCC);

            spFSWLX.setAdapter(new ArrayAdapter<>
                    (
                            getContext(),
                            android.R.layout.simple_list_item_1,
                            arrFSWLX
                    )
            );

            spFSWLX.setSelection(getSelectIndex(arrFSWLX, pointYS.FSWLX));

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
                    if (TextUtils.equals((String) spJLX.getSelectedItem(), "其他")) {
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

            if (jlxIndex == arrJLX.length - 1) {
                etJLX.setText(pointYS.CJQK);
            }


            if (fragments == null) {
                fragments = new ArrayList<>();
            } else {
                fragments.clear();
            }

            int pipeLineCount = lineYSList.size();
            for (int i = 0; i < pipeLineCount; i++) {
                fragments.add(ConnectPointFragment.newInstance(lineYSList.get(i), pointYS));
            }

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
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        });
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
        pointYS.JCJBH = etJCJBH.getText().toString();
        pointYS.JGCZ = (String) spJGCZ.getSelectedItem();
        pointYS.JGQK = (String) spJGQK.getSelectedItem();
        pointYS.JSCZ = (String) spJSCZ.getSelectedItem();
        pointYS.JSQK = (String) spJSQK.getSelectedItem();
        pointYS.JSCC = etJSCC.getText().toString();
        pointYS.FSWLX = (String) spFSWLX.getSelectedItem();
        pointYS.JLX = (String) spJLX.getSelectedItem();

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
                });
            }

        });
    }

    public void setBehaviorInstance(BottomSheetBehavior bottomSheetBehavior) {
        this.behavior = bottomSheetBehavior;
    }
}
