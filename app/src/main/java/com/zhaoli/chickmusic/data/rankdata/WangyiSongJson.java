package com.zhaoli.chickmusic.data.rankdata;

import java.util.List;

/**
 * Created by zhaoli on 2015/10/18.
 */
public class WangyiSongJson {
    private int id;
    private String name;
    private List<WangyiSingerJson> artists;
    private String audio;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WangyiSingerJson> getArtists() {
        return artists;
    }

    public void setArtists(List<WangyiSingerJson> artist) {
        this.artists = artist;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }
}
