package com.herman.diagnosiscuacabatam.guide;

import android.content.Context;

import com.herman.diagnosiscuacabatam.R;

public class WeatherGuide {
    public static final int CLEAR_SKIES = 0;
    public static final int CLEAR_SKIES2 = 100;
    public static final int PARTLY_CLOUDY = 1;
    public static final int PARTLY_CLOUDY2 = 101;
    public static final int PARTLY_CLOUDY3 = 2;
    public static final int PARTLY_CLOUDY4 = 102;
    public static final int MOSTLY_CLOUDY = 3;
    public static final int MOSTLY_CLOUDY2 = 103;
    public static final int OVERCAST = 4;
    public static final int OVERCAST2 = 104;
    public static final int HAZE = 5;
    public static final int SMOKE = 10;
    public static final int FOG = 45;
    public static final int LIGHT_RAIN = 60;
    public static final int RAIN = 61;
    public static final int HEAVY_RAIN = 63;
    public static final int ISOLATED_SHOWER = 80;
    public static final int SEVERE_THUNDERSTORM = 95;
    public static final int SEVERE_THUNDERSTORM2 = 97;

    public static final String NORTH = "N";
    public static final String NORTH_NORTHEAST = "NNE";
    public static final String NORTH_EAST = "NE";
    public static final String EAST_SOUTHEAST = "ESE";
    public static final String EAST = "E";
    public static final String SOUTH_EAST = "SE";
    public static final String SOUTH_SOUTHEAST = "SSE";
    public static final String SOUTH = "S";
    public static final String SOUTH_SOUTHWEST = "SSW";
    public static final String SOUTHWEST = "SW";
    public static final String WEST_SOUTHWEST = "WSW";
    public static final String WEST = "W";
    public static final String WEST_NORTHWEST = "WNW";
    public static final String NORTHWEST = "NW";
    public static final String NORTH_NORTHWEST = "NNW";
    public static final String VARIABLE = "VARIABLE";


    public String getWeatherByCode(Context context, int code){

        switch (code){
            case CLEAR_SKIES:
            case CLEAR_SKIES2:
                return context.getString(R.string.weather_code_0);
            case PARTLY_CLOUDY:
            case PARTLY_CLOUDY2:
            case PARTLY_CLOUDY3:
            case PARTLY_CLOUDY4:
                return context.getString(R.string.weather_code_1);
            case MOSTLY_CLOUDY:
            case MOSTLY_CLOUDY2:
                return context.getString(R.string.weather_code_3);
            case OVERCAST:
            case OVERCAST2:
                return context.getString(R.string.weather_code_4);
            case HAZE:
                return context.getString(R.string.weather_code_5);
            case SMOKE:
                return context.getString(R.string.weather_code_3);
            case FOG:
                return context.getString(R.string.weather_code_45);
            case LIGHT_RAIN:
                return context.getString(R.string.weather_code_60);
            case RAIN:
                return context.getString(R.string.weather_code_61);
            case HEAVY_RAIN:
                return context.getString(R.string.weather_code_63);
            case ISOLATED_SHOWER:
                return context.getString(R.string.weather_code_80);
            case SEVERE_THUNDERSTORM:
            case SEVERE_THUNDERSTORM2:
                return context.getString(R.string.weather_code_95);
        }
        return context.getString(R.string.weather_code_0);
    }

}
