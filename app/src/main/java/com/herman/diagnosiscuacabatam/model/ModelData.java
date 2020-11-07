package com.herman.diagnosiscuacabatam.model;

import com.herman.diagnosiscuacabatam.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ModelData implements Serializable { ;
    private ArrayList<WeatherByDatetime> weatherDataList;

    public ModelData(){
        weatherDataList = new ArrayList<>();
    }

    public ArrayList<WeatherByDatetime> getWeatherDataList() {
        return weatherDataList;
    }

    public void setWeatherDataList(ArrayList<WeatherByDatetime> weatherDataList) {
        this.weatherDataList = weatherDataList;
    }

    public int findWeatherDataPosByDate (String date){
        for(int i = 0; i < weatherDataList.size() ; i++){
            if(weatherDataList.get(i).getDatetime().equals(date)){
                return i;
            }
        }

        WeatherByDatetime data = new WeatherByDatetime();
        data.setDatetime(date);
        data.setDate(Util.stringToDate(date));

        weatherDataList.add(data);
        return weatherDataList.size()-1;
    }

}