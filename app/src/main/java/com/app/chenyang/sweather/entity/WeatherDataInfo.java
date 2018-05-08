package com.app.chenyang.sweather.entity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyang on 2017/3/10.
 */

public class WeatherDataInfo extends DataSupport{
    private String areaId;
    private String name;
    private int position;
    private long saveTime;
    private String province;
    private String serveTime;
    private double lat;
    private double lon;
    private boolean gps;

    private String tmp;
    private String aqi;
    private int icon;
    private String weather;

    private ArrayList<Integer> maxTmp = new ArrayList<>();
    private ArrayList<Integer> minTmp = new ArrayList<>();
    private ArrayList<Integer> dailyIcon = new ArrayList<>();
    private ArrayList<String> livingIndex = new ArrayList<>();

    public boolean isGps() {
        return gps;
    }

    public void setGps(boolean gps) {
        this.gps = gps;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getServeTime() {
        return serveTime;
    }

    public void setServeTime(String serveTime) {
        this.serveTime = serveTime;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public ArrayList<Integer> getMaxTmp() {
        return maxTmp;
    }

    public void setMaxTmp(ArrayList<Integer> maxTmp) {
        this.maxTmp = maxTmp;
    }

    public ArrayList<Integer> getMinTmp() {
        return minTmp;
    }

    public void setMinTmp(ArrayList<Integer> minTmp) {
        this.minTmp = minTmp;
    }

    public ArrayList<Integer> getDailyIcon() {
        return dailyIcon;
    }

    public void setDailyIcon(ArrayList<Integer> dailyIcon) {
        this.dailyIcon = dailyIcon;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getLivingIndex() {
        return livingIndex;
    }

    public void setLivingIndex(ArrayList<String> livingIndex) {
        this.livingIndex = livingIndex;
    }
}
