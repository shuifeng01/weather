package com.weather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.weather.app.model.City;
import com.weather.app.model.County;
import com.weather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * �����ṩ�����ݿ�����ķ���
 * �ֱ��Ǳ���ʡ,��,�ص����ݵ����ݿ�
 * �����ݿ��ѯʡ,��,�ص�����
 * @author Administrator
 *
 */
public class WeatherDB {
	/**
	 * ���ݿ���
	 */
	public static final String DB_NAME = "weather";
	/**
	 * ���ݿ�汾��
	 */
	public static final int VERSION = 1;
	
	private static WeatherDB weatherDB;
	
	private SQLiteDatabase db;	
	
	/**
	 * �����췽��˽�л�,ʹ�õ���ģʽ,ֻ�ṩһ��weatherDB��ʵ��
	 * �ڴ˹��췽����,����DBOpenHelper�Ĺ���
	 * ��ȡdb��ʵ��
	 */
	private WeatherDB(Context context) {
		WeatherDBOpenHelper dbHelper = new WeatherDBOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	/**
	 * ��ȡWeatherDb��ʵ��,����ģʽ���������
	 */
	public synchronized static WeatherDB getInstance(Context context){
		if(weatherDB == null){
			weatherDB = new WeatherDB(context);
		}
		return weatherDB;
	}
	/**
	 * ��Provinceʵ��,�洢�����ݿ�
	 */
	public void saveProvince(Province province) {
		if(province != null){
			ContentValues cv = new ContentValues();
			cv.put("province_name", province.getProvinceName());
			cv.put("province_code", province.getProvinceCode());
			db.insert("province", null, cv);			
		}
	}
	/**
	 * �����ݿ��ж�ȡȫ�����е�ʡ����Ϣ,����һ��list,����ΪProvince,����
	 */
	public List<Province> loadProvince() {
		Cursor cursor = db.query("province", null, null, null, null, null, null);
		List<Province> list = new ArrayList<Province>();
		if(cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("_id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}
			while(cursor.moveToNext());			
		}
		if(cursor != null){
			cursor.close();
		}
		return list;		
	}
	/**
	 * ��Cityʵ��,�洢�����ݿ�
	 */
	public void saveCity(City city) {
		if(city != null){
			ContentValues cv = new ContentValues();
			cv.put("city_name", city.getCityName());
			cv.put("city_code", city.getCityCode());
			cv.put("province_id", city.getProvinceId());
			db.insert("city", null, cv);			
		}
	}
	/**
	 * �����ݿ��ж�ȡ��Ӧʡ�ݵ������е���Ϣ,����һ��list,����ΪCity,����
	 */
	public List<City> loadCity(int provinceId) {
		Cursor cursor = db.query("city", null, "province_id=?", new String[] {String.valueOf(provinceId)}, null, null, null);
		List<City> list = new ArrayList<City>();
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("_id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			}
			while(cursor.moveToNext());			
		}
		if(cursor != null){
			cursor.close();
		}
		return list;		
	}
	
	/**
	 * ��Countyʵ��,�洢�����ݿ�
	 */
	public void saveCounty(County county) {
		if(county != null){
			ContentValues cv = new ContentValues();
			cv.put("county_name", county.getCountyName());
			cv.put("county_code", county.getCountyCode());
			cv.put("city_id", county.getCityId());
			db.insert("county", null, cv);			
		}
	}
	/**
	 * �����ݿ��ж�ȡ��Ӧ�����������ص���Ϣ,����һ��list,����ΪCounty,����
	 */
	public List<County> loadCounty(int cityId) {
		List<County> list = new ArrayList<County>();
		
		Cursor cursor = db.query("county", null, "city_id=?", new String[] {String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("_id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			}
			while(cursor.moveToNext());			
		}
		if(cursor != null){
			cursor.close();
		}
		return list;		
	}
}
