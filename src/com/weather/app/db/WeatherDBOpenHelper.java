package com.weather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 建立全国各省,市,县的数据表
 * @author Administrator
 *
 */
public class WeatherDBOpenHelper extends SQLiteOpenHelper{

	/**
	 * Province(省)表的建表语句
	 */
	public static final String CREATE_PROVINCE="create table province ( _id integer primary key autoincrement, province_name text, province_code text)";
	/**
	 * City(市)表的建表语句
	 */
	public static final String CREATE_CITY="create table city ( _id integer primary key autoincrement, city_name text, city_code text,province_id integer)";
	/**
	 * County(县)表的建表语句
	 */
	public static final String CREATE_COUNTY="create table county ( _id integer primary key autoincrement, county_name text, county_code text, city_id integer)";
	
	/*
	 * 有参构造
	 */
	public WeatherDBOpenHelper(Context context, String name,
			CursorFactory factory, int version) {		
		super(context, name, factory, version);
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

}
