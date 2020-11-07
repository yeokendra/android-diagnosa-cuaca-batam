package com.herman.diagnosiscuacabatam.ui.cuacaKota;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.herman.diagnosiscuacabatam.R;
import com.herman.diagnosiscuacabatam.base.BaseRecyclerAdapter;
import com.herman.diagnosiscuacabatam.base.BaseRecyclerViewHolder;
import com.herman.diagnosiscuacabatam.model.ModelData;
import com.herman.diagnosiscuacabatam.model.WeatherByDatetime;
import com.herman.diagnosiscuacabatam.util.Util;

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
import java.util.List;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class CuacaKotaFragment extends Fragment implements View.OnClickListener {

    private ModelData mData;
    private TextView tvDate;
    private AsyncTask getWeatherDataAsync;
    private RecyclerView mRecyclerview;
    private BaseRecyclerAdapter<WeatherByDatetime> mAdapter;
    private LinearLayoutManager layoutListManager;
    private LocalDateTime mTodayDate;
    private ImageView ivNext, ivPrev;

    private TextView tvSuhu, tvHumid;

    //max page 2
    private int maxPage = 1;
    private int currPage = 0;
    private ArrayList<ArrayList<WeatherByDatetime>> mWeatherListByPage = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cuaca_kota, container, false);
        tvDate = root.findViewById(R.id.tv_date);
        mRecyclerview = root.findViewById(R.id.rv_weather);
        ivNext = root.findViewById(R.id.iv_next);
        ivPrev = root.findViewById(R.id.iv_prev);

        tvSuhu = root.findViewById(R.id.tv_diag_temperature);
        tvHumid = root.findViewById(R.id.tv_diag_humidity);

        ivNext.setOnClickListener(this);
        ivPrev.setOnClickListener(this);

        layoutListManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        mTodayDate = LocalDateTime.now();
        currPage = 0;
        validateNextPrevVisibility();

        mData = new ModelData();
        try {
            getWeatherDataAsync = new RetrieveFeed().execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getWeatherDataAsync!=null){
            try {
                getWeatherDataAsync.cancel(true);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(getWeatherDataAsync!=null){
            try {
                getWeatherDataAsync.cancel(true);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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

        Log.e("err",currPage+" ");
        doShowDataInView();
        validateNextPrevVisibility();

    }

    private void validateNextPrevVisibility(){
        if(currPage == 0){
            ivPrev.setVisibility(View.GONE);
        }else{
            ivPrev.setVisibility(View.VISIBLE);
        }

        if(currPage == maxPage){
            ivNext.setVisibility(View.GONE);
        }else{
            ivNext.setVisibility(View.VISIBLE);
        }
    }

    private void doShowDataInView(){
        String date = Util.dateToString(mWeatherListByPage.get(currPage).get(0).getDate(),"EEEE, dd MMMM");
        tvDate.setText(date);

         setWeatherList(mWeatherListByPage.get(currPage));
         processWeatherDiagnosis(mWeatherListByPage.get(currPage));

    }

    private void processWeatherDiagnosis(ArrayList<WeatherByDatetime> list){

        boolean isTemperatureSet = false;
        int tmax = -1;
        int tmin = -1;

        boolean isHumidSet = false;
        int humax = -1;
        int humin = -1;

        for(int i=0; i<list.size(); i++){
            if(!isTemperatureSet){
                if(list.get(i).getTmax() >= 0){
                    tmax = list.get(i).getTmax();
                    if(tmin!=-1){
                        tvSuhu.setText(buildTemperatureString(tmin,tmax));
                        isTemperatureSet = true;
                    }
                }
                if(list.get(i).getTmin() >= 0){
                    tmin = list.get(i).getTmin();
                    if(tmax!=-1){
                        tvSuhu.setText(buildTemperatureString(tmin,tmax));
                        isTemperatureSet = true;
                    }
                }
            }
            if(!isHumidSet){
                if(list.get(i).getHumax() >= 0){
                    Log.e("err","called humax "+i);
                    humax = list.get(i).getHumax();
                    if(humin!=-1){
                        tvHumid.setText(buildHumidString(humin,humax));
                        isHumidSet = true;
                    }
                }
                if(list.get(i).getHumin() >= 0){
                    Log.e("err","called humin "+i);
                    humin = list.get(i).getHumin();
                    if(humax!=-1){
                        tvHumid.setText(buildHumidString(humin,humax));
                        isHumidSet = true;
                    }
                }
            }
        }
    }

    private StringBuilder buildTemperatureString(int tmin, int tmax){
        String suhuMin = "";
        String suhuMax = "";

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.temperature_pre));
        suhuMin = Util.getTemperature(getActivity(),tmin);
        suhuMax = Util.getTemperature(getActivity(),tmax);
        sb.append(" "+suhuMin);
        if(!suhuMax.equals(suhuMin)){
            sb.append("-"+suhuMax);
        }
        sb.append(" "+getString(R.string.temperature_middle));
        sb.append(" "+tmin + "°C ~ "+tmax+"°C.");
        return sb;
    }

    private StringBuilder buildHumidString(int humin, int humax){
        String huMax = "";
        String huMin = "";

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.humid_pre));
        huMin = Util.getHumid(getActivity(),humin);
        huMax = Util.getHumid(getActivity(),humax);
        sb.append(" "+huMin);
        if(!huMax.equals(huMin)){
            sb.append("-"+huMax);
        }
        sb.append(" "+getString(R.string.humid_middle));
        sb.append(" "+humin+"% ~ "+humax+"%.");

        return sb;
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
                    holder.setText(R.id.tv_temperature, item.getTemperature() + "°C");
                    holder.setText(R.id.tv_weather, Util.getWeatherTextByCode(getActivity(),item.getWeather()));
                    holder.setImageResource(R.id.iv_weather_icon, Util.getWeatherIconByCode(getActivity(),item.getWeather()));
                }
            };
            mAdapter.setHasStableIds(true);
            mRecyclerview.setLayoutManager(layoutListManager);
            //mRecyclerview.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(this, 16)));
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_next:
                changePage(true);
                break;
            case R.id.iv_prev:
                changePage(false);
                break;
        }
    }

    public class RetrieveFeed extends AsyncTask {

        URL url;
        String formatted = "";
        JSONObject json = new JSONObject();

        @Override
        protected Object doInBackground(Object[] objects) {
            // Initializing instance variables


            try {
                url = new URL("https://data.bmkg.go.id/datamkg/MEWS/DigitalForecast/DigitalForecast-KepulauanRiau.xml");
            }catch (Exception e){

            }
            if(url!=null) {
                AssetManager assetManager = getActivity().getAssets();
                InputStream inputStream = getInputStream(url);
                XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null).build();
                try {
                    inputStream.close();
                }catch (Exception e){

                }

                formatted = xmlToJson.toFormattedString();
                json = xmlToJson.toJson();



                JSONArray jsonAreaList = new JSONArray();
                try {
                    jsonAreaList = json.getJSONObject("data").getJSONObject("forecast").getJSONArray("area");


                    for(int i=0; i<jsonAreaList.length(); i++){
                        JSONObject jsonArea = jsonAreaList.getJSONObject(i);

                        //area id 501601 = batam
                        if(jsonArea.getInt("id") == 501601){
                            JSONArray jsonParameterList = jsonArea.getJSONArray("parameter");
                            Log.e("err",jsonParameterList.toString());
                            Log.e("err"," length = " +jsonParameterList.length());

                            //id hu = humidity
                            //id t = temperature
                            //id weather = weather
                            //id wd = wind direction
                            //id ws = wind speed
                            //id humax, humin = humidity max & min
                            //id tmax, tmin = temperature max & min

                            JSONObject jsonParameter;
                            for(int j=0; j<jsonParameterList.length();j++){
                                jsonParameter = jsonParameterList.getJSONObject(j);
                                Log.e("err",jsonParameter.getString("id"));
                                //WeatherByDatetime weatherByDatetime = new WeatherByDatetime();

                                if(jsonParameter.getString("id").equals("hu") || jsonParameter.getString("id").equals("t") || jsonParameter.getString("id").equals("weather")
                                        || jsonParameter.getString("id").equals("wd") || jsonParameter.getString("id").equals("ws") || jsonParameter.getString("id").equals("humax")
                                        || jsonParameter.getString("id").equals("humin") || jsonParameter.getString("id").equals("tmax") || jsonParameter.getString("id").equals("tmin")){
//                                    Log.e("err","yep");
                                    JSONArray jsonTimerangeList = jsonParameter.getJSONArray("timerange");
                                    Log.e("err",jsonTimerangeList.toString());


                                    if(jsonParameter.getString("id").equals("hu") || jsonParameter.getString("id").equals("weather")){
                                        //loop through Humidity or Weather
                                        for(int k=0; k<jsonTimerangeList.length(); k++){
                                            JSONObject jsonHu = jsonTimerangeList.getJSONObject(k);
                                            int pos = mData.findWeatherDataPosByDate(jsonHu.getString("datetime"));
                                            if(jsonParameter.getString("id").equals("hu")) {
                                                mData.getWeatherDataList().get(pos).setHumidity(jsonHu.getJSONObject("value").getInt("content"));
                                            }else if(jsonParameter.getString("id").equals("weather")){
                                                mData.getWeatherDataList().get(pos).setWeather(jsonHu.getJSONObject("value").getInt("content"));
                                            }
                                            Log.e("err",mData.getWeatherDataList().get(pos).getHumidity()+" ");
                                        }
                                    }else if(jsonParameter.getString("id").equals("t")){
                                        //loop through Temperature
                                        for(int k=0; k<jsonTimerangeList.length(); k++){
                                            JSONObject jsonT = jsonTimerangeList.getJSONObject(k);
                                            int pos = mData.findWeatherDataPosByDate(jsonT.getString("datetime"));
                                            for(int l=0; l<jsonT.getJSONArray("value").length();l++){
                                                JSONObject jsonValue = jsonT.getJSONArray("value").getJSONObject(l);
                                                if(jsonValue.getString("unit").equals("C")){
                                                    mData.getWeatherDataList().get(pos).setTemperature(jsonValue.getInt("content"));
                                                    Log.e("err",mData.getWeatherDataList().get(pos).getTemperature()+" ");
                                                    break;
                                                }
                                            }
                                        }
                                    }else if(jsonParameter.getString("id").equals("wd")){
                                        //loop through Wind Direction
                                        for(int k=0; k<jsonTimerangeList.length(); k++){
                                            JSONObject jsonWd = jsonTimerangeList.getJSONObject(k);
                                            int pos = mData.findWeatherDataPosByDate(jsonWd.getString("datetime"));
                                            for(int l=0; l<jsonWd.getJSONArray("value").length();l++){
                                                JSONObject jsonValue = jsonWd.getJSONArray("value").getJSONObject(l);
                                                if(jsonValue.getString("unit").equalsIgnoreCase("CARD")){
                                                    mData.getWeatherDataList().get(pos).setWindDirection(jsonValue.getString("content"));
                                                    Log.e("err",mData.getWeatherDataList().get(pos).getWindDirection()+" ");
                                                    break;
                                                }
                                            }
                                        }
                                    }else if(jsonParameter.getString("id").equals("ws")){
                                        //loop through Wind Speed
                                        for(int k=0; k<jsonTimerangeList.length(); k++){
                                            JSONObject jsonWs = jsonTimerangeList.getJSONObject(k);
                                            int pos = mData.findWeatherDataPosByDate(jsonWs.getString("datetime"));
                                            for(int l=0; l<jsonWs.getJSONArray("value").length();l++){
                                                JSONObject jsonValue = jsonWs.getJSONArray("value").getJSONObject(l);
                                                if(jsonValue.getString("unit").equalsIgnoreCase("KPH")){
                                                    mData.getWeatherDataList().get(pos).setWindSpeed(jsonValue.getDouble("content"));
                                                    Log.e("err",mData.getWeatherDataList().get(pos).getWindSpeed()+" ");
                                                    break;
                                                }
                                            }
                                        }
                                    }else if(jsonParameter.getString("id").equals("humax") || jsonParameter.getString("id").equals("humin")) {
                                        //loop through Humax or Humin
                                        for(int k=0; k<jsonTimerangeList.length(); k++) {
                                            JSONObject jsonHuM = jsonTimerangeList.getJSONObject(k);
                                            int pos = mData.findWeatherDataPosByDate(jsonHuM.getString("datetime"));
                                            if(jsonParameter.getString("id").equals("humax")) {
                                                mData.getWeatherDataList().get(pos).setHumax(jsonHuM.getJSONObject("value").getInt("content"));
                                                Log.e("err",mData.getWeatherDataList().get(pos).getHumax()+" ");
                                            }else if(jsonParameter.getString("id").equals("humin")){
                                                mData.getWeatherDataList().get(pos).setHumin(jsonHuM.getJSONObject("value").getInt("content"));
                                                Log.e("err",mData.getWeatherDataList().get(pos).getHumin()+" ");
                                            }
                                        }
                                    }else if(jsonParameter.getString("id").equals("tmax") || jsonParameter.getString("id").equals("tmin")){
                                        //loop through Tmax or Tmin
                                        for(int k=0; k<jsonTimerangeList.length(); k++){
                                            JSONObject jsonTM = jsonTimerangeList.getJSONObject(k);
                                            int pos = mData.findWeatherDataPosByDate(jsonTM.getString("datetime"));
                                            for(int l=0; l<jsonTM.getJSONArray("value").length();l++){
                                                JSONObject jsonValue = jsonTM.getJSONArray("value").getJSONObject(l);
                                                if(jsonValue.getString("unit").equals("C")){
                                                    if(jsonParameter.getString("id").equals("tmax")) {
                                                        mData.getWeatherDataList().get(pos).setTmax(jsonValue.getInt("content"));
                                                        Log.e("err",mData.getWeatherDataList().get(pos).getTmax()+" ");
                                                    }else if(jsonParameter.getString("id").equals("tmin")) {
                                                        mData.getWeatherDataList().get(pos).setTmin(jsonValue.getInt("content"));
                                                        Log.e("err",mData.getWeatherDataList().get(pos).getTmin()+" ");
                                                    }

                                                    break;
                                                }
                                            }
                                        }
                                    }



                                }else{
                                    Log.e("err","nope");
                                }

                            }

                            maxPage = Util.daysDifference(mTodayDate,(mData.getWeatherDataList().get(mData.getWeatherDataList().size()-1)).getDate());
                            Log.e("err",maxPage+" < max page");
                            mWeatherListByPage.clear();
                            for(int x=0; x <= maxPage ; x++){
                                mWeatherListByPage.add(new ArrayList<WeatherByDatetime>());
                                for(int y=0;y < mData.getWeatherDataList().size(); y++){
                                    if(Util.daysDifference(mTodayDate,mData.getWeatherDataList().get(y).getDate()) == x){
                                        mWeatherListByPage.get(x).add(mData.getWeatherDataList().get(y));
                                        //Log.e("err","today = "+mTodayDate.toString()+", data = "+mData.getWeatherDataList().get(y).getDate() + " differecence = " + Util.daysDifference(mTodayDate,mData.getWeatherDataList().get(y).getDate()));
                                    }
                                }
                            }
//                            formatted = jsonTimerange.getString("datetime");
//                            Log.e("err",formatted);
                        }
                    }




                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("err",e.toString());
                }
            }


            return 1;
        }

        @Override
        protected void onPostExecute(Object o) {

            //do sort
            Collections.sort(mData.getWeatherDataList(), new Comparator<WeatherByDatetime>() {
                @Override
                public int compare(WeatherByDatetime data1, WeatherByDatetime data2) {
                    return data1.getDatetime().compareTo(data2.getDatetime());
                }
            });

            doShowDataInView();

        }

        public InputStream getInputStream(URL url) {
            try {
                return url.openConnection().getInputStream();
            } catch (IOException e) {
                return null;
            }
        }


    }


}