package com.zhaoli.chickmusic.app.eventbus.event;

/**
 * Created by zhaoli on 2015/10/22.
 *
 * 播放进度
 */
public class MusicProgressEvent {
    private int progress;

    public MusicProgressEvent(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }
}
