package com.weather.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Message;
/**
 * 此类提供HTTP请求的网络操作
 * @author Administrator
 *
 */
public class HttpUtil {
	/**
	 * 此静态方法提供一个GET请求,连接超时8秒,读取超时8秒,使用的是HttpURLConnection方式.
	 * 通过回调函数返回请求数据,有两个参数,网络地址和回调接口
	 * 注意:回调方法处于子线程当中,不能进行更新UI的操作
	 * @param address:请求的网络地址
	 * @param listener:需要实现的监听接口
	 */
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener) {
		new Thread(new Runnable() {					
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream is = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					StringBuilder result = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null){
						result.append(line);
					}						
					is.close();
					reader.close();
					//回调onFinish方法
					if(listener != null){
						listener.onFinish(result.toString());						
					}
				} catch (Exception e) {
					//回调onError方法
					if(listener != null){
						listener.onError(e);
					}
				} finally {
					if(connection != null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
