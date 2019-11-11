package com.njscky.mapcollect.business.photo.album;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.njscky.mapcollect.R;
import com.njscky.mapcollect.business.photo.DisplayPhotoActivity;
import com.njscky.mapcollect.business.photo.PhotoHelper;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.db.entitiy.PhotoJCJ;
import com.njscky.mapcollect.db.entitiy.PhotoJCJDao;
import com.njscky.mapcollect.util.AppExecutors;

import java.util.List;
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

    @BindView(R.id.empty_view)
    View emptyView;
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

    private String JCJBH;
    private String photoType;

    private PhotoAdapter adapter;

    private int state = STATE_VIEW;
    private PhotoJCJDao photoJCJDao;

    private RecyclerView.AdapterDataObserver emptyObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (adapter != null && adapter.getItemCount() > 0) {
                emptyView.setVisibility(View.GONE);
                rvPhotoList.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.VISIBLE);
                rvPhotoList.setVisibility(View.GONE);
            }
        }
    };

    public static void start(Activity activity, String JCJBH, String photoType) {
        Intent intent = new Intent(activity, AlbumListActivity.class);
        intent.putExtra("JCJBH", JCJBH);
        intent.putExtra("photoType", photoType);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
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
        JCJBH = getIntent().getStringExtra("JCJBH");
        photoType = getIntent().getStringExtra("photoType");
        rvPhotoList.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new PhotoAdapter();
        adapter.registerAdapterDataObserver(emptyObserver);
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
                    DisplayPhotoActivity.start(AlbumListActivity.this, adapter.getPhotos(), position);
                }
            }
        });
        rvPhotoList.setAdapter(adapter);
        refreshStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.unregisterAdapterDataObserver(emptyObserver);
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

    private void refreshStateView() {
        if (state == STATE_VIEW) {
            setTitle(getAlbumName());
            toolbar.setNavigationIcon(R.drawable.ic_nav_back);
            llBottomMenu.setVisibility(View.GONE);
        }
    }

    private String getAlbumName() {
        return String.format(Locale.getDefault(), "%s(%s)", photoType, JCJBH);
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
        AppExecutors.DB.execute(() -> {
            List<PhotoJCJ> photoList = photoJCJDao.queryBuilder()
                    .where(PhotoJCJDao.Properties.JCJBH.eq(JCJBH), PhotoJCJDao.Properties.ZPLX.eq(photoType))
                    .list();

            AppExecutors.MAIN.execute(() -> {
                adapter.setData(photoList);
                refreshStatus();
            });
        });
    }

    @OnClick(R.id.btn_delete)
    void onDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_delete_photo)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    doDelete();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {

                })
                .show();
    }

    private void doDelete() {
        AppExecutors.DB.execute(() -> {
            List<PhotoJCJ> photos = adapter.getSelectedPhotos();
            for (PhotoJCJ photo : photos) {
                boolean success = PhotoHelper.deletePhotoFile(this, photo);
            }
            photoJCJDao.deleteInTx(photos);

            state = STATE_VIEW;
            loadData();
        });
    }

    @OnClick(R.id.btn_select_all)
    void onSelectALl() {
        if (adapter != null) {
            if (btnSelectAll.getText().toString().equals(getString(R.string.select_all))) {
                adapter.selectAll();
            } else {
                adapter.unselectAll();
            }
        }
    }
}
