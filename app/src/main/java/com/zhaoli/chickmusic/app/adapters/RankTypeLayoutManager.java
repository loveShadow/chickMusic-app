package com.zhaoli.chickmusic.app.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhaoli on 2015/10/21.
 */
public class RankTypeLayoutManager extends GridLayoutManager {

    private RecyclerView recyclerView = null;

    public RankTypeLayoutManager(Context context, int spanCount, RecyclerView recyclerView) {
        super(context, spanCount);
        this.recyclerView = recyclerView;
    }

    public RankTypeLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        RankTypeAdapter adapter = (RankTypeAdapter)recyclerView.getAdapter();
        if (adapter != null && adapter.getItemHeight() > 0) {
            int measuredWidth = View.MeasureSpec.getSize(widthSpec);
            int measuredHeight = adapter.getItemHeight() + recyclerView.getPaddingBottom() + recyclerView.getPaddingTop();
            int line = adapter.getItemCount() / getSpanCount();
            if (adapter.getItemCount() % getSpanCount() > 0) {
                line ++;
            }
            setMeasuredDimension(measuredWidth, measuredHeight * line);
        } else {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
        }
    }
}
