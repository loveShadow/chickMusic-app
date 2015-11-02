package com.zhaoli.chickmusic.app.adapters;

import android.view.View;

import com.zhaoli.chickmusic.data.Song;

/**
 * Created by zhaoli on 2015/10/22.
 */
public interface IOnItemClickListener {
    public void onClicked(View view, Song song);
    public void onLongClicked(View view, Song song);
}
