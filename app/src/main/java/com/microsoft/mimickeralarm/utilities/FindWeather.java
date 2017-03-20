package com.microsoft.mimickeralarm.utilities;

/**
 * Created by Administrator on 2017/3/19 0019.
 */
public class FindWeather {
    public  static  String  getKeyWord(String weather){
        if (weather.contains("雪")){
            return "雪";
        }else if(weather.contains("雨")){
            return "雨";
        }else if(weather.contains("阴")||weather.contains("云")){
            return "阴";
        }else if(weather.contains("晴")){
           return "晴";
        }
        return "";

    }
}
