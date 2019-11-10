package com.njscky.mapcollect.business.photo.album;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.njscky.mapcollect.R;
import com.njscky.mapcollect.business.photo.DisplayPhotoActivity;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlbumListActivity extends AppCompatActivity {

    public static final int STATE_VIEW = 0;
    public static final int STATE_EDIT = 1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_album_list)
    RecyclerView rvPhotoList;
    @BindView(R.id.ll_bottom_menu)
    LinearLayout llBottomMenu;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    @BindView(R.id.btn_select_all)
    Button btnSelectAll;

    private AlbumDir albumDir;
    private PhotoAdapter adapter;

    private int state = STATE_VIEW;

    public static void start(Activity activity, AlbumDir item) {
        Intent intent = new Intent(activity, AlbumListActivity.class);
        intent.putExtra("albumDir", item);
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
                    adapter.setState(state);
                    break;
            }
        });
        albumDir = getIntent().getParcelableExtra("albumDir");
        rvPhotoList.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new PhotoAdapter();
        adapter.setOnItemLongClickListener(new PhotoAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                if (state == STATE_VIEW) {
                    state = STATE_EDIT;
                    refreshStatus();
                    adapter.setState(state);
                }
            }
        });
        adapter.setOnItemSelectListener(new PhotoAdapter.OnItemSelectListener() {
            @Override
            public void onItemSelect(View view, boolean selected) {
                refreshStatus();
            }
        });
        adapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (state == STATE_VIEW) {
                    DisplayPhotoActivity.start(AlbumListActivity.this, albumDir.photos, position);
                }
            }
        });
        rvPhotoList.setAdapter(adapter);
        refreshStatus();
        loadData();
    }

    private void refreshStatus() {
        switch (state) {
            case STATE_VIEW:
                refreshStateView();
                break;
            case STATE_EDIT:
                refreshStateEdit();
                break;
        }
    }

    private void refreshStateView() {
        if (state == STATE_VIEW) {
            setTitle(getAlbumName());
            toolbar.setNavigationIcon(R.drawable.ic_nav_back);
            llBottomMenu.setVisibility(View.GONE);
        }
    }

    private String getAlbumName() {
        if (albumDir != null) {
            return String.format(Locale.getDefault(), "%s(%s)", albumDir.dirName, albumDir.JCJBH);
        }
        return "";
    }

    private void refreshStateEdit() {
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
    }

    private int getSelectedCount() {
        if (adapter == null) {
            return 0;
        }
        return adapter.getSelectedCount();
    }

    private void loadData() {
        if (albumDir != null && albumDir.photos != null) {
            adapter.setData(albumDir.photos);
        }
    }

    @OnClick(R.id.btn_delete)
    void onDelete() {
        Toast.makeText(this, "Unimplemented", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_select_all)
    void onSelectALl() {

        Toast.makeText(this, "Unimplemented", Toast.LENGTH_SHORT).show();
    }
}
