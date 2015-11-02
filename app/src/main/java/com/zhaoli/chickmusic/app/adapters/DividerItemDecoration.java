package com.zhaoli.chickmusic.app.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhaoli on 2015/10/22.
 *
 * 分割线
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;   //水平排序
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;       //垂直排序

    private static final int[] ATTRS = new int[] {android.R.attr.listDivider};
    private int orientation;
    private Drawable divider;

    public DividerItemDecoration(Context context, int orientation) {
        TypedArray typedArray = context.obtainStyledAttributes(ATTRS);
        divider = typedArray.getDrawable(0);
        typedArray.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        this.orientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (orientation == HORIZONTAL_LIST) {
            drawHorizontal(c, parent);
        } else {
            drawVertical(c, parent);
        }
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i ++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams)child.getLayoutParams();
            final int left = child.getRight() + lp.rightMargin;
            final int right = left + divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i ++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams)child.getLayoutParams();
            final int top = child.getBottom() + lp.bottomMargin;
            final int bottom = top + divider.getIntrinsicHeight();
            divider.setBounds(left + 30, top, right - 30, bottom);
            divider.draw(canvas);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (orientation == HORIZONTAL_LIST) {
            outRect.set(0, 0, 0, divider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, divider.getIntrinsicWidth(), 0);
        }
    }
}
