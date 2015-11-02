package com.zhaoli.chickmusic.data.rankdata;

import java.util.List;

/**
 * Created by zhaoli on 2015/10/16.
 */
public class BaiduRankSongJson {
    private List<BaiduSongJson> song_list;
    private BaiduBillboardJson billboard;

    public List<BaiduSongJson> getSong_list() {
        return song_list;
    }

    public void setSong_list(List<BaiduSongJson> song_list) {
        this.song_list = song_list;
    }

    public BaiduBillboardJson getBillboard() {
        return billboard;
    }

    public void setBillboard(BaiduBillboardJson billboard) {
        this.billboard = billboard;
    }
}
