package com.zhaoli.chickmusic.app.recyclerview.listener;

import android.support.v7.widget.RecyclerView;

/**
 * Created by zhaoli on 2015/10/18.
 *
 * 普通滚动监听，可以添加多个
 *
 */
public interface IOnRecyclerViewScrollListener {
    public void onScrollStateChanged(RecyclerView recyclerView, int newState);
    public void onScrolled(RecyclerView recyclerView, int dx, int dy);
}
