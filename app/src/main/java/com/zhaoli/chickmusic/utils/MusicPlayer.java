package com.zhaoli.chickmusic.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.zhaoli.chickmusic.app.CMApplication;
import com.zhaoli.chickmusic.app.eventbus.event.BufferingUpdateEvent;
import com.zhaoli.chickmusic.app.eventbus.event.MusicProgressEvent;
import com.zhaoli.chickmusic.app.eventbus.event.PlayerStateUpdateEvent;
import com.zhaoli.chickmusic.data.PlayerManager;
import com.zhaoli.chickmusic.data.PlayerState;
import com.zhaoli.chickmusic.data.Song;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhaoli on 2015/10/22.
 */
public class MusicPlayer implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private static MusicPlayer mInstance = null;

    private MediaPlayer mediaPlayer = null;
    private Timer timer = null;

    private int currentBufferingUpdateProgress = -1;        //缓冲进度

    private PlayerState currentPlayerState = PlayerState.IDLE_STATE;

    public static MusicPlayer getInstance() {
        if (mInstance == null) {
            mInstance = new MusicPlayer();
        }
        return mInstance;
    }

    /**
     * 释放
     */
    public void release() {
        changePlayerState(PlayerState.IDLE_STATE);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 获取当前播放状态
     * @return
     */
    public PlayerState getCurrentPlayerState() {
        return currentPlayerState;
    }

    public MusicPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    /**
     * 获取缓冲进度
     * @return
     */
    public int getCurrentBufferingUpdateProgress() {
        if (PlayerManager.getmInstance().getCurrentPlaySong() == null) {
            return 0;
        }
        return currentBufferingUpdateProgress;
    }

    /**
     * 设置数据源
     * @param url
     */
    public void setPlayData(String url) {
        try {
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);     //设置数据源
            changePlayerState(PlayerState.INIT_STATE);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 准备
     */
    public void preparePlayer() {
        mediaPlayer.prepareAsync();
        changePlayerState(PlayerState.PREPARING_STATE);
    }

    /**
     * 暂停
     */
    public void pause() {
        switch (currentPlayerState) {
            case IDLE_STATE:
            case PREPARING_STATE:
            case PAUSE_STATE:
            case STOP_STATE:
            case INIT_STATE:
                break;
            case START_STATE:
                mediaPlayer.pause();
                changePlayerState(PlayerState.PAUSE_STATE);
                break;
        }
    }

    /**
     * 开始播放
     */
    public void start() {
        switch (currentPlayerState) {
            case IDLE_STATE:
            case PREPARING_STATE:
            case START_STATE:
                break;
            case INIT_STATE:
                preparePlayer();
                break;
            case PREPARED_STATE:
            case PAUSE_STATE:
            case STOP_STATE:
                mediaPlayer.start();
                changePlayerState(PlayerState.START_STATE);
                break;
        }
    }

    /**
     * 停止
     */
    public void stop() {
        switch (currentPlayerState) {
            case IDLE_STATE:
            case PREPARING_STATE:
            case STOP_STATE:
            case INIT_STATE:
                break;
            case START_STATE:
            case PAUSE_STATE:
                mediaPlayer.stop();
                changePlayerState(PlayerState.STOP_STATE);
                break;
        }
    }

    /**
     * 是否在播放
     * @return
     */
    public boolean isPlaying() {
        return (currentPlayerState == PlayerState.START_STATE);
    }

    /**
     * 进度（100%）
     * @param progress
     * @return
     */
    public int seekTo(int progress) {
        if (currentBufferingUpdateProgress <= 0) {
            return 0;
        }
        int relProgress = 0;
        if (progress > currentBufferingUpdateProgress) {
            relProgress = currentBufferingUpdateProgress;
        } else {
            relProgress = progress;
        }
        mediaPlayer.seekTo(progress * mediaPlayer.getDuration() / 100);
        return relProgress;
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null) {
                return;
            }
            if (mediaPlayer.isPlaying()) {
                int position = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                if (duration > 0) {
                    int pos = 100 * position / duration;
                    CMApplication.getInstance().getEventBus().post(new MusicProgressEvent(pos));
                }
            }
        }
    };

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        currentBufferingUpdateProgress = percent;
        CMApplication.getInstance().getEventBus().post(new BufferingUpdateEvent(percent));
        if (percent == 100) {
            //缓冲完成
            mediaPlayer.setOnBufferingUpdateListener(null);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //下一曲
        PlayerManager.getmInstance().playNextSong();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        changePlayerState(PlayerState.PREPARED_STATE);
        start();
    }

    private void changePlayerState(PlayerState state) {
        currentPlayerState = state;
        CMApplication.getInstance().getEventBus().post(new PlayerStateUpdateEvent(state));
    }
}
