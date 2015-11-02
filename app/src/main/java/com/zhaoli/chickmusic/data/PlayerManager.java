package com.zhaoli.chickmusic.data;

import android.content.Context;

import com.zhaoli.chickmusic.app.CMApplication;
import com.zhaoli.chickmusic.app.eventbus.event.ChangeCurrentSongEvent;
import com.zhaoli.chickmusic.app.eventbus.event.PlayerListEmptyEvent;
import com.zhaoli.chickmusic.base.PlayMode;
import com.zhaoli.chickmusic.base.ThreadPoolUtils;
import com.zhaoli.chickmusic.utils.MusicPlayer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zhaoli on 2015/10/21.
 */
public class PlayerManager {
    private static PlayerManager mInstance = null;
    private List<Song>  playerList = null;
    private PlayMode playMode = PlayMode.MODE_ALL;
    private Song currentPlaySong = null;

    private Random random = null;

    private PlayerManager() {
        playerList = new ArrayList<>();
        random = new Random(System.currentTimeMillis());
        readPlayerList();
    }

    public static PlayerManager getmInstance() {
        if (mInstance == null) {
            mInstance = new PlayerManager();
        }
        return mInstance;
    }

    public boolean hasCurrentSong() {
        return (currentPlaySong != null);
    }

    public int getCurrentSongIndex() {
        if (currentPlaySong == null) {
            return -1;
        }
        return playerList.indexOf(currentPlaySong);
    }

    public void addSongToList(Song song) {
        addSongToList(song, false);
    }

    /**
     *
     * @param song
     * @param isPlay    是否立即播放
     */
    public void addSongToList(Song song, boolean isPlay) {
        if (playerList.contains(song)) {
            return;
        } else {
            boolean beforIsEmpty = false;
            if (playerList.isEmpty()) {
                //之前是空的
                beforIsEmpty = true;
            }
            playerList.add(playerList.size(), song);
            if (beforIsEmpty) {
                changeCurrentPlaySong(song);
            } else if (isPlay) {
                changeCurrentPlaySong(song);
            }
        }
        savePlayerList();
    }

    public void deleteSongFromList(Song song) {
        if (playerList.contains(song)) {
            int index = playerList.indexOf(song);
            playerList.remove(song);
            if (song != null && song.equals(currentPlaySong)) {
                //删除的是当前歌曲
                if (! playerList.isEmpty()) {
                    if ((playerList.size() - 1) <= index) {
                        index = 0;
                    }
                    changeCurrentPlaySong(playerList.get(index));
                } else {
                    currentPlaySong = null;
                    MusicPlayer.getInstance().stop();
                    CMApplication.getInstance().getEventBus().post(new PlayerListEmptyEvent());
                }
            }
        }
        savePlayerList();
    }

    public void playNextSong() {
        if (playerList.isEmpty()) {
            return;
        }
        switch (playMode) {
            case MODE_ALL:
                int index = playerList.indexOf(currentPlaySong);
                changeCurrentToNextSong(index);
                break;
            case MODE_SIGNAL:
                changeCurrentPlaySong(currentPlaySong);
                break;
            case MODE_RANDOM:
                changeCurrentPlaySong(playerList.get(random.nextInt(playerList.size())));
                break;
        }
    }

    public void playThisSong(Song song) {
        if (song == null || ! playerList.contains(song)) {
            return;
        }
        changeCurrentPlaySong(song);
    }

    public List<Song> getPlayerList() {
        return playerList;
    }

    public void playModePlusPlus() {
        switch (playMode) {
            case MODE_ALL:
                playMode = PlayMode.MODE_RANDOM;
                break;
            case MODE_RANDOM:
                playMode = PlayMode.MODE_SIGNAL;
                break;
            case MODE_SIGNAL:
                playMode = PlayMode.MODE_ALL;
                break;
        }
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public Song getCurrentPlaySong() {
        return currentPlaySong;
    }

    public void startPlayCurrentSong() {
        if (currentPlaySong == null) {
            return;
        }
        changeCurrentPlaySong(currentPlaySong);
    }

    private void changeCurrentToNextSong(int currentIndex) {
        if (currentIndex + 1 < playerList.size()) {
            changeCurrentPlaySong(playerList.get(currentIndex + 1));
        } else {
            changeCurrentPlaySong(playerList.get(0));
        }
    }

    private void changeCurrentPlaySong(final Song song) {
        if (song == null) {
            throw new IllegalArgumentException("song is null");
        }
        currentPlaySong = song;
        //播放
        if (song.getDownUrl() != null && ! song.getDownUrl().isEmpty()) {
            ThreadPoolUtils.execute(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.getInstance().setPlayData(song.getDownUrl());
                    MusicPlayer.getInstance().preparePlayer();
                }
            });
        }
        savePlayerList();
        CMApplication.getInstance().getEventBus().post(new ChangeCurrentSongEvent(song));
    }

    public void savePlayerList() {
        try {
            FileOutputStream stream = CMApplication.getInstance().getApplicationContext()
                    .openFileOutput("PlayerListData", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(stream);
            oos.writeObject(new PlayerListData(playerList, currentPlaySong));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readPlayerList() {
        PlayerListData data;
        try {
            FileInputStream stream = CMApplication.getInstance().getApplicationContext()
                    .openFileInput("PlayerListData");
            ObjectInputStream ois = new ObjectInputStream(stream);
            data = (PlayerListData)ois.readObject();
            if (data != null)
            {
                playerList = data.getSongList();
                currentPlaySong = data.getCurrentSong();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
