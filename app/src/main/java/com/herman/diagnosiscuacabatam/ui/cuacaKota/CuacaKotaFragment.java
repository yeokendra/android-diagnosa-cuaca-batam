package com.herman.diagnosiscuacabatam.ui.cuacaKota;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.herman.diagnosiscuacabatam.R;
import com.herman.diagnosiscuacabatam.base.BaseRecyclerAdapter;
import com.herman.diagnosiscuacabatam.base.BaseRecyclerViewHolder;
import com.herman.diagnosiscuacabatam.model.CityCode;
import com.herman.diagnosiscuacabatam.model.ModelData;
import com.herman.diagnosiscuacabatam.model.WeatherByDatetime;
import com.herman.diagnosiscuacabatam.util.Util;
import com.herman.diagnosiscuacabatam.util.WeatherDataGetter;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class CuacaKotaFragment extends Fragment implements View.OnClickListener, WeatherDataGetter.WeatherDataPresenter {

    private ModelData mData;
    private AppCompatSpinner spCity;
    private TextView tvDate;
    private RecyclerView mRecyclerview;
    private BaseRecyclerAdapter<WeatherByDatetime> mAdapter;
    private LinearLayoutManager layoutListManager;
    private LocalDateTime mTodayDate;
    private ImageView ivNext, ivPrev;

    private TextView tvSuhu, tvHumid, tvRain, tvWind;
    private FloatingActionButton fabAsk;

    private LinearLayout lldiagnosis;
    private RelativeLayout rlQ1, rlQ2, rlQ3;
    private Button btnQ1y, btnQ1n, btnQ2y, btnQ2n, btnQ3y, btnQ3n;
    private boolean isQ1yes = false, isQ2yes = false, isQ3yes = false;

    //max page 2
    private int maxPage = 1;
    private int currPage = 0;
    private ArrayList<ArrayList<WeatherByDatetime>> mWeatherListByPage = new ArrayList<>();

    //current selected city
    private int currentCity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cuaca_kota, container, false);
        //spCity = root.findViewById(R.id.sp_city);
        //tvDate = root.findViewById(R.id.tv_date);
        mRecyclerview = root.findViewById(R.id.rv_weather);
        //ivNext = root.findViewById(R.id.iv_next);
        //ivPrev = root.findViewById(R.id.iv_prev);

        tvSuhu = root.findViewById(R.id.tv_diag_temperature);
        tvHumid = root.findViewById(R.id.tv_diag_humidity);
        tvRain = root.findViewById(R.id.tv_diag_rain);
        tvWind = root.findViewById(R.id.tv_diag_wind);
        fabAsk = root.findViewById(R.id.fab_ask);

        lldiagnosis = root.findViewById(R.id.ll_diagnosis);

        rlQ1 = root.findViewById(R.id.rl_q1);
        rlQ2 = root.findViewById(R.id.rl_q2);
        rlQ3 = root.findViewById(R.id.rl_q3);

        btnQ1y = root.findViewById(R.id.btn_q1_yes);
        btnQ1n = root.findViewById(R.id.btn_q1_no);
        btnQ2y = root.findViewById(R.id.btn_q2_yes);
        btnQ2n = root.findViewById(R.id.btn_q2_no);
        btnQ3y = root.findViewById(R.id.btn_q3_yes);
        btnQ3n = root.findViewById(R.id.btn_q3_no);

        btnQ1y.setOnClickListener(this);
        btnQ1n.setOnClickListener(this);
        btnQ2y.setOnClickListener(this);
        btnQ2n.setOnClickListener(this);
        btnQ3y.setOnClickListener(this);
        btnQ3n.setOnClickListener(this);



        //ivNext.setOnClickListener(this);
        //ivPrev.setOnClickListener(this);

        layoutListManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currentCity = settings.getInt(Util.SP_CITYCODES, -1);

        ArrayList<CityCode> cityCodes = Util.populateCityCode();

