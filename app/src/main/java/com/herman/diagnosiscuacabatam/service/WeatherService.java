package com.herman.diagnosiscuacabatam.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.herman.diagnosiscuacabatam.MainActivity;
import com.herman.diagnosiscuacabatam.R;
import com.herman.diagnosiscuacabatam.model.ModelData;
import com.herman.diagnosiscuacabatam.model.WeatherByDatetime;
import com.herman.diagnosiscuacabatam.util.Util;
import com.herman.diagnosiscuacabatam.util.WeatherDataGetter;

import java.util.ArrayList;

public class WeatherService extends Service implements WeatherDataGetter.WeatherDataPresenter {

    private static WeatherService weatherService = null;

    private ModelData modelData;
    private WeatherDataGetter weatherDataGetter;

    public static boolean isInstanceCreated() {
        return weatherService != null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        weatherService = this;
        Log.e("err","service created");

        weatherDataGetter = new WeatherDataGetter();
        weatherDataGetter.getWeather(this,this);


    }

    @Override
    public void onDestroy() {
        weatherService = null;
        Log.e("err","service destroyed");
    }

    @Override
    public void onDataReady(ModelData data, ArrayList<ArrayList<WeatherByDatetime>> weatherListByPage) {

        ArrayList<WeatherByDatetime> weatherList = weatherListByPage.get(0);
        String title = "";
        String content = "";
        int drawable = -1;

        for(int i=0;i<weatherList.size();i++){
            //202011191200

            if(weatherList.get(i).getDatetime().contains("1200")){
                title = Util.getWeatherTextByCode(this,weatherList.get(i).getWeather());
                content = weatherList.get(i).getTmin() + "° ~ " +weatherList.get(i).getTmax()+"°";
                drawable = Util.getWeatherIconByCode(this,weatherList.get(i).getWeather());
                break;
            }

        }

        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        String CHANNEL_ID="CUACA_BATAM";
        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name",NotificationManager.IMPORTANCE_LOW);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1,intent,0);
        Notification notification=new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentText(content)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(drawable)
                .build();

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1,notification);

    }
}
