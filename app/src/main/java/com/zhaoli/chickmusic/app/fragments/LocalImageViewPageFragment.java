package com.zhaoli.chickmusic.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhaoli.chickmusic.R;
import com.zhaoli.chickmusic.data.Const;

/**
 * Created by zhaoli on 2015/10/17.
 */
public class LocalImageViewPageFragment extends Fragment implements ViewPager.OnPageChangeListener,
        View.OnClickListener{
    private final int CHNAGE_TIME = 3 * 1000;

    private ViewPager viewPager = null;
    private LinearLayout viewGroup = null;
    private ImageView[] tips = null;
    private ImageView[] imageViews = null;
    private int[] imageIdArray = new int[]{
            R.mipmap.zh_music_tab,
            R.mipmap.network_hot_music_tab,
            R.mipmap.move_music_tab,
            R.mipmap.ea_music_tab
    };

    private int[] rankTypeList = new int[]{
            Const.CH_SONG_TYPE,
            Const.NETWORK_SONG_TYPE,
            Const.MOVIE_SONG_TYPE,
            Const.EU_SONG_TYPE
    };

    private IOnItemClickListener onItemClickListener = null;
    private ImageViewPageAdapter adapter = null;

    public interface IOnItemClickListener {
        void onItemClicked(int rankType);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_viewpage_layout, null);
        initView(view);
        return view;
    }

    public void setOnItemClickListener(IOnItemClickListener listener) {
        onItemClickListener = listener;
    }

    private void initView(View view) {
        viewPager = (ViewPager)view.findViewById(R.id.viewPage);
        viewGroup = (LinearLayout)view.findViewById(R.id.viewGroup);

        tips = new ImageView[imageIdArray.length];
        for (int i = 0; i < tips.length; i ++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(20, 20));
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_nofocused);
            }

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = 20;
            lp.rightMargin = 20;
            viewGroup.addView(tips[i], lp);
        }

        imageViews = new ImageView[imageIdArray.length];
        for (int i = 0; i <imageViews.length; i ++) {
            ImageView imageView = new ImageView(getActivity());
            imageViews[i] = imageView;
            imageView.setOnClickListener(this);
            imageView.setBackgroundResource(imageIdArray[i]);
        }

        adapter = new ImageViewPageAdapter();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
        startAutoChangePage();
    }

    private class ImageViewPageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return imageViews.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews[position]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = imageViews[position];
            container.addView(imageView, 0);
            return imageView;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        changeTipBackground(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void changeTipBackground(int selectIndex) {
        currentIndex = selectIndex;
        for (int i = 0; i < tips.length; i ++) {
            if (i == selectIndex) {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_nofocused);
            }
        }
    }

    @Override
    public void onClick(View view) {
        for (int i = 0; i <imageViews.length; i ++) {
            if (view == imageViews[i] && onItemClickListener != null) {
                onItemClickListener.onItemClicked(rankTypeList[i]);
                break;
            }
        }
    }

    private void startAutoChangePage() {
        UIHandler.sendMessageDelayed(UIHandler.obtainMessage(AUTO_CHANGE_PAGE), CHNAGE_TIME);
    }

    private void stopAutoChangePage() {
        UIHandler.removeMessages(AUTO_CHANGE_PAGE);
    }

    private int currentIndex = 0;
    private final int AUTO_CHANGE_PAGE = 1;
    private Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AUTO_CHANGE_PAGE:
                    int index = currentIndex + 1;
                    if (index >= imageViews.length) {
                        index = 0;
                    }
                    viewPager.setCurrentItem(index);
                    UIHandler.sendMessageDelayed(UIHandler.obtainMessage(AUTO_CHANGE_PAGE), CHNAGE_TIME);
                    break;
            }
        }
    };
}
