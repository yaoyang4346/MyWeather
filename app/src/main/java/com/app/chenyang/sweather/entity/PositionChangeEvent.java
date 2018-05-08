package com.app.chenyang.sweather.entity;

/**
 * Created by chenyang on 2017/5/6.
 */

public class PositionChangeEvent {
    private boolean change;

    public PositionChangeEvent(boolean change){
        this.change = change;
    }

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }
}
