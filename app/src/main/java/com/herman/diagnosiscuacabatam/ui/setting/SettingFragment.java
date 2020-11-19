package com.herman.diagnosiscuacabatam.ui.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.herman.diagnosiscuacabatam.R;
import com.herman.diagnosiscuacabatam.service.WeatherService;
import com.herman.diagnosiscuacabatam.util.Util;

public class SettingFragment extends Fragment {

    SwitchCompat swNotif;
    SharedPreferences settings;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        swNotif = root.findViewById(R.id.sw_notif);

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isNotifOn = settings.getBoolean(Util.SP_UTIL, false);

        if(isNotifOn){
            swNotif.setChecked(true);
        }else{
            swNotif.setChecked(false);
        }

        swNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setSpNotif(b);
                if(b){
                    Log.e("err","on");
//                    if(!WeatherService.isInstanceCreated()){
//                        Log.e("err","service isnt created yet");
//                        try {
//                            Log.e("err","service creating");
//                            getActivity().startService(new Intent(getActivity(), WeatherService.class));
//                        }catch (NullPointerException e){
//                            e.printStackTrace();
//                        }
//                    }
                    getActivity().startService(new Intent(getActivity(), WeatherService.class));
                }else{
                    Log.e("err","off");
                    if(WeatherService.isInstanceCreated()){
                        Log.e("err","service already created");
                        try {
                            Log.e("err","service stopping");
                            getActivity().stopService(new Intent(getActivity(), WeatherService.class));
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        return root;
    }

    public void setSpNotif(boolean isNotifOn){
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(Util.SP_UTIL, isNotifOn);
        editor.apply();
    }
}

