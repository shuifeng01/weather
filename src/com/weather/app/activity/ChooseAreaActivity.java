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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * �˻���ڱ���ȫ����ʡ��������
 * @author Administrator
 *
 */
public class ChooseAreaActivity extends Activity {
	/**
	 * ������������,�ֱ����ǰ��ʾ����
	 */
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	/**
	 * �������ؼ������ݿ����,����������,����Դ�����
	 */
	private ProgressDialog progressDialog;
	private TextView textView;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private WeatherDB weatherDB;
	private List<String> dataList = new ArrayList<String>();
	/**
	 * ʡ�б�
	 */
	private List<Province> provinceList;
	/**
	 * ���б�
	 */
	private List<City> cityList;
	/**
	 * ���б�
	 */
	private List<County> countyList;
	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectProvince;
	/**
	 * ѡ�еĳ���
	 */
	private City selectCity;
	/**
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel;
	/**
	 * oncreatyʱ����ʡ������,��list_item���м���
	 */
	private boolean isFromWeatherActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//�ж��Ƿ���ת��weather_activity
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selected", false) && !isFromWeatherActivity){
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		textView = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		//���ݿ�ʵ����
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
					String countyCode = countyList.get(index).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
				
			}
		});
		queryProvinces();
	}
	/**
	 * ��ѯʡ������,���ȴ����ݿ��ѯ,���û���ٵ���������ѯ
	 */
	private void queryProvinces(){
		provinceList = weatherDB.loadProvince();
		if(provinceList.size() >0){
			dataList.clear();
			for(Province p : provinceList){
				dataList.add(p.getProvinceName());
			}
			//����ListView
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText("�й�");
			currentLevel = LEVEL_PROVINCE;			
		}else{
			queryFromServer(null, "province");
		}
	}
	/**
	 * ��ѯʡ�������е�����,���ȴ����ݿ��ѯ,���û���ٵ���������ѯ
	 */
	private void queryCities(){
		cityList = weatherDB.loadCity(selectProvince.getId());
		if(cityList.size() >0){
			dataList.clear();
			for(City c : cityList){
				dataList.add(c.getCityName());
			}
			//����ListView
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectProvince.getProvinceName());
			currentLevel = LEVEL_CITY;			
		}else{
			queryFromServer(selectProvince.getProvinceCode(), "city");
		}
	}
	/**
	 * ��ѯ���������ص�����,���ȴ����ݿ��ѯ,���û���ٵ���������ѯ
	 */
	private void queryCounties(){
		countyList = weatherDB.loadCounty(selectCity.getId());
		if(countyList.size() >0){
			dataList.clear();
			for(County c : countyList){
				dataList.add(c.getCountyName());
			}
			//����ListView
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectCity.getCityName());
			currentLevel = LEVEL_COUNTY;			
		}else{
			queryFromServer(selectCity.getCityCode(), "county");
		}
	}
	/**
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯ��Ӧ��ʡ,��,�ص�����
	 */
	private void queryFromServer(final String code,final String type){
		String address;
		//���ݴ������,ƴ��URL��ַ
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		//��ʾ���ȶԻ���
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.show();
		}
		progressDialog.show();
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			/**
			 * ���������ɺ�,�ص��������ݴ�������type �ֱ�������
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
					//ͨ��runOnUiThread�����ص����̴߳����߼�
					runOnUiThread(new Runnable() {						
						@Override
						public void run() {
							//�رս��ȶԻ���
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
			 * ����������ִ���ʱ,�ص�������ʾ����ʧ��
			 */
			@Override
			public void onError(Exception e) {
				//ͨ��runOnUiThread�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//�رս��ȶԻ���
						if(progressDialog != null){
							progressDialog.dismiss();
						}
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					}					
				});
			}
		});
	}
	/**
	 * ����back����,���ݵ�ǰ�������жϴ�ʱӦ�÷��ص��ĸ��б����ֱ���˳�
	 */
	@Override
	public void onBackPressed() {
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			if(isFromWeatherActivity){
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
}
