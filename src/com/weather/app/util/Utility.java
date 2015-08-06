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
 * ����Ϊһ��������,�ṩ����,���� ʡ�������ݵķ���
 * @author Administrator
 *
 */
public class Utility {
	/**
	 * ����������������ص�ʡ������,���������ݿ���
	 * @param weatherDB:���ݿ⴦�����,�ṩ�������ݿ�ĸ��෽��
	 * @param response:���������ص���Ҫ����������
	 * @return :���ش����Ƿ�ɹ�
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
					//�����������ݴ浽Province��
					weatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * ������Ӧ���е�����,�������ݿ�
	 * @param weatherDB:���ݿ�ʵ��
	 * @param response:Ҫ���������
	 * @param provinceId:��Ӧ��ʡ��
	 * @return :���ش����Ƿ�ɹ�
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
					//�����������ݴ浽City��
					weatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;

	}
	/**
	 * ������Ӧ�ص�����,�������ݿ�
	 * @param weatherDB:���ݿ�ʵ��
	 * @param response:Ҫ���������
	 * @param cityId:��Ӧ����
	 * @return :���ش����Ƿ�ɹ�
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
					//�����������ݴ浽county��
					weatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * �������������ص�json����,���洢������
	 * ���ǽӿ�http://www.weather.com.cn/data/cityinfo/101010100.html�е���Ϣ
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
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��",Locale.CHINA);
			publishTime = sdf.format(new Date())+" "+publishTime+" "+"����";
			saveWeatherInfo(context, cityName, weatherCode, weatherDesp, publishTime);
		} catch (JSONException e) {			
			e.printStackTrace();
		}
	}
	/**
	 * ��������Ϣ(������,����,��������,����ʱ��)�洢��SharedPreferences�ļ���,
	 * ���ǽӿ�http://www.weather.com.cn/data/cityinfo/101010100.html�е���Ϣ
	 * @param context:�Ự����
	 * @param cityName:������
	 * @param weatherCode:��������
	 * @param weatherDesp:��������
	 * @param publishTime:����ʱ��
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
	 * �������������ص�json����,���洢������,������Ϣ(��ǰ�¶Ⱥͷ����ȼ�)
	 * ���ǽӿ�http://www.weather.com.cn/data/sk/101010100.html�е���Ϣ
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
			String wdws = wd+","+"����"+ws;
			saveWeatherInfo_current(context, temp, wdws);
		} catch (JSONException e) {			
			e.printStackTrace();
		}
	}
	/**
	 * ��������Ϣ(��ǰ�¶Ⱥͷ����ȼ�)�洢��SharedPreferences�ļ���,
	 * ���ǽӿ�http://www.weather.com.cn/data/sk/101010100.html�е���Ϣ
	 * @param context:�Ự����
	 * @param temp:�����¶�
	 * @param wdws:�����ȼ�
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
	 * δ������������Ϣ
	 * �������������ص�json����,���洢������
	 * ���ǽӿ�http://m.weather.com.cn/data/101010100.html�е���Ϣ
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse_sixday(Context context,String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			//����ʱ��
			String date_y = weatherInfo.getString("date_y");
			String week = weatherInfo.getString("week");
			 //δ�����������¶�
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String temp3 = weatherInfo.getString("temp3");
			String temp4 = weatherInfo.getString("temp4");
			String temp5 = weatherInfo.getString("temp5");
			String temp6 = weatherInfo.getString("temp6");
			//δ��������������;
			String weather1 = weatherInfo.getString("weather1");
			String weather2 = weatherInfo.getString("weather2");
			String weather3 = weatherInfo.getString("weather3");
			String weather4 = weatherInfo.getString("weather4");
			String weather5 = weatherInfo.getString("weather5");
			String weather6 = weatherInfo.getString("weather6");
			/**
			 * ��δ������������Ϣ(.�¶Ⱥ�����)�洢��SharedPreferences�ļ���,
			 * ���ǽӿ�http://m.weather.com.cn/data/101010100.html�е���Ϣ
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
