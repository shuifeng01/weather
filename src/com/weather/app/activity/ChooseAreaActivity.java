package com.weather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.gxa.weather.R;
import com.weather.app.db.WeatherDB;
import com.weather.app.model.City;
import com.weather.app.model.County;
import com.weather.app.model.Province;
import com.weather.app.util.HttpCallbackListener;
import com.weather.app.util.HttpUtil;
import com.weather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 此活动用于遍历全国的省市县数据
 * @author Administrator
 *
 */
public class ChooseAreaActivity extends Activity {
	/**
	 * 定义三个常量,分别代表当前显示级别
	 */
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	/**
	 * 定义各类控件及数据库对象,适配器对象,数据源对象等
	 */
	private ProgressDialog progressDialog;
	private TextView textView;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private WeatherDB weatherDB;
	private List<String> dataList = new ArrayList<String>();
	/**
	 * 省列表
	 */
	private List<Province> provinceList;
	/**
	 * 市列表
	 */
	private List<City> cityList;
	/**
	 * 县列表
	 */
	private List<County> countyList;
	/**
	 * 选中的省份
	 */
	private Province selectProvince;
	/**
	 * 选中的城市
	 */
	private City selectCity;
	/**
	 * 当前选中的级别
	 */
	private int currentLevel;
	/**
	 * oncreaty时加载省级数据,对list_item进行监听
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		textView = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		//数据库实例化
		weatherDB = WeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if(currentLevel == LEVEL_PROVINCE){
					selectProvince = provinceList.get(index);
					queryCities();
				}else if(currentLevel == LEVEL_CITY){
					selectCity = cityList.get(index);
					queryCounties();
				}else if(currentLevel == LEVEL_COUNTY){
					Toast.makeText(ChooseAreaActivity.this, "hha", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		queryProvinces();
	}
	/**
	 * 查询省级数据,优先从数据库查询,如果没有再到服务器查询
	 */
	private void queryProvinces(){
		provinceList = weatherDB.loadProvince();
		if(provinceList.size() >0){
			dataList.clear();
			for(Province p : provinceList){
				dataList.add(p.getProvinceName());
			}
			//更新ListView
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText("中国");
			currentLevel = LEVEL_PROVINCE;			
		}else{
			queryFromServer(null, "province");
		}
	}
	/**
	 * 查询省内所有市的数据,优先从数据库查询,如果没有再到服务器查询
	 */
	private void queryCities(){
		cityList = weatherDB.loadCity(selectProvince.getId());
		if(cityList.size() >0){
			dataList.clear();
			for(City c : cityList){
				dataList.add(c.getCityName());
			}
			//更新ListView
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectProvince.getProvinceName());
			currentLevel = LEVEL_CITY;			
		}else{
			queryFromServer(selectProvince.getProvinceCode(), "city");
		}
	}
	/**
	 * 查询市内所有县的数据,优先从数据库查询,如果没有再到服务器查询
	 */
	private void queryCounties(){
		countyList = weatherDB.loadCounty(selectCity.getId());
		if(countyList.size() >0){
			dataList.clear();
			for(County c : countyList){
				dataList.add(c.getCountyName());
			}
			//更新ListView
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectCity.getCityName());
			currentLevel = LEVEL_COUNTY;			
		}else{
			queryFromServer(selectCity.getCityCode(), "county");
		}
	}
	/**
	 * 根据传入的代号和类型从服务器上查询相应的省,市,县的数据
	 */
	private void queryFromServer(final String code,final String type){
		String address;
		//根据传入代码,拼接URL地址
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		//显示进度对话框
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.show();
		}
		progressDialog.show();
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			/**
			 * 网络操作完成后,回调方法根据传入类型type 分别处理数据
			 */
			@Override
			public void onFinish(String result) {
				boolean res = false;
				if("province".equals(type)){
					res = Utility.handleProvincesResponse(weatherDB, result);
				}else if("city".equals(type)){
					res = Utility.handleCitiesResponse(weatherDB, result, selectProvince.getId());					
				}else if("county".equals(type)){
					res = Utility.handleCountiesResponse(weatherDB, result, selectCity.getId());
				}
				if(res){
					//通过runOnUiThread方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {						
						@Override
						public void run() {
							//关闭进度对话框
							if(progressDialog != null){
								progressDialog.dismiss();
							}
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();				
							}else if("county".equals(type)){
								queryCounties();
							}
						}
					});
				}
				
			}
			/**
			 * 网络操作出现错误时,回调方法显示加载失败
			 */
			@Override
			public void onError(Exception e) {
				//通过runOnUiThread方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//关闭进度对话框
						if(progressDialog != null){
							progressDialog.dismiss();
						}
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}					
				});
			}
		});
	}
	/**
	 * 捕获back按键,根据当前级别来判断此时应该返回到哪个列表或是直接退出
	 */
	@Override
	public void onBackPressed() {
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			finish();
		}
	}
}
