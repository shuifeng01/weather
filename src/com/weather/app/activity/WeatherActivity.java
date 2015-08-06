package com.weather.app.activity;

import com.gxa.weather.R;
import com.weather.app.util.HttpCallbackListener;
import com.weather.app.util.HttpUtil;
import com.weather.app.util.Utility;

import android.R.integer;
import android.app.Activity;
import android.app.SearchManager.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 此活动是显示相应城市天气详情的主界面
 * @author Administrator
 *
 */
public class WeatherActivity extends Activity implements OnClickListener{
	/**
	 * 用于显示城市名,天气描述,发布时间
	 */
	private TextView cityName;
	private TextView weatherDesp;
	private TextView publishTime;
	/**
	 * 用于显示温度,风力等级等
	 */
	private ImageView temp_shi;
	private ImageView temp_ge;
	private TextView wdws;
	/**
	 * 更新按钮
	 */
	private ImageButton retry;
	private Button choose;
	/**
	 * 县级代号对应的天气代号
	 */
	private String weatherCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		init();//控件初始化
		retry.setOnClickListener(this);
		choose.setOnClickListener(this);
		String countyCode = getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)){
			//有县级代号时就去查询天气
			publishTime.setText("同步中...");
			cityName.setVisibility(View.INVISIBLE);
			queryWeatherCodeFromServer(countyCode);
		}else{
			showWeather();
		}
	}

	/**
	 * 查询县级代号对应的天气代号
	 */
	private void queryWeatherCodeFromServer(String countyCode){
		String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {			
			@Override
			public void onFinish(String result) {
				if(!TextUtils.isEmpty(result)){
					//从服务器返回的数据解析出天气代号
					String[] array = result.split("\\|");
					if(array != null && array.length == 2){
						weatherCode = array[1];//天气代号
						Log.d("weatherCode",weatherCode);
						queryWeatherInfo01(weatherCode);
					}
				}
			}		
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(WeatherActivity.this, "同步失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	/**
	 * 查询天气代号对应的天气信息01
	 */
	private void queryWeatherInfo01(String weatherCode){
		String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		Log.d("weatherCode",address);
		queryWeatherInfoFromServer(address,"info01");
	}
	/**
	 * 查询天气代号对应的天气信息02
	 */
	private void queryWeatherInfo02(String weatherCode){
		String address = "http://www.weather.com.cn/data/sk/"+weatherCode+".html";
		queryWeatherInfoFromServer(address,"info02");
	}
	/**
	 * 查询天气代号对应的天气信息03
	 */
	private void queryWeatherInfo03(String weatherCode){
		String address = "http://m.weather.com.cn/data/"+weatherCode+".html";
		queryWeatherInfoFromServer(address,"info03");
	}
	private void queryWeatherInfoFromServer(final String address,final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String result) {
				if("info01".equals(type)){
					//处理返回的天气信息01
					Utility.handleWeatherResponse(WeatherActivity.this, result);
					//查询天气信息02
					if(weatherCode != null){
						queryWeatherInfo02(weatherCode);
					}
				}else if("info02".equals(type)){
					//处理返回的天气信息02
					Utility.handleWeatherResponse_current(WeatherActivity.this, result);
					//查询天气信息03
					if(weatherCode != null){
						queryWeatherInfo03(weatherCode);
					}
				}else if("info03".equals(type)){
					//处理返回的天气信息03
					Utility.handleWeatherResponse_sixday(WeatherActivity.this, result);
					//天气信息已全部保存在sharedPreferences中,返回主线程更新UI
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							//显示天气信息
							showWeather();
						}
					});
				}				
			}
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(WeatherActivity.this, "同步失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityName.setText(prefs.getString("city_name",""));
		weatherDesp.setText(prefs.getString("weather_desp",""));
		publishTime.setText(prefs.getString("publish_time",""));
		String temp = prefs.getString("temp", "");
		int temp1 = Integer.parseInt(temp);		
		temp_shi.setBackgroundResource(R.drawable.cityselector_locate_centigrade_0+temp1%100/10);
		temp_ge.setBackgroundResource(R.drawable.cityselector_locate_centigrade_0+temp1%10);
		wdws.setText(prefs.getString("wdws",""));
		cityName.setVisibility(View.VISIBLE);		
	}
	/**
	 * 初始化控件
	 */
	private void init(){
		cityName = (TextView) findViewById(R.id.city);
		weatherDesp = (TextView) findViewById(R.id.weather_now);
		publishTime = (TextView) findViewById(R.id.ptime);

		temp_shi = (ImageView) findViewById(R.id.temp_shi);
		temp_ge = (ImageView) findViewById(R.id.temp_ge);
		wdws = (TextView) findViewById(R.id.WD_WS);
		//刷新按钮
		retry = (ImageButton) findViewById(R.id.retry);
		choose = (Button) findViewById(R.id.choose);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.retry:
			publishTime.setText("同步中...");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			weatherCode = prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo01(weatherCode);
			}
			break;
		case R.id.choose:
			Intent intent = new Intent(WeatherActivity.this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}

}
