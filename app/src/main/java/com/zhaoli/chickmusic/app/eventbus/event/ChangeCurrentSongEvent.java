package com.zhaoli.chickmusic.app.eventbus.event;

import com.zhaoli.chickmusic.data.Song;

/**
 * Created by zhaoli on 2015/10/22.
 *
 * 更换当前歌曲
 */
public class ChangeCurrentSongEvent {
    private Song song;

    public ChangeCurrentSongEvent(Song song) {
        this.song = song;
    }

    public Song getSong() {
        return song;
    }
}
