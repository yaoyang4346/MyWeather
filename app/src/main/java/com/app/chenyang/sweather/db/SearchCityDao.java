package com.app.chenyang.sweather.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.app.chenyang.sweather.utils.BaseUtils;
import com.app.chenyang.sweather.utils.LogUtils;

import java.io.File;

/**
 * Created by chenyang on 2017/2/14.
 */

public class SearchCityDao {
    public static final String DB_NAME = "weathercity.db";
    public static final String TABLE_NAME = "CITY_LIST";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PROVINCE = "province";
    public static final String COLUMN_AREA_ID = "areaid";
    private File DBFile;
    private SQLiteDatabase db;

    public SearchCityDao(){
        DBFile = new File(BaseUtils.getContext().getFilesDir(), DB_NAME);
        db = SQLiteDatabase.openDatabase(DBFile.getAbsolutePath(),null,SQLiteDatabase.OPEN_READONLY);
    }
    public Cursor searchCity(String s){
        if(TextUtils.isEmpty(s)){
            return null;
        }

        Cursor cursor = db.rawQuery("select "+COLUMN_NAME+","+COLUMN_PROVINCE+","+COLUMN_AREA_ID+" from "+ TABLE_NAME +" where "+COLUMN_NAME+" like ?;"
                                    ,new String[]{"%"+s+"%"});
        LogUtils.d("select "+COLUMN_NAME+","+COLUMN_PROVINCE+","+COLUMN_AREA_ID+" from "+ TABLE_NAME +" where "+COLUMN_NAME+" like "+"%"+s+"%");

        return cursor;
    }
    public String getIDByName(String s){
        if(TextUtils.isEmpty(s)){
            return null;
        }
        String id = null;
        Cursor cursor = db.query(TABLE_NAME,new String[]{COLUMN_AREA_ID},COLUMN_NAME + "=?",new String[]{s},null,null,null);
        if(cursor.moveToNext()){
            id = cursor.getString(0);
        }
        cursor.close();
        return id;
    }

    public String getProvinceByID(String ID){
        if(TextUtils.isEmpty(ID)){
            return null;
        }
        String province = null;
        Cursor cursor = db.query(TABLE_NAME,new String[]{COLUMN_PROVINCE},COLUMN_AREA_ID + "=?",new String[]{ID},null,null,null);
        if(cursor.moveToNext()){
            province = cursor.getString(0);
        }
        cursor.close();
        return province;
    }

    public void closeDB(){
        if(db!=null){
            db.close();
        }
    }
}
