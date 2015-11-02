package com.zhaoli.chickmusic.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhaoli.chickmusic.R;
import com.zhaoli.chickmusic.app.views.IrregularButton;
import com.zhaoli.chickmusic.data.Song;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoli on 2015/10/15.
 */
public class ClassifyButtonFragment extends Fragment implements IrregularButton.IOnButtonClickedListener,
        View.OnLongClickListener {

    private int layoutId = R.layout.classify_button_layout_1;
    private List<Song> songList;

    private List<IrregularButton> irregularButtonList = new ArrayList<>();
    private IOnUpdateUIListener onUpdateUIListener = null;

    public interface IOnUpdateUIListener {
        void onButtonLongTouch(Song song);
        void onButtonTouchUp();
        void onButtonClicked(Song song);
    }

    public ClassifyButtonFragment() {
    }

    public ClassifyButtonFragment(int layoutId, List<Song> songList) {
        this.layoutId = layoutId;
        this.songList = (songList == null) ? new ArrayList<Song>() : songList;
    }

    public void setOnUpdateUIListener(IOnUpdateUIListener listener) {
        onUpdateUIListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            songList = (List<Song>)savedInstanceState.getSerializable("SongList");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId, null);
        initButtonList(view);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("SongList", (Serializable) songList);
    }

    private void initButtonList(View view) {
        irregularButtonList.add((IrregularButton) view.findViewById(R.id.classifyButton01));
        irregularButtonList.add((IrregularButton) view.findViewById(R.id.classifyButton02));
        irregularButtonList.add((IrregularButton) view.findViewById(R.id.classifyButton03));
        irregularButtonList.add((IrregularButton) view.findViewById(R.id.classifyButton04));
        irregularButtonList.add((IrregularButton) view.findViewById(R.id.classifyButton05));

        for (int i = 0; i < irregularButtonList.size(); i++) {
            irregularButtonList.get(i).setTag(songList.get(i));
            irregularButtonList.get(i).setButtonText(songList.get(i).getSongName());
            irregularButtonList.get(i).setOnButtonClickedListener(this);
            irregularButtonList.get(i).setOnLongClickListener(this);
        }
    }

    @Override
    public void onClicked(View v) {
        if (onUpdateUIListener != null) {
            onUpdateUIListener.onButtonClicked((Song) v.getTag());
        }
    }

    @Override
    public void onTouchUp(View v) {
        if (onUpdateUIListener != null) {
            onUpdateUIListener.onButtonTouchUp();
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (onUpdateUIListener != null) {
            onUpdateUIListener.onButtonLongTouch((Song) view.getTag());
        }
        return false;
    }
}
