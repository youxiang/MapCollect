package com.njscky.mapcollect.business.jcjinspect;

import android.os.Bundle;
import android.text.TextUtils;
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
    @BindView(R.id.et_gc)
    EditText etGC;
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
        etLJDH.setText(line.LJBH);
        etMS.setText(String.valueOf(line.QDMS));
        etGJ.setText(line.GJ);
        etGC.setText(line.CZ);
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

        if (hjlxIndex == arrHJLX.length - 1 || hjlxIndex == arrHJLX.length - 2) {
            etHJLX.setText(line.HJLX);
        }
        etBZ.setText(line.BZ);
    }

    void updateLineValue(JCJLineYS lineYS) {
        lineYS.LJBH = etLJDH.getText().toString();
        lineYS.QDMS = AppUtils.parseFloat(etMS.getText().toString());
        lineYS.GJ = etGJ.getText().toString();
        lineYS.CZ = etGC.getText().toString();
        lineYS.SFDTYZ = (String) spSFDTYZ.getSelectedItem();
        lineYS.SFHJ = (String) spSFHJ.getSelectedItem();
        lineYS.HJLX = (String) spHJLX.getSelectedItem();
        lineYS.BZ = etBZ.getText().toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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

}
