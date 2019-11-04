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
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.njscky.mapcollect.R;
import com.njscky.mapcollect.db.entitiy.JCJLineYS;
import com.njscky.mapcollect.db.entitiy.JCJPointYS;
import com.njscky.mapcollect.util.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 展示连接点属性
 */
public class ConnectPointFragment extends Fragment {
    private static final String TAG = ConnectPointFragment.class.getSimpleName();

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

    JCJLineYS line;
    JCJPointYS point;
    private Unbinder unbinder;
    private String[] arrSFDTYZ;
    private String[] arrSFHJ;
    private String[] arrHJLX;
    private String[] arrGC;

    public static ConnectPointFragment newInstance(JCJLineYS line, JCJPointYS point) {
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
        Log.i(TAG, "onCreateView: ");
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            line = args.getParcelable("line");
            point = args.getParcelable("point");
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        arrSFDTYZ = getResources().getStringArray(R.array.sfdtyz);
        arrSFHJ = getResources().getStringArray(R.array.sfhj);
        arrHJLX = getResources().getStringArray(R.array.hjlx);
        arrGC = getResources().getStringArray(R.array.gc);
        String JCJBH = point.JCJBH;  //增加判断，当点的JCJBH=线的JCJBH时，取LJBH的值，否则取JCJBH的值
        if (TextUtils.equals(JCJBH, line.JCJBH)) {
            etLJDH.setText(line.LJBH);
        } else {
            etLJDH.setText(line.JCJBH);
        }
        etMS.setText(String.valueOf(line.QDMS));
        etGJ.setText(line.GJ);
        spGC.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrGC));
        spGC.setSelection(getSelectIndex(arrGC, line.CZ));

        spSFDTYZ.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrSFDTYZ));
        spSFDTYZ.setSelection(getSelectIndex(arrSFDTYZ, line.SFDTYZ));

        spSFHJ.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrSFHJ));
        spSFHJ.setSelection(getSelectIndex(arrSFHJ, line.SFHJ));

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

    void updateLineValue(JCJLineYS lineYS) {
        float QDMS = lineYS.QDMS;
        float ZDMS = lineYS.ZDMS;
        String GJ = lineYS.GJ;
        String CZ = lineYS.CZ;
        String LX = lineYS.LX;  //增加流向的判断

        //lineYS.LJBH = etLJDH.getText().toString();
        if (TextUtils.equals(etLJDH.getText().toString(), lineYS.LJBH) && LX == "0") {
            lineYS.QDMS = AppUtils.parseFloat(etMS.getText().toString());
        } else if (TextUtils.equals(etLJDH.getText().toString(), lineYS.LJBH) && LX == "1") {
            lineYS.ZDMS = AppUtils.parseFloat(etMS.getText().toString());
        } else if (TextUtils.equals(etLJDH.getText().toString(), lineYS.JCJBH) && LX == "0") {
            lineYS.ZDMS = AppUtils.parseFloat(etMS.getText().toString());
        } else if (TextUtils.equals(etLJDH.getText().toString(), lineYS.JCJBH) && LX == "1") {
            lineYS.QDMS = AppUtils.parseFloat(etMS.getText().toString());
        }
        //lineYS.QDMS = AppUtils.parseFloat(etMS.getText().toString());
        lineYS.GJ = etGJ.getText().toString();
        lineYS.CZ = (String) spGC.getSelectedItem();
        lineYS.SFDTYZ = (String) spSFDTYZ.getSelectedItem();
        lineYS.SFHJ = (String) spSFHJ.getSelectedItem();
        lineYS.HJLX = (String) spHJLX.getSelectedItem();
        boolean sfxg = false;
        if (QDMS != lineYS.QDMS || ZDMS != lineYS.ZDMS || !TextUtils.equals(GJ, lineYS.GJ) || !TextUtils.equals(CZ, lineYS.CZ)) {
            sfxg = true;
        }
        lineYS.SFXG = sfxg ? "是" : "否";
        if (TextUtils.equals(lineYS.HJLX, "其他") || TextUtils.equals(lineYS.HJLX, "片区")) {
            lineYS.HJLX_extra = etHJLX.getText().toString();
        }

        lineYS.BZ = etBZ.getText().toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView: ");
        if (unbinder != null) {
            unbinder.unbind();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }
}
