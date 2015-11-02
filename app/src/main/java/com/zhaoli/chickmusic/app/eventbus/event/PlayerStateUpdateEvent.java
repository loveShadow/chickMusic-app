package com.zhaoli.chickmusic.app.eventbus.event;

import com.zhaoli.chickmusic.data.PlayerState;

/**
 * Created by zhaoli on 2015/10/23.
 */
public class PlayerStateUpdateEvent {

    private PlayerState currentState;

    public PlayerStateUpdateEvent(PlayerState currentState) {
        this.currentState = currentState;
    }

    public PlayerState getCurrentState() {
        return currentState;
    }
}
