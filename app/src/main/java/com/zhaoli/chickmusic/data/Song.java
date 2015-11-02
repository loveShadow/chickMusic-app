package com.zhaoli.chickmusic.data;

import com.zhaoli.chickmusic.data.rankdata.BaiduSongJson;
import com.zhaoli.chickmusic.data.rankdata.WangyiSingerJson;
import com.zhaoli.chickmusic.data.rankdata.WangyiSongJson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhaoli on 2015/10/16.
 */
public class Song implements Serializable{
    private int songType = -1;      //0 百度  1 网易
    private String songID;
    private String songName;
    private String singerName;
    private String singerPicUrl;
    private String downUrl;
    private String lycUrl;

    public Song() {
    }

    public Song(BaiduSongJson baiduSongJson) {
        songType = 0;
        songID = baiduSongJson.getSong_id();
        songName = baiduSongJson.getTitle();
        singerName = baiduSongJson.getAuthor();
        lycUrl = baiduSongJson.getLrclink();
        singerPicUrl = baiduSongJson.getPic_premium();
    }

    public Song(WangyiSongJson wangyiSongJson) {
        songType = 1;
        songID = String.valueOf(wangyiSongJson.getId());
        songName = wangyiSongJson.getName();
        List<WangyiSingerJson> singerJsons = wangyiSongJson.getArtists();
        if (singerJsons != null && !singerJsons.isEmpty()) {
            singerName = singerJsons.get(0).getName();
            singerPicUrl = singerJsons.get(0).getPicUrl();
        }
        downUrl = wangyiSongJson.getAudio();
    }

    public String getSongID() {
        return songID;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSinger() {
        return singerName;
    }

    public void setSinger(String singerName) {
        this.singerName = singerName;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getLycUrl() {
        return lycUrl;
    }

    public void setLycUrl(String lycUrl) {
        this.lycUrl = lycUrl;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getSingerPicUrl() {
        return singerPicUrl;
    }

    public void setSingerPicUrl(String singerPicUrl) {
        this.singerPicUrl = singerPicUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Song song = (Song) o;

        if (songType == song.songType) {
            return !(songID != null ? !songID.equals(song.songID) : song.songID != null);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return songID != null ? songID.hashCode() : 0;
    }
}
