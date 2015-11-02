package com.zhaoli.chickmusic.data.rankdata;

import java.util.List;

/**
 * Created by zhaoli on 2015/10/16.
 */
public class RankSongJson {
    private List<SongJson> song_list;

    public List<SongJson> getSong_list() {
        return song_list;
    }

    public void setSong_list(List<SongJson> song_list) {
        this.song_list = song_list;
    }
}
