package com.zhaoli.chickmusic.app.recyclerview.listener;

import android.support.v7.widget.RecyclerView;

/**
 * Created by zhaoli on 2015/10/18.
 *
 * 特殊滚动监听，可以监听到顶部还是底部
 *
 */
public interface IOnRecyclerViewScrollLocationListener {
    void onTopWhenScrollIdle(RecyclerView recyclerView);
    void onBottomWhenScrollIdle(RecyclerView recyclerView);
}
