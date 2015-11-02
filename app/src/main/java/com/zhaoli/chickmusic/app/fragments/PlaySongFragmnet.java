package com.zhaoli.chickmusic.app.fragments;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.zhaoli.chickmusic.R;
import com.zhaoli.chickmusic.app.CMApplication;
import com.zhaoli.chickmusic.app.eventbus.event.BufferingUpdateEvent;
import com.zhaoli.chickmusic.app.eventbus.event.ChangeCurrentSongEvent;
import com.zhaoli.chickmusic.app.eventbus.event.MusicProgressEvent;
import com.zhaoli.chickmusic.app.eventbus.event.PlayerListEmptyEvent;
import com.zhaoli.chickmusic.app.eventbus.event.PlayerStateUpdateEvent;
import com.zhaoli.chickmusic.data.PlayerManager;
import com.zhaoli.chickmusic.data.Song;
import com.zhaoli.chickmusic.utils.AppTools;
import com.zhaoli.chickmusic.utils.DensityUtils;
import com.zhaoli.chickmusic.utils.MusicPlayer;

/**
 * Created by zhaoli on 2015/10/21.
 */
public class PlaySongFragmnet extends Fragment implements View.OnClickListener{

    private ImageView songImageView = null;
    private SeekBar songSeekBar = null;
    private TextView songName = null;
    private TextView singName = null;
    private Button playButton;
    private Button nextSongButton;
    private Button playListButton;

    public interface IOnPlaySongFragmentListener {
        public void onPlayListButtonClicked();
    }

    private IOnPlaySongFragmentListener onPlaySongFragmentListener = null;

    public PlaySongFragmnet() {
    }

    public PlaySongFragmnet(IOnPlaySongFragmentListener onPlaySongFragmentListener) {
        this.onPlaySongFragmentListener = onPlaySongFragmentListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playsong_fragment_layout, null);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //读取当前歌曲信息
        readCurrentSongInfo();
        CMApplication.getInstance().getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        CMApplication.getInstance().getEventBus().unregister(this);
    }

    private void initView(View view) {
        songImageView = (ImageView)view.findViewById(R.id.songImageView);
        songSeekBar = (SeekBar)view.findViewById(R.id.songSeekBar);
        songSeekBar.setOnSeekBarChangeListener(getOnSeekBarChangeListener());
        songName = (TextView)view.findViewById(R.id.songName);
        singName = (TextView)view.findViewById(R.id.singerName);
        playButton = (Button)view.findViewById(R.id.playButton);
        playButton.setOnClickListener(this);
        nextSongButton = (Button)view.findViewById(R.id.nextSongButton);
        nextSongButton.setOnClickListener(this);
        playListButton = (Button)view.findViewById(R.id.playListButton);
        playListButton.setOnClickListener(this);
    }

    private void readCurrentSongInfo() {
        Song song = PlayerManager.getmInstance().getCurrentPlaySong();
        if (song == null) {
            return;
        }
        changeSongInfo(song);
        changePlayButtonState(MusicPlayer.getInstance().isPlaying());
        songSeekBar.setSecondaryProgress(MusicPlayer.getInstance().getCurrentBufferingUpdateProgress());
    }

    private SeekBar.OnSeekBarChangeListener getOnSeekBarChangeListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            private int relProgress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                relProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (PlayerManager.getmInstance().hasCurrentSong()) {
                    relProgress = MusicPlayer.getInstance().seekTo(relProgress);
                    seekBar.setProgress(relProgress);
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (v == playListButton && onPlaySongFragmentListener != null) {
            onPlaySongFragmentListener.onPlayListButtonClicked();
        } else if (v == nextSongButton) {
            PlayerManager.getmInstance().playNextSong();
        } else if (v == playButton) {
            if (PlayerManager.getmInstance().hasCurrentSong()) {
                if (MusicPlayer.getInstance().isPlaying()) {
                    MusicPlayer.getInstance().pause();
                    changePlayButtonState(false);
                } else {
                    MusicPlayer.getInstance().start();
                    changePlayButtonState(true);
                }
            } else {
                AppTools.showToast("您的播放列表还是空的，赶快添加歌曲吧~", PlaySongFragmnet.this.getActivity());
            }
        }
    }

    public void onEventMainThread(PlayerStateUpdateEvent event) {
        switch (event.getCurrentState()) {
            case IDLE_STATE:
            case INIT_STATE:
            case STOP_STATE:
                songSeekBar.setSecondaryProgress(0);
            case PREPARING_STATE:
                songSeekBar.setProgress(0);
                changeSongInfo(PlayerManager.getmInstance().getCurrentPlaySong());
                changePlayButtonState(false);
                break;
            case PAUSE_STATE:
                changePlayButtonState(false);
                break;
            case START_STATE:
                changePlayButtonState(true);
                break;
        }
    }

    public void onEventMainThread(PlayerListEmptyEvent event) {
        songImageView.setImageResource(R.mipmap.defult_music_icon);
        songName.setText("小鸡音乐");
        singName.setText("小鸡音乐");
        songSeekBar.setProgress(0);
        songSeekBar.setSecondaryProgress(0);
        changePlayButtonState(false);
    }

    public void onEventMainThread(MusicProgressEvent event) {
        songSeekBar.setProgress(event.getProgress());
    }

    public void onEventMainThread(BufferingUpdateEvent event) {
        songSeekBar.setSecondaryProgress(event.getPercent());
    }

    private void changeSongInfo(Song song) {
        if (song.getSingerPicUrl() != null && ! song.getSingerPicUrl().isEmpty()) {
            CMApplication.getInstance().getImageLoader().get(song.getSingerPicUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    songImageView.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }, DensityUtils.dp2px(getActivity(), 60), DensityUtils.dp2px(getActivity(), 60));
        }
        songName.setText(song.getSongName());
        singName.setText(song.getSingerName());
    }

    private void changePlayButtonState(boolean isPlaying) {
        playButton.setBackgroundResource(isPlaying ? R.drawable.playsong_pausesong_background : R.drawable.playsong_startplay_background);
    }
}
