package com.zhaoli.chickmusic.request;

import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.zhaoli.chickmusic.app.CMApplication;
import com.zhaoli.chickmusic.base.ThreadPoolUtils;
import com.zhaoli.chickmusic.data.Const;
import com.zhaoli.chickmusic.data.Song;
import com.zhaoli.chickmusic.data.rankdata.BaiduBitrateJson;
import com.zhaoli.chickmusic.data.rankdata.BaiduRankSongJson;
import com.zhaoli.chickmusic.data.rankdata.BaiduSongInfoJson;
import com.zhaoli.chickmusic.data.rankdata.BaiduSongJson;
import com.zhaoli.chickmusic.utils.AppTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoli on 2015/10/22.
 */
public class GetSongInfoRequest {

    private Response.Listener<String> getSongSuccessListener = null;
    private Response.ErrorListener getSongErrorListener = null;
    private IOnGetSongInfoListener onGetSongInfoListener = null;

    public interface IOnGetSongInfoListener {
        public void onSuccess(Song song);
        public void onFailed(Song song);
    }

    public Song requestSong = null;

    public GetSongInfoRequest(Song song, IOnGetSongInfoListener onGetSongInfoListener) {
        this.onGetSongInfoListener = onGetSongInfoListener;
        getSongSuccessListener = getGetSongSuccessListener();
        getSongErrorListener = getGetSongErrorListener();
        requestSong = song;
        String url = Const.GET_SONG_INFO + "&songid=" + song.getSongID();
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, getSongSuccessListener, getSongErrorListener);

        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                CMApplication.getInstance().addToRequestQueue(stringRequest);
            }
        });
    }

    private Response.Listener<String> getGetSongSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.length() > 7) {
                    s = s.substring(5, s.length() - 2);
                }
                Gson gson = new Gson();
                BaiduSongInfoJson songInfoJson;
                try {
                    songInfoJson = gson.fromJson(s, BaiduSongInfoJson.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    onGetSongInfoListener.onFailed(requestSong);
                    return;
                }
                boolean isSuccessed = true;
                BaiduBitrateJson baiduBitrateJson = null;
                BaiduSongJson baiduSongJson = null;
                if (songInfoJson != null) {
                    baiduBitrateJson = songInfoJson.getBitrate();
                    baiduSongJson = songInfoJson.getSonginfo();
                    if (baiduBitrateJson != null && baiduSongJson != null) {
                        Song song = new Song(baiduSongJson);
                        String realUrl = getRealDownUrl(baiduBitrateJson.getFile_link());
                        if (! realUrl.isEmpty()) {
                            song.setDownUrl(realUrl);
                            onGetSongInfoListener.onSuccess(song);
                            return;
                        }
                    }
                }
                onGetSongInfoListener.onFailed(requestSong);
            }
        };
    }

    private Response.ErrorListener getGetSongErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        };
    }

    private String getRealDownUrl(String url) {
        int index = url.indexOf("?xcode");
        if (index != -1) {
            String temp = url.substring(0, index);
            if (temp.contains("yinyueshiting")) {
                temp = temp.replace("yinyueshiting", "musicdata");
                return temp;
            }
        }
        return "";
    }
}
