package com.weather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.weather.app.db.WeatherDB;
import com.weather.app.model.City;
import com.weather.app.model.County;
import com.weather.app.model.Province;

/**
 * 此类为一个工具类,提供解析,处理 省市县数据的方法
 * @author Administrator
 *
 */
public class Utility {
	/**
	 * 解析处理服务器返回的省级数据,保存于数据库中
	 * @param weatherDB:数据库处理对象,提供操作数据库的各类方法
	 * @param response:服务器返回的需要解析的数据
	 * @return :返回处理是否成功
	 */
	public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB,
			String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			if(allProvinces != null && allProvinces.length > 0){
				for(String s : allProvinces){
					String[] array = s.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//解析出的数据存到Province表
					weatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 处理相应城市的数据,存于数据库
	 * @param weatherDB:数据库实例
	 * @param response:要处理的数据
	 * @param provinceId:对应的省份
	 * @return :返回处理是否成功
	 */
	public static boolean handleCitiesResponse (WeatherDB weatherDB,
			String response, int provinceId){		
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			if(allCities != null && allCities.length > 0){
				for(String s : allCities){
					String[] array = s.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//解析出的数据存到City表
					weatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;

	}
	/**
	 * 处理相应县的数据,存于数据库
	 * @param weatherDB:数据库实例
	 * @param response:要处理的数据
	 * @param cityId:对应的市
	 * @return :返回处理是否成功
	 */
	public static boolean handleCountiesResponse (WeatherDB weatherDB,
			String response, int cityId){		
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			if(allCounties != null && allCounties.length > 0){
				for(String s : allCounties){
					String[] array = s.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//解析出的数据存到county表
					weatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 解析服务器返回的json数据,并存储到本地
	 * 这是接口http://www.weather.com.cn/data/cityinfo/101010100.html中的信息
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
			publishTime = sdf.format(new Date())+" "+publishTime+" "+"发布";
			saveWeatherInfo(context, cityName, weatherCode, weatherDesp, publishTime);
		} catch (JSONException e) {			
			e.printStackTrace();
		}
	}
	/**
	 * 将天气信息(城市名,代码,天气描述,发布时间)存储到SharedPreferences文件中,
	 * 这是接口http://www.weather.com.cn/data/cityinfo/101010100.html中的信息
	 * @param context:会话对象
	 * @param cityName:城市名
	 * @param weatherCode:天气代码
	 * @param weatherDesp:天气描述
	 * @param publishTime:发布时间
	 */
	public static void saveWeatherInfo(Context context,String cityName,String weatherCode
			,String weatherDesp,String publishTime){
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.commit();
	}
	/**
	 * 解析服务器返回的json数据,并存储到本地,天气信息(当前温度和风力等级)
	 * 这是接口http://www.weather.com.cn/data/sk/101010100.html中的信息
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse_current(Context context,String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String temp = weatherInfo.getString("temp");
			String wd = weatherInfo.getString("WD");
			String ws = weatherInfo.getString("WS");
			String wdws = wd+","+"风力"+ws;
			saveWeatherInfo_current(context, temp, wdws);
		} catch (JSONException e) {			
			e.printStackTrace();
		}
	}
	/**
	 * 将天气信息(当前温度和风力等级)存储到SharedPreferences文件中,
	 * 这是接口http://www.weather.com.cn/data/sk/101010100.html中的信息
	 * @param context:会话对象
	 * @param temp:当日温度
	 * @param wdws:风力等级
	 */
	public static void saveWeatherInfo_current(Context context,String temp,String wdws){
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("temp", temp);
		editor.putString("wdws", wdws);
		editor.commit();
	}
	
	/**
	 * 未来六天天气信息
	 * 解析服务器返回的json数据,并存储到本地
	 * 这是接口http://m.weather.com.cn/data/101010100.html中的信息
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse_sixday(Context context,String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			//当天时间
			String date_y = weatherInfo.getString("date_y");
			String week = weatherInfo.getString("week");
			 //未来六天摄氏温度
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String temp3 = weatherInfo.getString("temp3");
			String temp4 = weatherInfo.getString("temp4");
			String temp5 = weatherInfo.getString("temp5");
			String temp6 = weatherInfo.getString("temp6");
			//未来六天天气描述;
			String weather1 = weatherInfo.getString("weather1");
			String weather2 = weatherInfo.getString("weather2");
			String weather3 = weatherInfo.getString("weather3");
			String weather4 = weatherInfo.getString("weather4");
			String weather5 = weatherInfo.getString("weather5");
			String weather6 = weatherInfo.getString("weather6");
			/**
			 * 将未来六天天气信息(.温度和描述)存储到SharedPreferences文件中,
			 * 这是接口http://m.weather.com.cn/data/101010100.html中的信息
			 */
			SharedPreferences.Editor editor = PreferenceManager
					.getDefaultSharedPreferences(context).edit();
			editor.putBoolean("city_selected", true);
			editor.putString("date_y", date_y);
			editor.putString("week", week);
			editor.putString("temp1", temp1);
			editor.putString("weather1", weather1);
			editor.putString("temp2", temp2);
			editor.putString("weather2", weather2);
			editor.putString("temp3", temp3);
			editor.putString("weather3", weather3);
			editor.putString("temp4", temp4);
			editor.putString("weather4", weather4);
			editor.putString("temp5", temp5);
			editor.putString("weather5", weather5);
			editor.putString("temp6", temp6);
			editor.putString("weather6", weather6);
			editor.commit();
		} catch (JSONException e) {			
			e.printStackTrace();
		}
	}

}
