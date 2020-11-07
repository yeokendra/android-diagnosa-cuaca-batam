package com.herman.diagnosiscuacabatam.util;

import android.content.Context;
import android.util.Log;

import com.herman.diagnosiscuacabatam.R;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Util {

    public static final int RAIN_LOW = 10;
    public static final int RAIN_HIGH = 11;

    public static LocalDateTime stringToDate(String dateString){
        //format yyyyMMddHHmm;
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
//        try {
//            LocalDateTime date = format.parse(dateString);
//            return date;
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return null;
//        }

        String pattern = "yyyyMMddHHmm";
        LocalDateTime dateTime  = LocalDateTime.parse(dateString, DateTimeFormat.forPattern(pattern));
        return dateTime;
    }
    public static String dateToString(LocalDateTime date, String format){
//        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
//        try {
//            String dateTime = dateFormat.format(date);
//            return dateTime;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
        String formattedDate = date.toString(format);
        return formattedDate;

    }

    public static Integer daysDifference(LocalDateTime today, LocalDateTime date){
        LocalDateTime tempToday = today.hourOfDay().setCopy(0)
                .minuteOfHour().setCopy(0)
                .secondOfMinute().setCopy(0)
                .millisOfDay().setCopy(0);
        LocalDateTime tempDate = date.hourOfDay().setCopy(0)
                .minuteOfHour().setCopy(0)
                .secondOfMinute().setCopy(0)
                .millisOfDay().setCopy(0);
        int days = Days.daysBetween(tempToday, tempDate).getDays();
        Log.e("err","today = "+tempToday.toString()+", data = "+tempDate.toString() + " difference = " + days);
        return days;
    }

    public static String getWeatherTextByCode(Context context, int code){
        switch (code){
            case 0:
            case 100:
                return context.getString(R.string.weather_code_0);
            case 1:
            case 101:
            case 2:
            case 102:
                return context.getString(R.string.weather_code_1);
            case 3:
            case 103:
                return context.getString(R.string.weather_code_3);
            case 4:
            case 104:
                return context.getString(R.string.weather_code_4);
            case 5:
                return context.getString(R.string.weather_code_5);
            case 10:
                return context.getString(R.string.weather_code_10);
            case 45:
                return context.getString(R.string.weather_code_45);
            case 60:
                return context.getString(R.string.weather_code_60);
            case 61:
                return context.getString(R.string.weather_code_61);
            case 63:
                return context.getString(R.string.weather_code_63);
            case 80:
                return context.getString(R.string.weather_code_80);
            case 95:
            case 97:
                return context.getString(R.string.weather_code_95);
            default:
                return "Tidak terdaftar";

        }
    }

    public static int getWeatherIconByCode(Context context, int code){
        switch (code){
            case 0:
            case 100:
                return R.drawable.ic_100;
            case 1:
            case 101:
            case 2:
            case 102:
                return R.drawable.ic_101;
            case 3:
            case 103:
                return R.drawable.ic_103;
            case 4:
            case 104:
                return R.drawable.ic_104;
            case 5:
                return R.drawable.ic_5;
            case 10:
                return R.drawable.ic_10;
            case 45:
                return R.drawable.ic_10;
            case 60:
                return R.drawable.ic_60;
            case 61:
                return R.drawable.ic_61;
            case 63:
                return R.drawable.ic_63;
            case 80:
                return R.drawable.ic_61;
            case 95:
            case 97:
                return R.drawable.ic_95;
            default:
                return R.drawable.ic_100;
        }
    }

    public static String getWindDirectionByCode(Context context, String code){
        switch (code){
            case "N":
                return context.getString(R.string.wd_n);
            case "NNE":
                return context.getString(R.string.wd_nne);
            case "NE":
                return context.getString(R.string.wd_ne);
            case "ENE":
                return context.getString(R.string.wd_ene);
            case "E":
                return context.getString(R.string.wd_e);
            case "ESE":
                return context.getString(R.string.wd_ese);
            case "SE":
                return context.getString(R.string.wd_se);
            case "SSE":
                return context.getString(R.string.wd_sse);
            case "S":
                return context.getString(R.string.wd_s);
            case "SSW":
                return context.getString(R.string.wd_ssw);
            case "SW":
                return context.getString(R.string.wd_sw);
            case "WSW":
                return context.getString(R.string.wd_wsw);
            case "W":
                return context.getString(R.string.wd_w);
            case "WNW":
                return context.getString(R.string.wd_wnw);
            case "NW":
                return context.getString(R.string.wd_nw);
            case "NNW":
                return context.getString(R.string.wd_nnw);
            case "VARIABLE":
                return context.getString(R.string.wd_var);
            default:
                return context.getString(R.string.wd_var);
        }
    }

    public static String getTemperature(Context context, int celcius){
        if(celcius < 16){
            return context.getString(R.string.temperature_cold);
        }else if(celcius >= 16 && celcius <=25){
            return context.getString(R.string.temperature_average);
        }else {
            return context.getString(R.string.temperature_hot);
        }
    }

    public static String getHumid(Context context, int humid){
        if(humid < 45){
            return context.getString(R.string.humid_low);
        }else if(humid >= 45 && humid <=65){
            return context.getString(R.string.humid_mid);
        }else {
            return context.getString(R.string.humid_high);
        }
    }

    public static int getRainChance(int weather){
        if((weather >= 0 && weather < 50) || weather >= 100){
            return RAIN_LOW;
        }else{
            return RAIN_HIGH;
        }
    }
}


