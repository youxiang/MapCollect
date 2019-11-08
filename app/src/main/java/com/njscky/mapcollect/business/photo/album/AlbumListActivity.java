package com.njscky.mapcollect.business.photo.album;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.njscky.mapcollect.R;
import com.njscky.mapcollect.business.photo.PhotoHelper;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlbumListActivity extends AppCompatActivity {

    private static final int STATE_VIEW = 0;
    private static final int STATE_EDIT = 1;
    private static final String TAG = "AlbumListActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_album_list)
    RecyclerView rvAlbumList;
    @BindView(R.id.ll_bottom_menu)
    LinearLayout llBottomMenu;
    @BindView(R.id.btn_delete)
    TextView btnDelete;
    @BindView(R.id.btn_select_all)
    TextView btnSelectAll;

    AlbumListAdapter adapter;

    private int state = STATE_VIEW;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, AlbumListActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            switch (state) {
                case STATE_VIEW:
                    finish();
                    break;
                case STATE_EDIT:
                    state = STATE_VIEW;
                    refreshStatus();
                    break;
            }
        });

        rvAlbumList.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new AlbumListAdapter();
        rvAlbumList.setAdapter(adapter);

        adapter.setOnItemSelectListener(new AlbumListAdapter.OnItemSelectListener() {
            @Override
            public void onItemClick(View view, boolean selected) {
                refreshStatus();
            }
        });

        adapter.setOnItemClickListener(new AlbumListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (state == STATE_EDIT) {

                } else {

                }
            }
        });

        adapter.setOnItemLongClickListener((view, position) -> {
            if (state == STATE_VIEW) {
                state = STATE_EDIT;
                refreshStatus();
            }
        });
        loadData();
        refreshStatus();
    }

    private void loadData() {
        File photoRootDir = PhotoHelper.getPhotoRootDir(this);
        File[] childDir = photoRootDir.listFiles();

        if (childDir == null || childDir.length == 0) {
            return;
        }
        Album[] albums = new Album[childDir.length];
        for (int i = 0; i < albums.length; i++) {
            albums[i] = new Album();
            albums[i].albumDir = childDir[i];
            albums[i].isSelected = false;
        }
        adapter.setData(albums);
    }

    private void refreshStatus() {
        switch (state) {
            case STATE_VIEW:
                setTitle(R.string.title_album_list);
                toolbar.setNavigationIcon(R.drawable.ic_nav_back);
                llBottomMenu.setVisibility(View.GONE);
                break;
            case STATE_EDIT:
                setTitle(getString(R.string.title_album_list_edit, getSelectedCount()));
                toolbar.setNavigationIcon(R.drawable.ic_close);
                llBottomMenu.setVisibility(View.VISIBLE);
                int selectedCount = getSelectedCount();
                btnDelete.setEnabled(selectedCount > 0);

                if (adapter != null && selectedCount < adapter.getItemCount()) {
                    btnSelectAll.setText(R.string.select_all);
                } else {
                    btnSelectAll.setText(R.string.unselect_all);
                }
                break;
        }
        adapter.setState(state);
    }

    private int getSelectedCount() {
        if (adapter == null) {
            return 0;
        }
        return adapter.getSelectedCount();
    }

    @OnClick(R.id.btn_delete)
    void onDelete() {
        Log.i(TAG, "onDelete: ");
    }

    @OnClick(R.id.btn_select_all)
    void onSelectAll() {
        Log.i(TAG, "onSelectAll: ");

        if (adapter != null) {
            if (btnSelectAll.getText().toString().equals(getString(R.string.select_all))) {
                adapter.selectAll();
            } else {
                adapter.unselectAll();
            }
        }
    }

}
