package com.example.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class HttpHandler extends Handler {

	private Context context;
	private ProgressDialog progressDialog;

	public HttpHandler(Context context) {
		this.context = context;
	}

	protected void start() {
		progressDialog = ProgressDialog.show(context,
				"Please Wait...", "processing...", true);
	}

	protected void succeed(JSONObject jObject) {
		if(progressDialog!=null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}

	protected void failed(JSONObject jObject) {
		if(progressDialog!=null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}
	
	protected void otherHandleMessage(Message message){
		//数据在这里处理
	}
	
	public void handleMessage(Message message) {
		switch (message.what) {
		case HttpConnectionUtils.DID_START: //connection start
			Log.d(context.getClass().getSimpleName(),
					"http connection start...");
			start();
			break;
		case HttpConnectionUtils.DID_SUCCEED: //connection success
			progressDialog.dismiss();
			String response = (String) message.obj;
			Log.d(context.getClass().getSimpleName(), "http connection return."
					+ response);
			try {
//				JSONObject jObject = new JSONObject(response == null ? ""
//						: response.trim());	
				JSONObject jObject = new JSONObject(response);
				if ("true".equals(jObject.getString("success"))) { //operate success
					Toast.makeText(context, "operate succeed:"+jObject.getString("message"),Toast.LENGTH_SHORT).show();
					succeed(jObject);
				} else {
					Toast.makeText(context, "operate fialed:"+jObject.getString("message"),Toast.LENGTH_LONG).show();
					failed(jObject);
				}
			} catch (JSONException e1) {
				if(progressDialog!=null && progressDialog.isShowing()){
					progressDialog.dismiss();
				}
				e1.printStackTrace();
				Toast.makeText(context, "Response data is not json data",
						Toast.LENGTH_LONG).show();
			}
			break;
		case HttpConnectionUtils.DID_ERROR: //connection error
			if(progressDialog!=null && progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			Exception e = (Exception) message.obj;
			e.printStackTrace();
			Log.e(context.getClass().getSimpleName(), "connection fail."
					+ e.getMessage());
			Toast.makeText(context, "connection fail,please check connection!",
					Toast.LENGTH_LONG).show();
			break;
		}
		otherHandleMessage(message);
	}

}
