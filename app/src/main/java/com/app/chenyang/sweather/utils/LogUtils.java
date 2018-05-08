package com.app.chenyang.sweather.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenyang on 2017/2/10.
 */

public class LogUtils {
    public static final String TAG = "SWeather";
    public static boolean showLog = true;
    public static boolean writeLogToFile = true;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static void e(String msg){
        if(showLog){
            Log.e(TAG,msg);
        }
        if(writeLogToFile){
            writeToFile(msg,"ERROR");
        }
    }

    public static void d(String msg){
        if(showLog){
            Log.d(TAG,msg);
        }
        if(writeLogToFile){
            writeToFile(msg,"DEBUG");
        }
    }

    private static void writeToFile(String msg , String type){
        File logFile = new File(BaseUtils.getContext().getFilesDir().getPath() + "log.txt");
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        String time = SDF.format(new Date());
        String log = time + " -> " + type + "\t" + msg + "\n";
        try {
            fos = BaseUtils.getContext().openFileOutput("log.txt", Context.MODE_APPEND);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(log);
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            FileUtils.closeIO(bw);
        }
    }

}