//        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(),
//                android.R.layout.simple_spinner_item,cityCodes);
//
//        spCity.setAdapter(spinnerArrayAdapter);
//
//        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                CityCode cityCode = (CityCode) parent.getSelectedItem();
//                currentCity = cityCode.getCode();
//
//                reload();
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//
//        if(currentCity != 0 && currentCity != -1){
//            for(int i=0; i<spCity.getAdapter().getCount();i++){
//                if(currentCity == ((CityCode)spCity.getItemAtPosition(i)).getCode()){
//                    spCity.setSelection(i);
//                }
//            }
//        }

        fabAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Intent.ACTION_SENDTO)
                        .setType("plain/text")
                        .setData(Uri.parse("mailto:"))
                        .putExtra(Intent.EXTRA_EMAIL, new String[]{"hermanx1997@gmail.com"})
                        .putExtra(Intent.EXTRA_SUBJECT, "[DIAGNOSIS CUACA BATAM] Pertanyaan")
                        .putExtra(Intent.EXTRA_TEXT, "halo, saya memiliki pertanyaan ");
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void reload(){
        mTodayDate = LocalDateTime.now();
        currPage = 0;
        validateNextPrevVisibility();

        mData = new ModelData();
        WeatherDataGetter weatherDataGetter = new WeatherDataGetter();
        weatherDataGetter.getWeather(getActivity(),currentCity,this);
    }

    private void changePage(boolean isNext){
        if(isNext){
            if(currPage!=maxPage){
                currPage = currPage+1;
            }
        }else{
            if(currPage!=0){
                currPage = currPage-1;
            }
        }

        //Log.e("err",currPage+" ");
        doShowDataInView();
        validateNextPrevVisibility();

    }

    private void validateNextPrevVisibility(){
//        if(currPage == 0){
//            ivPrev.setVisibility(View.GONE);
//        }else{
//            ivPrev.setVisibility(View.VISIBLE);
//        }
//
//        if(currPage == maxPage){
//            ivNext.setVisibility(View.GONE);
//        }else{
//            ivNext.setVisibility(View.VISIBLE);
//        }
    }

    private void doShowDataInView(){
        String date = Util.dateToString(mWeatherListByPage.get(currPage).get(0).getDate(),"EEEE, dd MMMM");
        //tvDate.setText(date);

        setWeatherList(mWeatherListByPage.get(currPage));
        processWeatherDiagnosis(mWeatherListByPage.get(currPage));

    }

    private void processWeatherDiagnosis(ArrayList<WeatherByDatetime> list){

        int tMax = -1;
        int tMin = -1;
        double tAvg = -1;
        ArrayList<Integer> temperature = new ArrayList<>();

        int huMax = -1;
        int huMin = -1;
        double huAvg = -1;
        ArrayList<Integer> humidity = new ArrayList<>();

        double windLow = -1;
        double windHigh = -1;
        double windAvg = -1;
        ArrayList<Double> windSpeed = new ArrayList<>();
        double wdMax = -1;
        HashMap<Double,Integer> windDegreeCount = new HashMap<>();
        double windDegree = -1;

        for (int i = 0; i < list.size(); i++) {


            if (list.get(i).getTmax() >= 0) {
                tMax = list.get(i).getTmax();
            }
            if (list.get(i).getTmin() >= 0) {
                tMin = list.get(i).getTmin();
            }
            if(list.get(i).getTemperature() >= 0){
                temperature.add(list.get(i).getTemperature());
            }

            if(list.get(i).getHumax() >= 0){
                huMax = list.get(i).getHumax();
            }
            if(list.get(i).getHumin() >= 0){
                huMin = list.get(i).getHumin();
            }
            if(list.get(i).getHumidity() >= 0){
                humidity.add(list.get(i).getHumidity());
            }

            double tempWindSpeed = list.get(i).getWindSpeed();
            double tempWindDegree = list.get(i).getWindDegree();

            if(tempWindSpeed >= 0) {
                windSpeed.add(list.get(i).getWindSpeed());
                if (windHigh == -1 || windLow == -1) {
                    if (windHigh == -1) {
                        windHigh = tempWindSpeed;
                        wdMax = list.get(i).getWindDegree();
                    }
                    if (windLow == -1){
                        windLow = tempWindSpeed;
                    }
                }else{
                    if(tempWindSpeed > windHigh){
                        windHigh = tempWindSpeed;
                        wdMax = list.get(i).getWindDegree();
                    }else if(tempWindSpeed < windLow){
                        windLow = tempWindSpeed;
                    }
                }
            }

            if(windDegreeCount.get(tempWindDegree) == null){
                windDegreeCount.put(tempWindDegree, 1);
            }else{
                int currCount = windDegreeCount.get(tempWindDegree);
                currCount = currCount+1;
                windDegreeCount.put(tempWindDegree,currCount);
            }
        }

        tAvg = calculateAverage(temperature);
        huAvg = calculateAverage(humidity);
        windAvg = calculateAverageDouble(windSpeed);


        int currHighestValue = 0;
        for (HashMap.Entry<Double, Integer> entry : windDegreeCount.entrySet()) {
            Double key = entry.getKey();
            Integer value = entry.getValue();

            if(value > currHighestValue){
                windDegree = key;
                currHighestValue = value;
            }
        }

        if(isAdded()) {
            StringBuilder sbTemperature = new StringBuilder();
            sbTemperature.append(getString(R.string.temperature_min));
            sbTemperature.append(" " + tMin + " °C.\n");
            sbTemperature.append(getString(R.string.temperature_max));
            sbTemperature.append(" " + tMax + " °C.\n");
            sbTemperature.append(getString(R.string.temperature_avg));
            sbTemperature.append(" " + tAvg + " °C.");

            StringBuilder sbHumidity = new StringBuilder();
            sbHumidity.append(getString(R.string.humid_min));
            sbHumidity.append(" " + huMin + " %.\n");
            sbHumidity.append(getString(R.string.humid_max));
            sbHumidity.append(" " + huMax + " %.\n");
            sbHumidity.append(getString(R.string.humid_avg));
            sbHumidity.append(" " + huAvg + " %.");

            StringBuilder sbWind = new StringBuilder();
            sbWind.append(getString(R.string.wind_max));
            sbWind.append(" " + windHigh + " km/jam.\n");
            sbWind.append(getString(R.string.wind_min));
            sbWind.append(" " + windLow + " km/jam.\n");
            String windAvgFormatted = new DecimalFormat("##.##").format(windAvg);
            sbWind.append(getString(R.string.wind_avg));
            sbWind.append(" " + windAvgFormatted + " km/jam.\n");
            sbWind.append(getString(R.string.wind_wd_max));
            sbWind.append(" " + wdMax + " °.\n");
            sbWind.append(getString(R.string.wind_most_wd));
            sbWind.append(" " + windDegree + " °.");

            tvSuhu.setText(sbTemperature);
            tvHumid.setText(sbHumidity);
            tvWind.setText(sbWind);
        }
    }

    private double calculateAverage(List <Integer> marks) {
        Integer sum = 0;
        if(!marks.isEmpty()) {
            for (Integer mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    private double calculateAverageDouble(List <Double> marks) {
        Double sum = 0.0;
        if(!marks.isEmpty()) {
            for (Double mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    private void setWeatherList(ArrayList<WeatherByDatetime> list){
        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<WeatherByDatetime>(getActivity(), list, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_weather_list;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, final int position, final WeatherByDatetime item) {

                    holder.setText(R.id.tv_hour, Util.dateToString(item.getDate(),"HH:mm")+ " WIB");

                    int hoursDiff = Util.hoursDifference(mTodayDate,item.getDate());
                    boolean terang = true;
                    if(hoursDiff > 0 && hoursDiff < 7){
                        if(!isQ3yes){
                            terang = false;
                        }
                    }

                    if(terang) {
                        holder.setText(R.id.tv_weather, Util.getWeatherTextByCode(getActivity(), item.getWeather()));
                        holder.setImageResource(R.id.iv_weather_icon, Util.getWeatherIconByCode(getActivity(),item.getWeather()));
                        if (isQ1yes) {
                            holder.setViewGone(R.id.tv_temperature, false);
                            holder.setText(R.id.tv_temperature, item.getTemperature() + "°C");
                        } else {
                            holder.setViewGone(R.id.tv_temperature, true);
                        }
                        if (isQ2yes) {
                            holder.setViewGone(R.id.tv_humid, false);
                            holder.setText(R.id.tv_humid, item.getHumidity() + "%");
                        } else {
                            holder.setViewGone(R.id.tv_humid, true);
                            holder.setViewGone(R.id.iv_droplet, true);
                        }
                    }else{
                        holder.setText(R.id.tv_weather, Util.getWeatherTextByCode(getActivity(), 60));
                        holder.setImageResource(R.id.iv_weather_icon, Util.getWeatherIconByCode(getActivity(),60));
                        if (isQ1yes) {
                            holder.setViewGone(R.id.tv_temperature, false);
                            holder.setText(R.id.tv_temperature,  "23°C");
                        } else {
                            holder.setViewGone(R.id.tv_temperature, true);
                        }
                        if (isQ2yes) {
                            holder.setViewGone(R.id.tv_humid, false);
                            holder.setText(R.id.tv_humid, "50%");
                        } else {
                            holder.setViewGone(R.id.tv_humid, true);
                            holder.setViewGone(R.id.iv_droplet, true);
                        }
                    }
                    holder.setText(R.id.tv_wd, Util.getWindDirectionByCode(getActivity(), item.getWindDirection()));

                }
            };
            mAdapter.setHasStableIds(true);
            mRecyclerview.setLayoutManager(layoutListManager);
            mRecyclerview.setItemAnimator(new DefaultItemAnimator());
            if (mRecyclerview.getItemAnimator() != null)
                mRecyclerview.getItemAnimator().setAddDuration(250);
            mRecyclerview.getItemAnimator().setMoveDuration(250);
            mRecyclerview.getItemAnimator().setChangeDuration(250);
            mRecyclerview.getItemAnimator().setRemoveDuration(250);
            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerview.setItemViewCacheSize(30);
            mRecyclerview.setAdapter(mAdapter);

        } else {
            mAdapter.setData(list);
        }

        mRecyclerview.scrollToPosition(0);
    }

    private void invalidateQ1(){
        rlQ1.setVisibility(View.GONE);
        rlQ2.setVisibility(View.VISIBLE);
    }

    private void invalidateQ2(){
        rlQ2.setVisibility(View.GONE);
        rlQ3.setVisibility(View.VISIBLE);
    }

    private void invalidateQ3(){
        rlQ3.setVisibility(View.GONE);
        mRecyclerview.setVisibility(View.VISIBLE);
        lldiagnosis.setVisibility(View.VISIBLE);
        reload();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.iv_next:
//                changePage(true);
//                break;
//            case R.id.iv_prev:
//                changePage(false);
//                break;
            case R.id.btn_q1_yes:
                isQ1yes = true;
                invalidateQ1();
                break;
            case R.id.btn_q1_no:
                isQ1yes = false;
                invalidateQ1();
                break;
            case R.id.btn_q2_yes:
                isQ2yes = true;
                invalidateQ2();
                break;
            case R.id.btn_q2_no:
                isQ2yes = false;
                invalidateQ2();
                break;
            case R.id.btn_q3_yes:
                isQ3yes = true;
                invalidateQ3();
                break;
            case R.id.btn_q3_no:
                isQ3yes = false;
                invalidateQ3();
                break;

        }
    }

    @Override
    public void onDataReady(ModelData data, ArrayList<ArrayList<WeatherByDatetime>> weatherListByPage) {

        mData = data;
        mWeatherListByPage = weatherListByPage;
        maxPage = Util.daysDifference(mTodayDate, (mData.getWeatherDataList().get(mData.getWeatherDataList().size() - 1)).getDate());
        doShowDataInView();

    }

}