package com.bytepace.gallerytest.adapters;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytepace.gallerytest.R;
import com.bytepace.gallerytest.utils.MediaUtils;
import com.bytepace.gallerytest.utils.VideoRequestHandler;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Viktor on 02.03.2018.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.BaseHolder> {

    private static final int PIC_RESIZE_W = 300;
    private static final int PIC_RESIZE_H = 180;
    static final int VIEWTYPE_HEADER = 2;
    static final int VIEWTYPE_NORMAL = 1;

    private List<File> files;
    private VideoRequestHandler videoRequestHandler;
    private Picasso picassoInstance;

    public GalleryAdapter(Context context) {
        super();
        files = new ArrayList<>();
        videoRequestHandler = new VideoRequestHandler();
        picassoInstance = new Picasso.Builder(context.getApplicationContext()).addRequestHandler(videoRequestHandler).build();
    }

    public void addFiles(List<File> files) {
        for (File f : files) {
            if (!this.files.contains(f)) {
                this.files.addAll(files);
            }
        }
    }

    public void update() {
        List<File> list = new ArrayList<>();
        for (File f : files) {
            if (f != null) {
                list.add(f);
            }
        }
        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return (int) Math.signum(o1.lastModified() - o2.lastModified());
            }
        });
        files.clear();
        int month = -100;
        int year = -100;
        Calendar c = Calendar.getInstance();
        for (File f : list) {
            c.setTime(new Date(f.lastModified()));
            if (month != c.get(Calendar.MONTH) || year != c.get(Calendar.YEAR)) {
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);
                files.add(null);
            }
            files.add(f);
        }
        notifyDataSetChanged();
    }

    public void clearFiles() {
        this.files.clear();
    }

    @Override
    public GalleryAdapter.BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEWTYPE_NORMAL: {
                return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_media_thumbnail, parent, false));
            }
            case VIEWTYPE_HEADER: {
                return new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header, parent, false));
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.BaseHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEWTYPE_NORMAL: {
                ((ItemHolder) holder).bind(files.get(position));
                break;
            }
            case VIEWTYPE_HEADER: {
                Date d = new Date(files.get(position + 1).lastModified());
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                ((HeaderHolder) holder).bind(c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + c.get(Calendar.YEAR));
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (files.get(position) != null) {
            return VIEWTYPE_NORMAL;
        } else {
            return VIEWTYPE_HEADER;
        }
    }

    abstract class BaseHolder extends RecyclerView.ViewHolder {

        public BaseHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class HeaderHolder extends BaseHolder {

        @BindView(R.id.text)
        TextView text;

        public HeaderHolder(View itemView) {
            super(itemView);
        }

        void bind(String date) {
            text.setText(date);
        }
    }

    class ItemHolder extends BaseHolder {

        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.line)
        View line;
        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.text_small)
        TextView text_small;
        @BindView(R.id.text_size)
        TextView text_size;

        ItemHolder(View itemView) {
            super(itemView);
        }

        void bind(File file) {
            if (MediaUtils.isImageFile(file.getAbsolutePath())) {
                picassoInstance.load(file).centerInside().resize(PIC_RESIZE_W, PIC_RESIZE_H).into(image);
                line.setBackgroundColor(ActivityCompat.getColor(line.getContext(), R.color.pink));
            } else if (MediaUtils.isVideoFile(file.getAbsolutePath())) {
                picassoInstance.load(VideoRequestHandler.SCHEME_VIDEO + ":" + file.getPath()).resize(PIC_RESIZE_W, PIC_RESIZE_H).centerInside().into(image);
                line.setBackgroundColor(ActivityCompat.getColor(line.getContext(), R.color.orange));
            } else if (MediaUtils.isAudioFile(file.getAbsolutePath())) {
                picassoInstance.load(R.drawable.ic_sound).resize(PIC_RESIZE_W, PIC_RESIZE_H).centerInside().into(image);
                line.setBackgroundColor(ActivityCompat.getColor(line.getContext(), R.color.purple));
            }
            text.setText(file.getName());
            Date d = new Date(file.lastModified());
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm");
            text_small.setText(format.format(d));
            text_size.setText(MediaUtils.getFileSize(file));
        }
    }
}
