package com.zhaoli.chickmusic.app.recyclerview.layoutmanager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zhaoli.chickmusic.app.recyclerview.listener.IOnRecyclerViewScrollLocationListener;

/**
 * Created by zhaoli on 2015/10/18.
 */
public class ABaseLinearLayoutManager extends LinearLayoutManager implements RecyclerViewScrollManager.IOnScrollManagerLocation {
    private RecyclerViewScrollManager recyclerViewScrollManager = null;

    public ABaseLinearLayoutManager(Context context) {
        super(context);
    }

    public ABaseLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public boolean isScrolling() {
        if (null != recyclerViewScrollManager) {
            return recyclerViewScrollManager.isScrolling();
        }
        return false;
    }

    public RecyclerViewScrollManager getRecyclerViewScrollManager() {
        ensureRecyclerViewScrollManager();
        return recyclerViewScrollManager;
    }

    public void setOnRecyclerViewScrollLocationListener(RecyclerView recyclerView, IOnRecyclerViewScrollLocationListener listener) {
        ensureRecyclerViewScrollManager();
        recyclerViewScrollManager.setOnRecyclerViewScrollLocationListener(listener);
        recyclerViewScrollManager.setOnScrollManagerLocation(this);
        recyclerViewScrollManager.registerScrollListener(recyclerView);
    }

    private void ensureRecyclerViewScrollManager() {
        if (null == recyclerViewScrollManager) {
            recyclerViewScrollManager = new RecyclerViewScrollManager();
        }
    }

    @Override
    public boolean isTop(RecyclerView recyclerView) {
        return 0 == findFirstVisibleItemPosition();
    }

    @Override
    public boolean isBottom(RecyclerView recyclerView) {
        int lastVisiblePosition = findLastVisibleItemPosition();
        int lastPosition = recyclerView.getAdapter().getItemCount() - 1;
        return lastVisiblePosition == lastPosition;
    }
}
