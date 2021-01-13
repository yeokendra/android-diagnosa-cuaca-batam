package com.herman.diagnosiscuacabatam.model;

public class CityCode {
    private int code;
    private String city;

    public CityCode(){
        code = -1;
        city = "";
    }

    public CityCode(int code, String city){
        this.code = code;
        this.city = city;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return city;
    }
}
