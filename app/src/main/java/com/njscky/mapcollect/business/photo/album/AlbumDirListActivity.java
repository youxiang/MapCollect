package com.njscky.mapcollect.business.photo.album;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.njscky.mapcollect.R;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.db.entitiy.PhotoJCJ;
import com.njscky.mapcollect.db.entitiy.PhotoJCJDao;
import com.njscky.mapcollect.util.AppExecutors;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlbumDirListActivity extends AppCompatActivity {

    private static final int STATE_VIEW = 0;
    private static final int STATE_EDIT = 1;
    private static final String TAG = "AlbumDirListActivity";
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

    AlbumDirListAdapter adapter;

    private PhotoJCJDao photoJCJDao;
    private int state = STATE_VIEW;

    private String JCJBH;

    public static void start(Activity activity, String JCJBH) {
        Intent intent = new Intent(activity, AlbumDirListActivity.class);
        intent.putExtra("JCJBH", JCJBH);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_dir_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        JCJBH = getIntent().getStringExtra("JCJBH");
        photoJCJDao = DbManager.getInstance(this).getDaoSession().getPhotoJCJDao();
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

        rvAlbumList.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new AlbumDirListAdapter();
        rvAlbumList.setAdapter(adapter);

        adapter.setOnItemSelectListener(new AlbumDirListAdapter.OnItemSelectListener() {
            @Override
            public void onItemClick(View view, boolean selected) {
                refreshStatus();
            }
        });

        adapter.setOnItemClickListener(new AlbumDirListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (state == STATE_EDIT) {

                } else {
                    AlbumListActivity.start(AlbumDirListActivity.this, adapter.getItem(position));
                }
            }
        });

        adapter.setOnItemLongClickListener((view, position) -> {
            if (state == STATE_VIEW) {
                state = STATE_EDIT;
                refreshStatus();
                adapter.setState(state);
            }
        });
        loadData();
        refreshStatus();
    }

    private void loadData() {
        AppExecutors.DB.execute(new Runnable() {
            @Override
            public void run() {
                List<PhotoJCJ> photoJCJList
                        = photoJCJDao.queryBuilder()
                        .where(PhotoJCJDao.Properties.JCJBH.eq(JCJBH))
                        .list();

                Map<String, AlbumDir> typeToPhotoMap = new HashMap<>();
                for (PhotoJCJ photoJCJ : photoJCJList) {
                    String key = photoJCJ.ZPLX;
                    AlbumDir value = typeToPhotoMap.get(key);
                    if (value == null) {
                        value = new AlbumDir();
                        value.dirName = key;
                        value.JCJBH = JCJBH;
                        typeToPhotoMap.put(key, value);
                    }
                    value.addPhoto(photoJCJ);
                }

                Collection<AlbumDir> albumDirs = typeToPhotoMap.values();

                AppExecutors.MAIN.execute(() -> {
                    adapter.setData(albumDirs);
                });

            }
        });


    }

    private void refreshStateView() {
        if (state == STATE_VIEW) {
            setTitle(getString(R.string.title_album_list, JCJBH));
            toolbar.setNavigationIcon(R.drawable.ic_nav_back);
            llBottomMenu.setVisibility(View.GONE);
        }
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

    private int getSelectedCount() {
        if (adapter == null) {
            return 0;
        }
        return adapter.getSelectedCount();
    }

    @OnClick(R.id.btn_delete)
    void onDelete() {
        Log.i(TAG, "onDelete: ");
        Toast.makeText(this, "Not Implemented", Toast.LENGTH_SHORT).show();
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
