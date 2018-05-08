package com.app.chenyang.sweather.entity;

/**
 * Created by chenyang on 2017/5/5.
 */

public class RefreshEvent {
    private boolean refresh;

    public RefreshEvent(boolean refresh){
        this.refresh = refresh;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }
}
