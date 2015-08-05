package com.weather.app.util;

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
}
