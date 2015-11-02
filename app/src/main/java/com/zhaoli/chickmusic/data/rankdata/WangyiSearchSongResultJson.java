package com.zhaoli.chickmusic.data.rankdata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoli on 2015/10/18.
 */
public class WangyiSearchSongResultJson {
    private WangyiSearchSongJson result;
    private int code;

    public boolean isOK() {
        return (code == 200);
    }

    public WangyiSearchSongJson getResult() {
        return result;
    }

    public void setResult(WangyiSearchSongJson result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public class WangyiSearchSongJson {
        private int songCount;
        private List<WangyiSongJson> songs;

        public int getSongCount() {
            return songCount;
        }

        public void setSongCount(int songCount) {
            this.songCount = songCount;
        }

        public List<WangyiSongJson> getSongs() {
            return (songs == null) ? new ArrayList<WangyiSongJson>() : songs;
        }

        public void setSongs(List<WangyiSongJson> songs) {
            this.songs = songs;
        }
    }

}