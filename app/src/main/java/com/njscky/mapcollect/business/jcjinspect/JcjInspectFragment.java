package com.njscky.mapcollect.business.jcjinspect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.esri.core.map.Graphic;
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
import butterknife.Unbinder;

/**
 * 检查井调查
 */
public class JcjInspectFragment extends Fragment {

    private static final String TAG = "JcjInspectFragment";

    @BindView(R.id.rv_property_list)
    RecyclerView rvPropertyList;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private List<Fragment> fragments;


    private Unbinder unbiner;

    private Graphic graphic;
    private String JCJBH;

    private JCJPointYSDao pointYSDao;
    private JCJLineYSDao lineYSDao;
    private PointPropertyListAdapter pointPropertyListAdapter;

    private LayerManager layerManager;

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
        AppExecutors.DB.execute(new Runnable() {
            @Override
            public void run() {
                JCJPointYS pointYS = pointYSDao.queryBuilder().where(JCJPointYSDao.Properties.JCJBH.eq(JCJBH)).list().get(0);

                List<JCJLineYS> lineYSList = lineYSDao.queryBuilder().where(JCJLineYSDao.Properties.JCJBH.eq(JCJBH)).list();

                showInfo(pointYS, lineYSList);
            }
        });
    }

    private void showInfo(JCJPointYS pointYS, List<JCJLineYS> lineYSList) {
        AppExecutors.MAIN.execute(new Runnable() {
            @Override
            public void run() {
                List<Property> pointProperties = getPointProperties(pointYS);
                pointPropertyListAdapter = new PointPropertyListAdapter();
                pointPropertyListAdapter.setOnItemClickListener(new PointPropertyListAdapter.OnItemClickListener() {
                    @Override
                    public void onAddPhoto() {
                        AddPhotoActivity.start(getActivity(), pointYS.JCJBH);
                    }

                    @Override
                    public void onViewPhoto() {
//                        DisplayPhotoActivity.start(getActivity(), );
                    }
                });
                GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
                rvPropertyList.setLayoutManager(layoutManager);
                rvPropertyList.setNestedScrollingEnabled(false);
                rvPropertyList.setAdapter(pointPropertyListAdapter);
                pointPropertyListAdapter.setProperties(pointProperties);

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
            }
        });
    }

    private List<Property> getPointProperties(JCJPointYS pipePoint) {
        List<Property> rst = new ArrayList<>();
        rst.add(new PhotoProperty("检查井编号", pipePoint.JCJBH));
        rst.add(new OptionalProperty("井盖材质", pipePoint.JGCZ, new String[]{"", "铸铁", "塑料", "矼", "其他"}, new int[]{4}));
        rst.add(new OptionalProperty("井盖情况", pipePoint.JGQK, new String[]{"", "正常", "破损", "错盖"}));
        rst.add(new OptionalProperty("井室材质", pipePoint.JSCZ, new String[]{"", "砖混", "塑料", "矼", "其他"}, new int[]{4}));
        rst.add(new OptionalProperty("井室情况", pipePoint.JSQK, new String[]{"", "正常", "破损", "渗漏"}));
        rst.add(new Property("井室尺寸", pipePoint.JSCC, true));
        rst.add(new OptionalProperty("附属物类型", pipePoint.FSWLX, new String[]{"", "雨篦", "排放口"}));
//        rst.add(new OptionalProperty("井类型", pipePoint.JLX, new String[]{"", "交叉井", "截流井", "节点井","传井"}, new int[]{4}));
        return rst;
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
}
