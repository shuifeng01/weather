package com.weather.app.util;
/**
 * �ṩonfinish�ʹ���Ļص�����
 * @author Administrator
 *
 */
public interface HttpCallbackListener {
	void onFinish(String result);
	void onError(Exception e);
}
