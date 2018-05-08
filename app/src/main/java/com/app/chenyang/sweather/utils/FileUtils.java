package com.app.chenyang.sweather.utils;

import android.content.res.AssetManager;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by chenyang on 2017/2/10.
 */

public class FileUtils {
    public  static final int COPYBUFFER = 1024;
    public static void closeIO(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // Ignored
            }
        }
    }

    public static boolean copyDB(String s,File dbFile){
        AssetManager am = BaseUtils.getContext().getAssets();
        InputStream in = null;
        OutputStream out = null;
        try{
            in = am.open(s);
            out = new FileOutputStream(dbFile);
            byte[] buffer = new byte[COPYBUFFER];
            int len = -1;
            while ((len = in.read(buffer)) != -1){
                out.write(buffer,0,len);
            }

        }catch (IOException ioe){
            return false;
        }finally {
            closeIO(in);
            closeIO(out);
        }
        return true;
    }
}
