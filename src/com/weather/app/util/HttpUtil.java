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
 * �����ṩHTTP������������
 * @author Administrator
 *
 */
public class HttpUtil {
	/**
	 * �˾�̬�����ṩһ��GET����,���ӳ�ʱ8��,��ȡ��ʱ8��,ʹ�õ���HttpURLConnection��ʽ.
	 * ͨ���ص�����������������,����������,�����ַ�ͻص��ӿ�
	 * ע��:�ص������������̵߳���,���ܽ��и���UI�Ĳ���
	 * @param address:����������ַ
	 * @param listener:��Ҫʵ�ֵļ����ӿ�
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
					//�ص�onFinish����
					if(listener != null){
						listener.onFinish(result.toString());						
					}
				} catch (Exception e) {
					//�ص�onError����
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
