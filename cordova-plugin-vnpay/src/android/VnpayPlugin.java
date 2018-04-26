package com.apvisa.cordova.vnpay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class VnpayPlugin extends CordovaPlugin {
	private static String TAG = "VnPayPlugin";
	
	@Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
		LOG.d(TAG, "VnPayPlugin initialize");
    }
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext){
		if ("pay".equals(action)) {
			// try {
				// JSONObject arguments = args.getJSONObject(0);
	            // // String tradeNo = arguments.getString("tradeNo");
	            // // String subject = arguments.getString("subject");
	            // // String body = arguments.getString("body");
	            // // String price = arguments.getString("price");
	            // // String notifyUrl = arguments.getString("notifyUrl");
				// LOG.d(TAG, "execute pay");
	            // this.pay(callbackContext);
			// } catch(JSONException e) {
				// callbackContext.error(new JSONObject());
				// e.printStackTrace();
				// return false;
			// }
			this.pay(callbackContext);
			LOG.d(TAG, "execute pay");
			return true;
		}
		else{
			callbackContext.error("no such method:" + action);
			return false;
		}
	}
	
	public void pay(final CallbackContext callbackContext){
		LOG.d(TAG, "getThreadPool pay");
		this.cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
            	Intent intent = new Intent(cordova.getActivity().getApplicationContext(), VnpayActivity.class);
				intent.putExtra("cp_serial", getTradeLongId());
				LOG.d(TAG, "startActivity pay");
				cordova.getActivity().startActivity(intent);
            }
		});
//		Intent intent = new Intent(this.cordova.getActivity().getApplicationContext(), VnpayActivity.class);
//		intent.putExtra("cp_serial", getTradeLongId());
//		LOG.d(TAG, "startActivity pay");
//		this.cordova.getActivity().startActivity(intent);
	}
	
	public String getTradeLongId() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + getRandomNumber(5);
	}
	
	/**
	 * 获得随机数（纯数字）
	 * 
	 * @param num
	 * @return
	 */
	public String getRandomNumber(int num) {
		int codeCount = num;
		String str = "";
		char[] codeSequence = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		Random random = new Random();
		for (int i = 0; i < codeCount; i++) {
			str = str + codeSequence[random.nextInt(10)];
		}
		return str;
	}
	
}
