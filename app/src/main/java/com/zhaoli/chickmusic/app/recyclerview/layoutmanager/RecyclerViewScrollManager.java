package com.zhaoli.chickmusic.app.recyclerview.layoutmanager;

import android.support.v7.widget.RecyclerView;

import com.zhaoli.chickmusic.app.recyclerview.listener.IOnRecyclerViewScrollListener;
import com.zhaoli.chickmusic.app.recyclerview.listener.IOnRecyclerViewScrollLocationListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoli on 2015/10/18.
 */
public class RecyclerViewScrollManager {
    private List<IOnRecyclerViewScrollListener> scrollListeners = null;

    public static interface IOnScrollManagerLocation {
        boolean isTop(RecyclerView recyclerView);
        boolean isBottom(RecyclerView recyclerView);
    }

    private IOnRecyclerViewScrollLocationListener onRecyclerViewScrollLocationListener = null;
    private IOnScrollManagerLocation onScrollManagerLocation = null;

    private boolean isScrolling;

    private RecyclerView.OnScrollListener onScrollListener = null;

    public void setOnRecyclerViewScrollLocationListener(IOnRecyclerViewScrollLocationListener onRecyclerViewScrollLocationListener) {
        this.onRecyclerViewScrollLocationListener = onRecyclerViewScrollLocationListener;
    }

    public void setOnScrollManagerLocation(IOnScrollManagerLocation onScrollManagerLocation) {
        this.onScrollManagerLocation = onScrollManagerLocation;
    }

    public boolean isScrolling() {
        return isScrolling;
    }

    public void registerScrollListener(RecyclerView recyclerView) {
        addScrollListener(recyclerView, new IOnRecyclerViewScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isScrolling = false;
                    if (null != onRecyclerViewScrollLocationListener) {
                        checkBottomWhenScrollIdle(recyclerView);
                        checkTopWhenScrollIdle(recyclerView);
                    }
                } else {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }

            private void checkBottomWhenScrollIdle(RecyclerView recyclerView) {
                if (onScrollManagerLocation.isBottom(recyclerView)) {
                    onRecyclerViewScrollLocationListener.onBottomWhenScrollIdle(recyclerView);
                }
            }

            private void checkTopWhenScrollIdle(RecyclerView recyclerView) {
                if (onScrollManagerLocation.isTop(recyclerView)) {
                    onRecyclerViewScrollLocationListener.onTopWhenScrollIdle(recyclerView);
                }
            }
        });
    }

    public void addScrollListener(RecyclerView recyclerView, IOnRecyclerViewScrollListener onRecyclerViewScrollListener) {
        if (null == onRecyclerViewScrollListener) {
            return;
        }
        if (null == scrollListeners) {
            scrollListeners = new ArrayList<>();
        }
        scrollListeners.add(onRecyclerViewScrollListener);
        ensureScrollListener(recyclerView);
    }

    private void ensureScrollListener(RecyclerView recyclerView) {
        if (null == onScrollListener) {
            onScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    for (IOnRecyclerViewScrollListener listener : scrollListeners) {
                        listener.onScrollStateChanged(recyclerView, newState);
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    for (IOnRecyclerViewScrollListener listener : scrollListeners) {
                        listener.onScrolled(recyclerView, dx, dy);
                    }
                }
            };
            recyclerView.setOnScrollListener(onScrollListener);
        }
    }
}
