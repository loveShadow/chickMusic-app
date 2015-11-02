package com.zhaoli.chickmusic.data;

import com.zhaoli.chickmusic.app.adapters.IOnItemClickListener;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhaoli on 2015/10/22.
 */
public class PlayerListData implements Serializable{

    private List<Song> songList = null;
    private Song currentSong = null;

    public PlayerListData(List<Song> songList, Song currentSong) {
        this.songList = songList;
        this.currentSong = currentSong;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }
}
