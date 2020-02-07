package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.air.Air;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.ForecastBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.Lifestyle;

public class Utility {
    public static boolean handleProvinceResponse(String response){
        //解析省级数据
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvinces = new JSONArray(response);
                for(int i = 0; i < allProvinces.length(); i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCityResponse(String response, int provinceId){
        //解析市级数据
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCities = new JSONArray(response);
                for(int i = 0; i < allCities.length(); i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCountyResponse(String response, int cityId){
        //解析市级数据
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounties = new JSONArray(response);
                for(int i = 0; i < allCounties.length(); i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    public static Weather handleWeatherResponce(String responce){
        try {
            JSONObject jsonObject = new JSONObject(responce);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String  weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static Weather newhandleWeatherResponce(interfaces.heweather.com.interfacesmodule.bean.weather.Weather responce1
            , Air responce2, List<ForecastBase> responce3, Lifestyle responce4){
        Weather weather = new Weather();
        Forecast tmp = new Forecast();
        weather.status = responce1.getStatus();
        weather.basic.cityName = responce1.getBasic().getLocation();
        weather.basic.weatherId = responce1.getBasic().getCid();
        weather.basic.update.updateTime = responce1.getUpdate().getLoc();
        weather.now.temperature = responce1.getNow().getTmp();
        weather.now.more.info = responce1.getNow().getCond_txt();
        weather.aqi.city.aqi = responce2.getAir_now_city().getAqi();
        weather.aqi.city.pm25 = responce2.getAir_now_city().getPm25();
        int i = 0;
        while(i<responce3.size()){
            tmp.date = responce3.get(i).getDate();
            tmp.tmeperature.max = responce3.get(i).getTmp_max();
            tmp.tmeperature.min = responce3.get(i).getTmp_min();
            tmp.more.info = responce3.get(i).getCond_code_d();
            weather.forecastList.add(tmp);
        }
        while (i<responce4.getLifestyle().size()){
            if(responce4.getLifestyle().get(i).getType().equals("comf")){
                weather.suggestion.comfort.info = responce4.getLifestyle().get(i).getTxt();
            }
            else if(responce4.getLifestyle().get(i).getType().equals("cw")){
                weather.suggestion.carWash.info = responce4.getLifestyle().get(i).getTxt();
            }
            else if(responce4.getLifestyle().get(i).getType().equals("sport")){
                weather.suggestion.sport.info = responce4.getLifestyle().get(i).getTxt();
            }
        }
        return weather;
    }
}

