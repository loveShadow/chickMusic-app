package com.zhaoli.chickmusic.data.rankdata;

/**
 * Created by zhaoli on 2015/10/22.
 */
public class BaiduSongInfoJson {
    private BaiduSongJson songinfo;
    private BaiduBitrateJson bitrate;

    public BaiduSongJson getSonginfo() {
        return songinfo;
    }

    public void setSonginfo(BaiduSongJson songinfo) {
        this.songinfo = songinfo;
    }

    public BaiduBitrateJson getBitrate() {
        return bitrate;
    }

    public void setBitrate(BaiduBitrateJson bitrate) {
        this.bitrate = bitrate;
    }
}
