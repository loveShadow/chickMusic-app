package com.zhaoli.chickmusic.data;

/**
 * Created by zhaoli on 2015/10/16.
 */
public class Const {
    public static final String BASE_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&callback=&from=webapp_music&method=";
    public static final String RANK_LIST_SONG_URL = BASE_URL + "baidu.ting.billboard.billList";

    public static final String SEARCH_SONG_URL = "http://s.music.163.com/search/get/";
    public static final String GET_SONG_INFO = BASE_URL + "baidu.ting.song.play";

    public static final int NEW_SONG_TYPE = 1;      //新歌榜
    public static final int HOT_SONG_TYPE = 2;      //热歌榜
    public static final int KTV_HOT_SONG_TYPE = 6;  //KTV热歌榜
    public static final int ROCK_SONG_TYPE = 11;    //摇滚榜
    public static final int JAZZ_SONG_TYPE = 12;    //爵士
    public static final int POP_SONG_TYPE = 16;     //流行
    public static final int CH_SONG_TYPE = 20;      //华语金曲榜
    public static final int EU_SONG_TYPE = 21;      //欧美金曲榜
    public static final int OLD_SONG_TYPE = 22;     //经典老歌榜
    public static final int LOVE_SONG_TYPE = 23;    //情歌对唱榜
    public static final int MOVIE_SONG_TYPE = 24;   //影视金曲榜
    public static final int NETWORK_SONG_TYPE = 25; //网络歌曲榜

    //随机歌曲 Type
    public static final int[] RANDOM_TYPE_LIST = new int[]{
            NEW_SONG_TYPE,
            HOT_SONG_TYPE,
            KTV_HOT_SONG_TYPE,
            OLD_SONG_TYPE,
            CH_SONG_TYPE,
            POP_SONG_TYPE
    };

    //榜单歌曲 Type
    public static final int[] RANK_TYPE_LIST = new int[]{
            NEW_SONG_TYPE,
            HOT_SONG_TYPE,
            KTV_HOT_SONG_TYPE,
            ROCK_SONG_TYPE,
            JAZZ_SONG_TYPE,
            POP_SONG_TYPE,
            CH_SONG_TYPE,
            EU_SONG_TYPE,
            OLD_SONG_TYPE,
            LOVE_SONG_TYPE,
            MOVIE_SONG_TYPE,
            NETWORK_SONG_TYPE
    };

}
