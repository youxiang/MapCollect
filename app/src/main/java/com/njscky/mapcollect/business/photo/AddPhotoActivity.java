package com.njscky.mapcollect.business.photo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.njscky.mapcollect.R;
import com.njscky.mapcollect.db.DbManager;
import com.njscky.mapcollect.db.entitiy.PhotoJCJ;
import com.njscky.mapcollect.db.entitiy.PhotoJCJDao;
import com.njscky.mapcollect.util.AppExecutors;
import com.njscky.mapcollect.util.AppUtils;
import com.njscky.mapcollect.util.SpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.Gravity.BOTTOM;

/**
 * 选择照片
 */
public class AddPhotoActivity extends AppCompatActivity {

    private static final int REQ_TAKE_PHOTO = 100;
    private static final int REQ_PICK_PHOTO = 101;
    private static final int REQ_TAKE_PHOTO_PERMISSION = 1;
    private static final String TAG = "AddPhotoActivity";
    private static final int REQ_DISPLAY_PHOTO = 102;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.jcj_bh)
    TextView tvJCJBH;
    @BindView(R.id.et_remark)
    EditText etRemark;
    @BindView(R.id.sp_type)
    Spinner spPhotoType;
    @BindView(R.id.rv_photo_list)
    RecyclerView rvPhotoList;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    PhotoJCJDao photoJCJDao;
    PhotoTypeAdapter typeAdapter;
    private String JCJBH;
    private PhotoTypeItem[] photoTypeItems;
    private PhotoListAdapter photoListAdapter;
    private PopupWindow bottomPopup;
    private PopupWindowBindHelper popupWindowBindHelper = new PopupWindowBindHelper();
    private File photoFile;

    public static void start(Activity activity, String JCJBH) {
        Intent intent = new Intent(activity, AddPhotoActivity.class);
        intent.putExtra("JCJBH", JCJBH);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        photoJCJDao = DbManager.getInstance(this).getDaoSession().getPhotoJCJDao();
        JCJBH = getIntent().getStringExtra("JCJBH");
        String[] photoTypes = getResources().getStringArray(R.array.photo_type_arr);
        photoTypeItems = new PhotoTypeItem[photoTypes.length];
        for (int i = 0; i < photoTypes.length; i++) {
            photoTypeItems[i] = new PhotoTypeItem(photoTypes[i]);
        }

        tvJCJBH.setText(JCJBH);

        photoListAdapter = new PhotoListAdapter();
        photoListAdapter.setOnItemClickListener(new PhotoListAdapter.OnItemClickListener() {
            @Override
            public void onAddPhoto() {
                showPopupWindow();
            }

            @Override
            public void onItemClick(int position) {
                DisplayPhotoActivity.startForResult(AddPhotoActivity.this, (ArrayList<PhotoJCJ>) photoListAdapter.getData(), position, REQ_DISPLAY_PHOTO);
            }
        });

        rvPhotoList.setLayoutManager(new GridLayoutManager(this, 4));
        rvPhotoList.setAdapter(photoListAdapter);

        spPhotoType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PhotoTypeItem item = typeAdapter.getItem(position);
                loadData(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        typeAdapter = new PhotoTypeAdapter(this, android.R.layout.simple_spinner_dropdown_item, photoTypeItems);
        spPhotoType.setAdapter(typeAdapter);
        spPhotoType.setSelection(0, true);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_TAKE_PHOTO_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQ_TAKE_PHOTO:
                if (photoFile != null) {
                    List<PhotoJCJ> photoList = photoListAdapter.getData();
                    long time = System.currentTimeMillis();
                    for (PhotoJCJ photo : photoList) {
                        photo.ZPSJ = time;
                    }
                    Bitmap bitmap = WatermarkUtils.mark(this, photoFile, getScreenSize(), JCJBH, time);
                    PhotoHelper.save(bitmap, photoFile, () -> {
                        Log.i(TAG, "run: ");
                        updatePhotoList();
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(photoFile)));
                    });

                }
                break;
            case REQ_PICK_PHOTO:
                if (data != null) {
                    String photoPath = AppUtils.getRealPathFromUri(this, data.getData());
                    if (!TextUtils.isEmpty(photoPath)) {

                        List<PhotoJCJ> photoList = photoListAdapter.getData();
                        long time = System.currentTimeMillis();
                        for (PhotoJCJ photo : photoList) {
                            photo.ZPSJ = time;
                        }
                        File originalFile = new File(photoPath);
                        Bitmap bitmap = WatermarkUtils.mark(this, originalFile, getScreenSize(), JCJBH, time);

                        PhotoTypeItem photoType = (PhotoTypeItem) spPhotoType.getSelectedItem();
                        File photoDir = PhotoHelper.getPhotoDir(this, photoType);
                        String fileName = generateFileName(JCJBH, photoType);
                        photoFile = new File(photoDir, fileName);

                        PhotoHelper.save(bitmap, photoFile, () -> {
                            updatePhotoList();
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(photoFile)));
                        });
                    }
                }

                break;
            case REQ_DISPLAY_PHOTO:
                if (data != null) {
                    ArrayList<PhotoJCJ> photos = data.getParcelableArrayListExtra("photos");
                    photoListAdapter.setPhotos(photos);
                }
                break;
        }
    }

    private void updatePhotoList() {
        PhotoJCJ photo = new PhotoJCJ();
        photo.JCJBH = JCJBH;
        PhotoTypeItem photoTypeItem = (PhotoTypeItem) spPhotoType.getSelectedItem();
        photo.ZPLX = photoTypeItem.typeName;
        photo.ZPLJ = photoFile.getAbsolutePath();
        if (!photoListAdapter.addPhoto(photo)) {
            Toast.makeText(this, "列表中已存在该照片", Toast.LENGTH_SHORT).show();
        }
    }

    private void takePhoto() {
        PhotoTypeItem photoType = (PhotoTypeItem) spPhotoType.getSelectedItem();
        File photoDir = PhotoHelper.getPhotoDir(this, photoType);
        String fileName = generateFileName(JCJBH, photoType);
        photoFile = new File(photoDir, fileName);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.njscky.mapcollect.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQ_TAKE_PHOTO);
    }

    private String generateFileName(String JCJBH, PhotoTypeItem photoType) {
        long serialNumber = SpUtils.getInstance(this).getSerialNumber(photoType.typeShortName + JCJBH);
        String name = String.format(Locale.getDefault(), "%s%s%d.jpg", JCJBH, photoType.typeShortName, serialNumber);
        Log.i(TAG, "generateFileName: " + name);
        return name;
    }

    private void showPopupWindow() {
        assertBottomPopup();
        if (!bottomPopup.isShowing()) {
            bottomPopup.showAtLocation(rvPhotoList, BOTTOM, 0, 0);
        }
    }

    private void assertBottomPopup() {
        if (bottomPopup != null) return;
        bottomPopup = new PopupWindow(this);
        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
        ButterKnife.bind(popupWindowBindHelper, view);

        bottomPopup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        bottomPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomPopup.setBackgroundDrawable(new BitmapDrawable());
        bottomPopup.setFocusable(true);
        bottomPopup.setOutsideTouchable(true);
        bottomPopup.setContentView(view);
    }

    private void loadData(PhotoTypeItem photoType) {
        AppExecutors.DB.execute(() -> {
            List<PhotoJCJ> photoList = photoJCJDao.queryBuilder()
                    .where(PhotoJCJDao.Properties.JCJBH.eq(JCJBH), PhotoJCJDao.Properties.ZPLX.eq(photoType.typeName))
                    .list();

            AppExecutors.MAIN.execute(() -> {
                photoListAdapter.setPhotos(photoList);
                if (!photoList.isEmpty()) {
                    etRemark.setText(photoList.get(0).BZ);
                }
            });
        });

    }

    private void showBottomPopup(int position) {
        assertBottomPopup();
        if (!bottomPopup.isShowing()) {
            bottomPopup.showAtLocation(rvPhotoList, BOTTOM, 0, 0);
        }
    }

    private void dismissBottomPopup() {
        if (bottomPopup != null && bottomPopup.isShowing()) {
            bottomPopup.dismiss();
        }
    }

    private void goPhotoAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_PICK_PHOTO);
    }

    @OnClick(R.id.btn_confirm)
    void onConfirm() {
        List<PhotoJCJ> photoList = photoListAdapter.getData();

        String bz = etRemark.getText().toString();
        for (PhotoJCJ photo : photoList) {
            photo.BZ = bz;
        }

        AppExecutors.DB.execute(() -> {
            photoJCJDao.insertOrReplaceInTx(photoList);
        });
        finish();
    }

    @OnClick(R.id.btn_cancel)
    void onCancel() {
        finish();
    }

    class PopupWindowBindHelper {
        @BindView(R.id.item_popupwindows_camera)
        Button btnTakePhoto;
        @BindView(R.id.item_popupwindows_Photo)
        Button btnSelectPhoto;
        @BindView(R.id.item_popupwindows_cancel)
        Button btnCancelSelectPhoto;

        @OnClick(R.id.item_popupwindows_camera)
        public void onTakePhoto() {
            dismissBottomPopup();
            ActivityCompat.requestPermissions(AddPhotoActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_TAKE_PHOTO_PERMISSION);
        }

        @OnClick(R.id.item_popupwindows_Photo)
        public void onSelectPhoto() {
            dismissBottomPopup();
            goPhotoAlbum();
        }

        @OnClick(R.id.item_popupwindows_cancel)
        public void onCancelSelectPhoto() {
            dismissBottomPopup();
        }

    }

    private Point getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        return outSize;
    }
}
