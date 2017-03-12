package com.microsoft.mimickeralarm.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.renderscript.ScriptIntrinsicConvolve3x3;

/**
 * Created by mouse on 2017/3/10 0010.
 */
public class SharePreferencesUtils {
    public  static SharedPreferences preferences;
    public  static String fileName="daily";
    public static void putBoolean(Context c,String key,Boolean value){
        if (preferences==null){
            preferences=c.getSharedPreferences(fileName,c.MODE_WORLD_READABLE);

        }
        SharedPreferences.Editor editor=preferences.edit().putBoolean(key, value);
        editor.commit();
    }

    public static Boolean getBoolean(Context c,String key,Boolean defaultValue){
        if (preferences==null){
            preferences=c.getSharedPreferences(fileName,c.MODE_WORLD_READABLE);
        }
        return preferences.getBoolean(key, defaultValue);

    }

    public static void putLong(Context c,String key,Long value){
        if (preferences==null){
            preferences=c.getSharedPreferences(fileName,c.MODE_WORLD_READABLE);
        }
        SharedPreferences.Editor editor=preferences.edit().putLong(key,value);
        editor.commit();
    }

    public  static  Long getLong(Context c,String key,Long value){
        if (preferences==null){
            preferences=c.getSharedPreferences(fileName,c.MODE_WORLD_READABLE);
        }
        return preferences.getLong(key,value);

    }





}
