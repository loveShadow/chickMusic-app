package com.zhaoli.chickmusic.app.eventbus.event;

/**
 * Created by zhaoli on 2015/10/22.
 *
 * 缓存进度
 */
public class BufferingUpdateEvent {
    private int percent;

    public BufferingUpdateEvent(int percent) {
        this.percent = percent;
    }

    public int getPercent() {
        return percent;
    }
}
