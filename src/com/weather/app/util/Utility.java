package com.weather.app.util;

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
}
