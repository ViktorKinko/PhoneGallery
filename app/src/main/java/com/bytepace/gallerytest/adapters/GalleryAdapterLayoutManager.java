package com.bytepace.gallerytest.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by Viktor on 02.03.2018.
 */

public class GalleryAdapterLayoutManager extends GridLayoutManager {
    GalleryAdapter adapter;
    public GalleryAdapterLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, GalleryAdapter adapter) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(adapter);
    }

    public GalleryAdapterLayoutManager(Context context, int spanCount, GalleryAdapter adapter) {
        super(context, spanCount);
        init(adapter);
    }

    public GalleryAdapterLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout, GalleryAdapter adapter) {
        super(context, spanCount, orientation, reverseLayout);
        init(adapter);
    }

    private void init(GalleryAdapter _adapter){
        this.adapter = _adapter;
        this.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position) == GalleryAdapter.VIEWTYPE_HEADER) return 3;
                else return 1;
            }
        });
    }
}
