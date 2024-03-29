package com.njscky.mapcollect.business.query;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.esri.core.tasks.identify.IdentifyResult;
import com.njscky.mapcollect.R;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QueryResultActivity extends AppCompatActivity {

    IdentifyResult result;
    @BindView(R.id.rv_property_list)
    RecyclerView rvQueryResult;
    private Map<String, Object> map;
    private QueryResultListAdapter queryResultListAdapter;

    public static void start(Activity activity, IdentifyResult[] results) {
        if (results != null && results.length > 0) {
            Intent intent = new Intent(activity, QueryResultActivity.class);
            intent.putExtra("result", results[0]);
            activity.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_query_result);
        ButterKnife.bind(this);
        result = (IdentifyResult) getIntent().getSerializableExtra("result");
        map = result.getAttributes();

        queryResultListAdapter = new QueryResultListAdapter(map);
        rvQueryResult.setAdapter(queryResultListAdapter);

    }
}
