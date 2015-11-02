package com.zhaoli.chickmusic.app.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by zhaoli on 2015/10/22.
 */
public class IOnScrollPostationListener extends RecyclerView.OnScrollListener {

    private boolean needMove = false;   //是否需要二次Move
    private int postation = -1;
    private LinearLayoutManager linearLayoutManager = null;
    private RecyclerView recyclerView = null;

    public IOnScrollPostationListener(LinearLayoutManager linearLayoutManager, RecyclerView recyclerView) {
        this.linearLayoutManager = linearLayoutManager;
        this.recyclerView = recyclerView;
    }

    public void moveTo(int postation) {
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = linearLayoutManager.findLastVisibleItemPosition();
        this.postation = postation;
        //然后区分情况
        if (postation <= firstItem ){
            //当要置顶的项在当前显示的第一个项的前面时
            recyclerView.scrollToPosition(postation);
        }else if (postation <= lastItem ){
            //当要置顶的项已经在屏幕上显示时
            int top = recyclerView.getChildAt(postation - firstItem).getTop();
            recyclerView.scrollBy(0, top);
        }else{
            //当要置顶的项在当前显示的最后一项的后面时
            recyclerView.scrollToPosition(postation);
            needMove = true;
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (needMove){
            needMove = false;
            //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
            int n = postation - linearLayoutManager.findFirstVisibleItemPosition();
            if ( 0 <= n && n < recyclerView.getChildCount()){
                //获取要置顶的项顶部离RecyclerView顶部的距离
                int top = recyclerView.getChildAt(n).getTop();
                //最后的移动
                recyclerView.scrollBy(0, top);
            }
        }
    }
}
