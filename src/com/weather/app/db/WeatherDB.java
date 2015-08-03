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
 * 此类提供对数据库操作的方法
 * 分别是保存省,市,县的数据到数据库
 * 从数据库查询省,市,县的数据
 * @author Administrator
 *
 */
public class WeatherDB {
	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "weather";
	/**
	 * 数据库版本号
	 */
	public static final int VERSION = 1;
	
	private static WeatherDB weatherDB;
	
	private SQLiteDatabase db;	
	
	/**
	 * 将构造方法私有化,使用单例模式,只提供一个weatherDB的实例
	 * 在此构造方法中,调用DBOpenHelper的构造
	 * 获取db的实例
	 */
	private WeatherDB(Context context) {
		WeatherDBOpenHelper dbHelper = new WeatherDBOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	/**
	 * 获取WeatherDb的实例,单例模式的最佳体现
	 */
	public synchronized static WeatherDB getInstance(Context context){
		if(weatherDB == null){
			weatherDB = new WeatherDB(context);
		}
		return weatherDB;
	}
	/**
	 * 将Province实例,存储到数据库
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
	 * 从数据库中读取全国所有的省份信息,放入一个list,泛型为Province,返回
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
	 * 将City实例,存储到数据库
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
	 * 从数据库中读取相应省份的所有市的信息,放入一个list,泛型为City,返回
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
	 * 将County实例,存储到数据库
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
	 * 从数据库中读取相应市区的所有县的信息,放入一个list,泛型为County,返回
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
