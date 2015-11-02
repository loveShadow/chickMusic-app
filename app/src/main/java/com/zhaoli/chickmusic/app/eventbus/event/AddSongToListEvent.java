package com.zhaoli.chickmusic.app.eventbus.event;

import com.zhaoli.chickmusic.data.Song;

/**
 * Created by zhaoli on 2015/10/22.
 *
 * 添加歌曲
 */
public class AddSongToListEvent {
    public int[] startPos = null;
    private Song song = null;

    public AddSongToListEvent(int[] startPos, Song song) {
        this.startPos = startPos;
        this.song = song;
    }

    public int[] getStartPos() {
        return startPos;
    }

    public Song getSong() {
        return song;
    }
}
