package com.bytepace.gallerytest.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bytepace.gallerytest.R;
import com.bytepace.gallerytest.adapters.GalleryAdapter;
import com.bytepace.gallerytest.adapters.GalleryAdapterLayoutManager;
import com.bytepace.gallerytest.utils.MediaUtils;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_REQUEST = 100;

    @BindView(R.id.recycler_gallery)
    RecyclerView recycler_gallery;
    @BindView(R.id.progress)
    ProgressBar progress;

    GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initRecyclerGallery();
        hasPermissionReadStorage();
    }

    public boolean hasPermissionReadStorage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE_REQUEST);
            return false;
        } else {
            return true;
        }
    }

    public void initRecyclerGallery() {
        if (adapter == null) {
            adapter = new GalleryAdapter(this);
            GridLayoutManager manager = new GalleryAdapterLayoutManager(this, 3, adapter);
            recycler_gallery.setLayoutManager(manager);
        }
        recycler_gallery.setAdapter(adapter);
    }

    @OnClick(R.id.btn_scan)
    public void onScan() {
        if (hasPermissionReadStorage()) {
            progress.setVisibility(View.VISIBLE);
            adapter.clearFiles();
            Observable.create(new Observable.OnSubscribe<List<File>>() {
                @Override
                public void call(Subscriber<? super List<File>> subscriber) {
                    subscriber.onNext(MediaUtils.getMediaFilesList(Environment.getExternalStorageDirectory().getAbsolutePath()));
                    subscriber.onNext(MediaUtils.getMediaFilesList(Environment.getDataDirectory().getAbsolutePath()));
                    subscriber.onNext(MediaUtils.getMediaFilesList(Environment.getDownloadCacheDirectory().getAbsolutePath()));
                    subscriber.onNext(MediaUtils.getMediaFilesList(Environment.getRootDirectory().getAbsolutePath()));
                    subscriber.onNext(MediaUtils.getMediaFilesList(getFilesDir().getAbsolutePath()));
                    subscriber.onCompleted();
                }
            })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Subscriber<List<File>>() {
                        @Override
                        public void onCompleted() {
                            progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(List<File> files) {
                            showRecyclerGallery(files);
                        }
                    });
        }
    }

    public void showRecyclerGallery(List<File> files) {
        adapter.addFiles(files);
        adapter.update();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_STORAGE_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onScan();
                }
            }
        }
    }
}
