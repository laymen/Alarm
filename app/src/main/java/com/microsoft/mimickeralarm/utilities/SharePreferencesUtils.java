package com.microsoft.mimickeralarm.utilities;

import android.content.Context;
import android.content.SharedPreferences;

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
        SharedPreferences.Editor editor=preferences.edit().putLong(key, value);
        editor.commit();
    }

    public  static  Long getLong(Context c,String key,Long value){
        if (preferences==null){
            preferences=c.getSharedPreferences(fileName,c.MODE_WORLD_READABLE);
        }
        return preferences.getLong(key, value);

    }

    public static void putInt(Context c,String key,int value){
        if (preferences==null){
            preferences=c.getSharedPreferences(fileName,c.MODE_WORLD_READABLE);
        }
        SharedPreferences.Editor editor=preferences.edit().putInt(key, value);
        editor.commit();
    }

    public static  int getInt(Context c,String key,int value){
        if (preferences==null){
            preferences=c.getSharedPreferences(fileName,c.MODE_WORLD_READABLE);
        }
        return preferences.getInt(key, value);
    }

    public static void putString(Context c,String key,String value){
        if (preferences==null){
            preferences=c.getSharedPreferences(fileName,c.MODE_WORLD_READABLE);
        }
        SharedPreferences.Editor editor=preferences.edit().putString(key, value);
        editor.commit();
    }

    public static  String getString(Context c,String key,String value){
        if (preferences==null){
            preferences=c.getSharedPreferences(fileName,c.MODE_WORLD_READABLE);
        }
        return preferences.getString(key, value);
    }


}
