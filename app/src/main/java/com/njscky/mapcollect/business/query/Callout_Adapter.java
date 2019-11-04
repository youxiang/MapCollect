package com.njscky.mapcollect.business.query;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.njscky.mapcollect.R;

import java.util.List;

public class Callout_Adapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<CalloutitemClass> mCalloutitemList;
    private Activity activity;

    public Callout_Adapter(Activity activity, List data) {
        mCalloutitemList = data;
        mLayoutInflater = activity.getLayoutInflater();
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return mCalloutitemList.size();
    }

    @Override
    public CalloutitemClass getItem(int position) {
        return mCalloutitemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View updateView;
        ViewHolder viewHolder;
        if (view == null) {
            updateView = mLayoutInflater.inflate(R.layout.layout_callout_listview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvFieldName = (TextView) updateView.findViewById(R.id.fieldName);
            viewHolder.tvFieldValue = (TextView) updateView.findViewById(R.id.fieldValue);
            updateView.setTag(viewHolder);

        } else {
            updateView = view;
            viewHolder = (ViewHolder) updateView.getTag();
        }

        final CalloutitemClass item = getItem(position);
        viewHolder.tvFieldName.setText(String.valueOf(item.getName()));
        viewHolder.tvFieldValue.setText(String.valueOf(item.getValue()));
        return updateView;
    }

    static class ViewHolder {
        TextView tvFieldName;
        TextView tvFieldValue;
    }
}
