package com.weather.app.util;
/**
 * 提供onfinish和错误的回调方法
 * @author Administrator
 *
 */
public interface HttpCallbackListener {
	void onFinish(String result);
	void onError(Exception e);
}
