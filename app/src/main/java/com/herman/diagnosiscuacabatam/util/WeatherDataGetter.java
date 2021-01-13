package com.herman.diagnosiscuacabatam.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.herman.diagnosiscuacabatam.R;
import com.herman.diagnosiscuacabatam.model.ModelData;
import com.herman.diagnosiscuacabatam.model.WeatherByDatetime;
import com.herman.diagnosiscuacabatam.ui.cuacaKota.CuacaKotaFragment;

import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class WeatherDataGetter {

    private AsyncTask getWeatherDataAsync;
    private ModelData mData = new ModelData();
    private ArrayList<ArrayList<WeatherByDatetime>> mWeatherListByPage = new ArrayList<>();
    private int maxPage;
    private int cityCode;


    public interface WeatherDataPresenter {
        void onDataReady(ModelData data, ArrayList<ArrayList<WeatherByDatetime>> weatherListByPage);
    }

    public void getWeather(Context context,Integer cityCode, WeatherDataPresenter presenter){
        if(cityCode != 0 && cityCode != -1) {
            this.cityCode = cityCode;
        }else{
            //batam
            this.cityCode = 501601;
        }
        try {
            getWeatherDataAsync = new RetrieveFeed(context, presenter).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class RetrieveFeed extends AsyncTask {

        URL url;
        String formatted = "";
        JSONObject json = new JSONObject();
        Context context;
        WeatherDataPresenter presenter;
        LocalDateTime mTodayDate;

        RetrieveFeed(Context context, WeatherDataPresenter presenter){
            this.context = context;
            this.presenter = presenter;

            mTodayDate = LocalDateTime.now();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            // Initializing instance variables


            try {
                url = new URL("https://data.bmkg.go.id/datamkg/MEWS/DigitalForecast/DigitalForecast-KepulauanRiau.xml");
            } catch (Exception e) {

            }
            if (url != null) {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = getInputStream(url);
                XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null).build();
                try {
                    inputStream.close();
                } catch (Exception e) {

                }

                formatted = xmlToJson.toFormattedString();
                json = xmlToJson.toJson();


                JSONArray jsonAreaList = new JSONArray();
                try {
                    jsonAreaList = json.getJSONObject("data").getJSONObject("forecast").getJSONArray("area");


                    for (int i = 0; i < jsonAreaList.length(); i++) {
                        JSONObject jsonArea = jsonAreaList.getJSONObject(i);

                        //area id 501601 = batam
                        if (jsonArea.getInt("id") == cityCode) {
                            JSONArray jsonParameterList = jsonArea.getJSONArray("parameter");
                            //.e("err",jsonParameterList.toString());
                            //Log.e("err"," length = " +jsonParameterList.length());

                            //id hu = humidity
                            //id t = temperature
                            //id weather = weather
                            //id wd = wind direction
                            //id ws = wind speed
                            //id humax, humin = humidity max & min
                            //id tmax, tmin = temperature max & min

                            JSONObject jsonParameter;
                            for (int j = 0; j < jsonParameterList.length(); j++) {
                                jsonParameter = jsonParameterList.getJSONObject(j);
                                //Log.e("err",jsonParameter.getString("id"));
                                //WeatherByDatetime weatherByDatetime = new WeatherByDatetime();

                                if (jsonParameter.getString("id").equals("hu") || jsonParameter.getString("id").equals("t") || jsonParameter.getString("id").equals("weather")
                                        || jsonParameter.getString("id").equals("wd") || jsonParameter.getString("id").equals("ws") || jsonParameter.getString("id").equals("humax")
                                        || jsonParameter.getString("id").equals("humin") || jsonParameter.getString("id").equals("tmax") || jsonParameter.getString("id").equals("tmin")) {

                                    JSONArray jsonTimerangeList = jsonParameter.getJSONArray("timerange");
                                    //Log.e("err",jsonTimerangeList.toString());


                                    if (jsonParameter.getString("id").equals("hu") || jsonParameter.getString("id").equals("weather")) {
                                        //loop through Humidity or Weather
                                        for (int k = 0; k < jsonTimerangeList.length(); k++) {
                                            JSONObject jsonHu = jsonTimerangeList.getJSONObject(k);
                                            int pos = mData.findWeatherDataPosByDate(jsonHu.getString("datetime"));
                                            if (jsonParameter.getString("id").equals("hu")) {
                                                mData.getWeatherDataList().get(pos).setHumidity(jsonHu.getJSONObject("value").getInt("content"));
                                            } else if (jsonParameter.getString("id").equals("weather")) {
                                                mData.getWeatherDataList().get(pos).setWeather(jsonHu.getJSONObject("value").getInt("content"));
                                            }
                                            Log.e("err", mData.getWeatherDataList().get(pos).getHumidity() + " ");
                                        }
                                    } else if (jsonParameter.getString("id").equals("t")) {
                                        //loop through Temperature
                                        for (int k = 0; k < jsonTimerangeList.length(); k++) {
                                            JSONObject jsonT = jsonTimerangeList.getJSONObject(k);
                                            int pos = mData.findWeatherDataPosByDate(jsonT.getString("datetime"));
                                            for (int l = 0; l < jsonT.getJSONArray("value").length(); l++) {
                                                JSONObject jsonValue = jsonT.getJSONArray("value").getJSONObject(l);
                                                if (jsonValue.getString("unit").equals("C")) {
                                                    mData.getWeatherDataList().get(pos).setTemperature(jsonValue.getInt("content"));
                                                    Log.e("err", mData.getWeatherDataList().get(pos).getTemperature() + " ");
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (jsonParameter.getString("id").equals("wd")) {
                                        //loop through Wind Direction
                                        for (int k = 0; k < jsonTimerangeList.length(); k++) {
                                            JSONObject jsonWd = jsonTimerangeList.getJSONObject(k);
                                            int pos = mData.findWeatherDataPosByDate(jsonWd.getString("datetime"));
                                            for (int l = 0; l < jsonWd.getJSONArray("value").length(); l++) {
                                                JSONObject jsonValue = jsonWd.getJSONArray("value").getJSONObject(l);
                                                if (jsonValue.getString("unit").equalsIgnoreCase("CARD")) {
                                                    mData.getWeatherDataList().get(pos).setWindDirection(jsonValue.getString("content"));
                                                    Log.e("err", mData.getWeatherDataList().get(pos).getWindDirection() + " ");
                                                    break;
                                                }
                                                if (jsonValue.getString("unit").equalsIgnoreCase("deg")) {
                                                    mData.getWeatherDataList().get(pos).setWindDegree(jsonValue.getDouble("content"));
                                                    Log.e("err", mData.getWeatherDataList().get(pos).getWindDegree() + " ");
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (jsonParameter.getString("id").equals("ws")) {
                                        //loop through Wind Speed
                                        for (int k = 0; k < jsonTimerangeList.length(); k++) {
                                            JSONObject jsonWs = jsonTimerangeList.getJSONObject(k);
                                            int pos = mData.findWeatherDataPosByDate(jsonWs.getString("datetime"));
                                            for (int l = 0; l < jsonWs.getJSONArray("value").length(); l++) {
                                                JSONObject jsonValue = jsonWs.getJSONArray("value").getJSONObject(l);
                                                if (jsonValue.getString("unit").equalsIgnoreCase("KPH")) {
                                                    mData.getWeatherDataList().get(pos).setWindSpeed(jsonValue.getDouble("content"));
                                                    Log.e("err", mData.getWeatherDataList().get(pos).getWindSpeed() + " ");
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (jsonParameter.getString("id").equals("humax") || jsonParameter.getString("id").equals("humin")) {
                                        //loop through Humax or Humin
                                        for (int k = 0; k < jsonTimerangeList.length(); k++) {
                                            JSONObject jsonHuM = jsonTimerangeList.getJSONObject(k);
                                            int pos = mData.findWeatherDataPosByDate(jsonHuM.getString("datetime"));
                                            if (jsonParameter.getString("id").equals("humax")) {
                                                mData.getWeatherDataList().get(pos).setHumax(jsonHuM.getJSONObject("value").getInt("content"));
                                                Log.e("err", mData.getWeatherDataList().get(pos).getHumax() + " ");
                                            } else if (jsonParameter.getString("id").equals("humin")) {
                                                mData.getWeatherDataList().get(pos).setHumin(jsonHuM.getJSONObject("value").getInt("content"));
                                                Log.e("err", mData.getWeatherDataList().get(pos).getHumin() + " ");
                                            }
                                        }
                                    } else if (jsonParameter.getString("id").equals("tmax") || jsonParameter.getString("id").equals("tmin")) {
                                        //loop through Tmax or Tmin
                                        for (int k = 0; k < jsonTimerangeList.length(); k++) {
                                            JSONObject jsonTM = jsonTimerangeList.getJSONObject(k);
                                            int pos = mData.findWeatherDataPosByDate(jsonTM.getString("datetime"));
                                            for (int l = 0; l < jsonTM.getJSONArray("value").length(); l++) {
                                                JSONObject jsonValue = jsonTM.getJSONArray("value").getJSONObject(l);
                                                if (jsonValue.getString("unit").equals("C")) {
                                                    if (jsonParameter.getString("id").equals("tmax")) {
                                                        mData.getWeatherDataList().get(pos).setTmax(jsonValue.getInt("content"));
                                                        Log.e("err", mData.getWeatherDataList().get(pos).getTmax() + " ");
                                                    } else if (jsonParameter.getString("id").equals("tmin")) {
                                                        mData.getWeatherDataList().get(pos).setTmin(jsonValue.getInt("content"));
                                                        Log.e("err", mData.getWeatherDataList().get(pos).getTmin() + " ");
                                                    }

                                                    break;
                                                }
                                            }
                                        }
                                    }


                                } else {
                                    Log.e("err", "nope");
                                }

                            }

                            maxPage = Util.daysDifference(mTodayDate, (mData.getWeatherDataList().get(mData.getWeatherDataList().size() - 1)).getDate());
                            Log.e("err", maxPage + " < max page");
                            mWeatherListByPage.clear();
                            for (int x = 0; x <= maxPage; x++) {
                                mWeatherListByPage.add(new ArrayList<WeatherByDatetime>());
                                for (int y = 0; y < mData.getWeatherDataList().size(); y++) {
                                    if (Util.daysDifference(mTodayDate, mData.getWeatherDataList().get(y).getDate()) == x) {
                                        mWeatherListByPage.get(x).add(mData.getWeatherDataList().get(y));
                                        //Log.e("err","today = "+mTodayDate.toString()+", data = "+mData.getWeatherDataList().get(y).getDate() + " differecence = " + Util.daysDifference(mTodayDate,mData.getWeatherDataList().get(y).getDate()));
                                    }
                                }
                            }
//                            formatted = jsonTimerange.getString("datetime");
//                            Log.e("err",formatted);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("err", e.toString());
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

            presenter.onDataReady(mData,mWeatherListByPage);

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
