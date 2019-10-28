package com.njscky.mapcollect.business.jcjinspect;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.njscky.mapcollect.R;
import com.njscky.mapcollect.db.entitiy.JCJLineYS;
import com.njscky.mapcollect.db.entitiy.JCJPointYS;

import java.util.ArrayList;
import java.util.List;

/**
 * 展示连接点属性
 */
public class ConnectPointFragment extends Fragment {
    private static final String TAG = ConnectPointFragment.class.getSimpleName();
    RecyclerView recyclerView;
    PointPropertyListAdapter adapter;
    JCJLineYS line;
    JCJPointYS point;

    public static Fragment newInstance(JCJLineYS line, JCJPointYS point) {
        ConnectPointFragment fragment = new ConnectPointFragment();
        Bundle args = new Bundle();
        args.putParcelable("line", line);
        args.putParcelable("point", point);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect_point, container, false);
        recyclerView = view.findViewById(R.id.connect_point_property_list_view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PointPropertyListAdapter();
        recyclerView.setAdapter(adapter);

        Bundle args = getArguments();
        if (args != null) {
            line = args.getParcelable("line");
            point = args.getParcelable("point");
        }

        List<Property> properties = getProperties();
        adapter.setProperties(properties);
    }

    private List<Property> getProperties() {
        List<Property> properties = new ArrayList<>();


        if (line != null) {
            properties.add(new Property("连接点号", line.JCJBH));
            properties.add(new Property("埋深", "", true));
            properties.add(new Property("管径", line.GJ, true));
            properties.add(new Property("管材", line.CZ, true));
            properties.add(new OptionalProperty("是否与底图一致", line.SFDTYZ, new String[]{"", "是", "否"}));
            properties.add(new OptionalProperty("是否混接", line.SFHJ, new String[]{"", "是", "否"}));
            properties.add(new OptionalProperty("混接类型", line.HJLX, new String[]{"", "雨混", "污混", "合流", "片区", "其他",}, new int[]{4, 5}));
            properties.add(new Property("备注", line.BZ, true));
        }

        Log.i(TAG, "getProperties: " + properties.size());
        return properties;
    }
}
