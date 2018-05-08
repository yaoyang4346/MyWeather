package com.app.chenyang.sweather.entity;

/**
 * Created by chenyang on 2017/2/14.
 */

public class SearchCityInfo {
    private String name;
    private String province;
    private String ID;

    public String getName() {
        return name;
    }

    public SearchCityInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getProvince() {
        return province;
    }

    public SearchCityInfo setProvince(String province) {
        this.province = province;
        return this;
    }

    public String getID(){
        return ID;
    }

    public SearchCityInfo setID(String ID){
        this.ID = ID;
        return this;
    }
}
