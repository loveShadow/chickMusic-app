package com.zhaoli.chickmusic.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaoli.chickmusic.R;
import com.zhaoli.chickmusic.app.CMApplication;
import com.zhaoli.chickmusic.app.adapters.DividerItemDecoration;
import com.zhaoli.chickmusic.app.adapters.IOnItemClickListener;
import com.zhaoli.chickmusic.app.adapters.PlayerListAdapter;
import com.zhaoli.chickmusic.app.eventbus.event.ChangeCurrentSongEvent;
import com.zhaoli.chickmusic.app.listener.IOnScrollPostationListener;
import com.zhaoli.chickmusic.base.PlayMode;
import com.zhaoli.chickmusic.data.PlayerManager;
import com.zhaoli.chickmusic.data.Song;
import com.zhaoli.chickmusic.utils.MusicPlayer;

/**
 * Created by zhaoli on 2015/10/21.
 */
public class PlayListFragment extends Fragment implements View.OnClickListener{

    private RecyclerView playListView = null;
    private Button clearPlayListButton = null;
    private Button changePlayModeButton = null;
    private TextView playListCountTextView = null;
    private ImageView blankImage = null;

    private PlayerListAdapter playerListAdapter = null;
    private LinearLayoutManager linearLayoutManager = null;
    private IOnScrollPostationListener onScrollPostationListener = null;

    private boolean needMove = false;

    public interface IOnPlayListFragmentListener {
        public void onHidePlayListFragment();
    }

    private IOnPlayListFragmentListener onPlayListFragmentListener = null;

    public PlayListFragment() {
    }

    public PlayListFragment(IOnPlayListFragmentListener onPlayListFragmentListener) {
        this.onPlayListFragmentListener = onPlayListFragmentListener;
    }

    /**
     * 重新初始化数据
     */
    public void reInit() {
        changePlayModeDrawable(PlayerManager.getmInstance().getPlayMode());
        playerListAdapter.setSongList(PlayerManager.getmInstance().getPlayerList());
        playListCountTextView.setText("播放列表（" + playerListAdapter.getItemCount() + "）");
        resetPlayerList();
    }

    /**
     * 重新刷新PlayerList
     */
    private void resetPlayerList() {
        playerListAdapter.notifyDataSetChanged();
        int index = PlayerManager.getmInstance().getCurrentSongIndex();
        if (index != -1) {
            onScrollPostationListener.moveTo(index);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist_fragment_layout, null);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        CMApplication.getInstance().getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        CMApplication.getInstance().getEventBus().unregister(this);
    }

    private void initView(View view) {
        playListView = (RecyclerView)view.findViewById(R.id.playListView);
        playListCountTextView = (TextView)view.findViewById(R.id.playlist_count);
        clearPlayListButton = (Button)view.findViewById(R.id.clear_playlist_button);
        clearPlayListButton.setOnClickListener(this);
        changePlayModeButton = (Button)view.findViewById(R.id.change_playway_button);
        changePlayModeButton.setOnClickListener(this);
        blankImage = (ImageView)view.findViewById(R.id.blankImage);
        blankImage.setOnClickListener(this);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        playListView.setLayoutManager(linearLayoutManager);
        playerListAdapter = new PlayerListAdapter(getActivity(), getSongItemListener());
        playListView.setAdapter(playerListAdapter);
        playListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        onScrollPostationListener = new IOnScrollPostationListener(linearLayoutManager, playListView);
        playListView.setOnScrollListener(onScrollPostationListener);

        reInit();
    }

    private IOnItemClickListener getSongItemListener() {
        return new IOnItemClickListener() {
            @Override
            public void onClicked(View view, Song song) {
                //播放歌曲
                PlayerManager.getmInstance().playThisSong(song);
            }

            @Override
            public void onLongClicked(View view, Song song) {

            }
        };
    }

    @Override
    public void onClick(View v) {
        if (v == blankImage && onPlayListFragmentListener != null) {
            onPlayListFragmentListener.onHidePlayListFragment();
        } else if (v == changePlayModeButton) {
            PlayerManager.getmInstance().playModePlusPlus();
            changePlayModeDrawable(PlayerManager.getmInstance().getPlayMode());
        } else if (v == clearPlayListButton) {
            showConfirmClearListDialog();
        }
    }

    private void showConfirmClearListDialog() {
    }

    private void changePlayModeDrawable(PlayMode mode) {
        switch (mode) {
            case MODE_ALL:
                changePlayModeButton.setBackgroundResource(R.drawable.playlist_mode_all_background);
                break;
            case MODE_RANDOM:
                changePlayModeButton.setBackgroundResource(R.drawable.playlist_mode_random_background);
                break;
            case MODE_SIGNAL:
                changePlayModeButton.setBackgroundResource(R.drawable.playlist_mode_single_background);
                break;
        }
    }

    public void onEventMainThread(ChangeCurrentSongEvent event) {
        resetPlayerList();
    }
}
