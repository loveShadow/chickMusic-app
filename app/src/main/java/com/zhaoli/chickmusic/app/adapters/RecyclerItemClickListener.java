package com.zhaoli.chickmusic.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zhaoli on 2015/10/21.
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private View actionDownView = null;
    private OnItemClickListener onItemClickListener = null;

    public static interface OnItemClickListener {
        public void onItemClicked(View view, int position);
    }

    public RecyclerItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                actionDownView = rv.findChildViewUnder(e.getX(), e.getY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (actionDownView != null) {
                    View upView = rv.findChildViewUnder(e.getX(), e.getY());
                    if (upView == actionDownView && onItemClickListener != null) {
                        onItemClickListener.onItemClicked(upView, rv.getChildPosition(upView));
                    }
                }
                break;
        }
         return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }
}
