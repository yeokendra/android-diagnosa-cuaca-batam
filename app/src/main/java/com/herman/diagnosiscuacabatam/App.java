package com.herman.diagnosiscuacabatam;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.herman.diagnosiscuacabatam.service.WeatherService;
import com.herman.diagnosiscuacabatam.util.Util;

import net.danlew.android.joda.JodaTimeAndroid;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);

        if(!WeatherService.isInstanceCreated()){
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isNotifOn = settings.getBoolean(Util.SP_UTIL, false);
            if(isNotifOn) {
                startService(new Intent(this, WeatherService.class));
            }
        }

    }
}
