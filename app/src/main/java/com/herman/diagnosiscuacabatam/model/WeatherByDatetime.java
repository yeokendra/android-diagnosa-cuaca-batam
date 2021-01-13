package com.herman.diagnosiscuacabatam.model;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

public class WeatherByDatetime implements Serializable {

    private LocalDateTime date;
    private String datetime;
    private int humidity;
    private int temperature;
    private int weather;
    private String windDirection;
    private double windDegree;
    private double windSpeed;
    private int humax, humin;
    private int tmax, tmin;

    public WeatherByDatetime(){
        date = null;
        datetime = "";
        humidity = -1;
        temperature = -1;
        weather = -1;
        windDirection = "";
        windDegree = -1;
        windSpeed = -1;
        tmax = -1;
        tmin = -1;
        humax = -1;
        humin = -1;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public double getWindDegree() {
        return windDegree;
    }

    public void setWindDegree(double windDegree) {
        this.windDegree = windDegree;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getHumax() {
        return humax;
    }

    public void setHumax(int humax) {
        this.humax = humax;
    }

    public int getHumin() {
        return humin;
    }

    public void setHumin(int humin) {
        this.humin = humin;
    }

    public int getTmax() {
        return tmax;
    }

    public void setTmax(int tmax) {
        this.tmax = tmax;
    }

    public int getTmin() {
        return tmin;
    }

    public void setTmin(int tmin) {
        this.tmin = tmin;
    }
}
