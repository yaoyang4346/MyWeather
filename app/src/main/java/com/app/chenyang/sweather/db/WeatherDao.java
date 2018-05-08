package com.app.chenyang.sweather.db;

import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.entity.HeWeather;
import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.utils.BaseUtils;

import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenyang on 2017/3/11.
 */

public class WeatherDao {
    private static final int DAY_NUM = 6;
    public static final String COLUMN_POSITION = "position";
    public static final String COLUMN_AREA_ID = "areaId";
    public static final String COLUMN_GPS = "gps";

    public static final String PRE_ICON = "s";
    public static final int NON_EXISTENT = -1;
    public static final int EXIST = 1;

    public static WeatherDataInfo getDataByPosition(int position){
        List<WeatherDataInfo> list = DataSupport.where(COLUMN_POSITION + " = ?",position+"").find(WeatherDataInfo.class);
        if(list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    public static int isExist(String id){
        List<WeatherDataInfo> list = DataSupport.where(COLUMN_AREA_ID + " = ?",id).find(WeatherDataInfo.class);
        if(list.size() > 0){
            if (list.size() == 1 && list.get(0).isGps()){
                return NON_EXISTENT;
            }
            return EXIST;
        }
        return NON_EXISTENT;
    }

    public static WeatherDataInfo getGPSData(){
        List<WeatherDataInfo> list = DataSupport.where(COLUMN_GPS + " = ?","1").find(WeatherDataInfo.class);
        if (list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    public static boolean saveWeatherData(HeWeather.HeWeather5Bean heWeather5Bean,int pos,boolean isGPS){
        WeatherDataInfo data = new WeatherDataInfo();
        data.setAreaId(heWeather5Bean.getBasic().getId());

        data.setName(heWeather5Bean.getBasic().getCity());

        data.setGps(isGPS);

        if (pos == -1){
            data.setPosition(DataSupport.count(WeatherDataInfo.class));
        }else{
            data.setPosition(pos);
        }

        data.setSaveTime(System.currentTimeMillis());

        data.setServeTime(formatTime(heWeather5Bean.getBasic().getUpdate().getLoc()));

        SearchCityDao searchCityDao = new SearchCityDao();
        data.setProvince(searchCityDao.getProvinceByID(heWeather5Bean.getBasic().getId()));
        searchCityDao.closeDB();

        data.setTmp(heWeather5Bean.getNow().getTmp()+ BaseUtils.getString(R.string.temperature));

        if(heWeather5Bean.getAqi() == null){
            data.setAqi(BaseUtils.getString(R.string.aqi)+ " " + BaseUtils.getString(R.string.no_data));
        }else{
            String aqi =BaseUtils.getString(R.string.aqi)+ " " + heWeather5Bean.getAqi().getCity().getAqi()+" ("+heWeather5Bean.getAqi().getCity().getQlty()+")";
            data.setAqi(aqi);
        }

        data.setIcon(BaseUtils.getImgResourceByName(PRE_ICON+heWeather5Bean.getNow().getCond().getCode()));

        data.setWeather(heWeather5Bean.getNow().getCond().getTxt());

        try {
            data.setLat(Double.parseDouble(heWeather5Bean.getBasic().getLat()));
            data.setLon(Double.parseDouble(heWeather5Bean.getBasic().getLon()));
        }catch (NumberFormatException e){
            data.setLat(-1);
            data.setLon(-1);
        }


        ArrayList<Integer> max = new ArrayList<>();
        for (int i = 0 ; i < DAY_NUM ; i++){
            if(i >= heWeather5Bean.getDaily_forecast().size()){
                max.add(max.get(i-1));
            }else{
                max.add(Integer.parseInt(heWeather5Bean.getDaily_forecast().get(i).getTmp().getMax()));
            }
        }
        data.setMaxTmp(max);

        ArrayList<Integer> min = new ArrayList<>();
        for (int i = 0 ; i < DAY_NUM ; i++){
            if(i >= heWeather5Bean.getDaily_forecast().size()){
                min.add(min.get(i-1));
            }else{
                min.add(Integer.parseInt(heWeather5Bean.getDaily_forecast().get(i).getTmp().getMin()));
            }

        }
        data.setMinTmp(min);

        ArrayList<Integer> dailyIcon = new ArrayList<>();
        for (int i = 0 ; i < DAY_NUM ; i++){
            if(i >= heWeather5Bean.getDaily_forecast().size()){
                dailyIcon.add(dailyIcon.get(i-1));
            }else{
                dailyIcon.add(BaseUtils.getImgResourceByName(PRE_ICON+heWeather5Bean.getDaily_forecast().get(i).getCond().getCode_d()));
            }
        }
        data.setDailyIcon(dailyIcon);

        ArrayList<String> livingIndex = new ArrayList<>();
        livingIndex.add(heWeather5Bean.getSuggestion().getUv().getBrf());
        livingIndex.add(heWeather5Bean.getDaily_forecast().get(0).getAstro().getSs());
        livingIndex.add(heWeather5Bean.getDaily_forecast().get(0).getWind().getSpd()+BaseUtils.getString(R.string.wind_speed));
        livingIndex.add(heWeather5Bean.getSuggestion().getDrsg().getBrf());
        livingIndex.add(heWeather5Bean.getSuggestion().getSport().getBrf());
        livingIndex.add(heWeather5Bean.getSuggestion().getCw().getBrf());
        livingIndex.add(heWeather5Bean.getSuggestion().getComf().getBrf());
        livingIndex.add(heWeather5Bean.getSuggestion().getFlu().getBrf());
        data.setLivingIndex(livingIndex);

        if (isGPS){
            return data.saveOrUpdate(COLUMN_GPS + " = 1");
        }
        return data.saveOrUpdate(COLUMN_AREA_ID + " = ? and " + COLUMN_GPS + " = 0" , heWeather5Bean.getBasic().getId());
    }

    private static String formatTime(String time){
        String regex = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";
        if(time.length() == 16){
            time = time + ":00";
        }else if(time.length() != 19){
            return time;
        }
        if(time.matches(regex)){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = dateFormat.parse(time);
            } catch (ParseException e) {
                return time;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("M"+BaseUtils.getString(R.string.month)+"d"+BaseUtils.getString(R.string.day)+",EEEE HH:mm");
            if(date == null){
                return time;
            }
            return sdf.format(date);
        }
        return time;
    }
}
