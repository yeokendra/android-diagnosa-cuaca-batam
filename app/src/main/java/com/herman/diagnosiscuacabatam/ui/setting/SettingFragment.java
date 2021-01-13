package com.herman.diagnosiscuacabatam.ui.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.herman.diagnosiscuacabatam.R;
import com.herman.diagnosiscuacabatam.model.CityCode;
import com.herman.diagnosiscuacabatam.service.WeatherService;
import com.herman.diagnosiscuacabatam.util.Util;

import java.util.ArrayList;

public class SettingFragment extends Fragment {

    SwitchCompat swNotif;
    AppCompatSpinner spDefaultCity;

    SharedPreferences settings;
    int currentCity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        swNotif = root.findViewById(R.id.sw_notif);
        spDefaultCity = root.findViewById(R.id.sp_default_city);

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

        ArrayList<CityCode> cityCodes = Util.populateCityCode();

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item,cityCodes);

        spDefaultCity.setAdapter(spinnerArrayAdapter);

        spDefaultCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CityCode cityCode = (CityCode) parent.getSelectedItem();
                currentCity = cityCode.getCode();

                setSpDefaultCity(currentCity);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        currentCity = settings.getInt(Util.SP_CITYCODES, -1);
        if(currentCity != 0 && currentCity != -1){
            for(int i=0; i<spDefaultCity.getAdapter().getCount();i++){
                if(currentCity == ((CityCode)spDefaultCity.getItemAtPosition(i)).getCode()){
                    spDefaultCity.setSelection(i);
                }
            }
        }
    }

    private void setSpDefaultCity(int code){
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(Util.SP_CITYCODES, code);
        editor.apply();
    }

    private void setSpNotif(boolean isNotifOn){
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(Util.SP_UTIL, isNotifOn);
        editor.apply();
    }
}

