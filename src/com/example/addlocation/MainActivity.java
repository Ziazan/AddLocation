package com.example.addlocation;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.util.HttpConnectionUtils;
import com.example.util.HttpHandler;

public class MainActivity extends Activity implements OnClickListener {
	public static final String URL = "http://192.168.253.14/callCar/driver/driverSignin.php";
	String driverjson="";
	public EditText edt_name, edt_identity_number, edt_plate_number,
			edt_phone_number, edt_http_imform,edt_loc_imform;
	public Button btn_show, btn_submit,btn_insert_driver;
	private Toast mToast;
	// 定位相关
	public LocationClient mLocClient = null;
	public MyLocationListener myListener = new MyLocationListener();
	boolean isFirstLoc = true;// 是否首次定位
	private Context mContext;
	private JSONObject loc_json;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext=this;
		edt_name = (EditText) findViewById(R.id.edt_name);
		edt_identity_number = (EditText) findViewById(R.id.edt_identity_number);
		edt_plate_number = (EditText) findViewById(R.id.edt_plate_number);
		edt_phone_number = (EditText) findViewById(R.id.edt_phone_number);
		edt_http_imform = (EditText) findViewById(R.id.edt_http_imform);
		edt_loc_imform = (EditText) findViewById(R.id.edt_loc_imform);
		btn_show = (Button) findViewById(R.id.btn_show);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_insert_driver=(Button) findViewById(R.id.btn_insert_driver);
		
		//模拟
		 edt_name.setText("zhangsan");
		 edt_identity_number.setText("121212121212121212");
		 edt_plate_number.setText("EA 66666");
		 edt_phone_number.setText("66666666666");

		// 定位初始化
		mLocClient = new LocationClient(getApplicationContext()); // 声明LocationClient
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		

		btn_show.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
		btn_insert_driver.setOnClickListener(this);
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			loc_json=new JSONObject();
			try {
				loc_json.put("time",location.getTime());
				loc_json.put("error_code",location.getLocType());
				loc_json.put("latitude",location.getLatitude());
				loc_json.put("lontitude",location.getLongitude());
				loc_json.put("phoneNumber",edt_phone_number.getText());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			edt_loc_imform.setText(loc_json.toString());
		}
	}
   
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_insert_driver:
			new Thread(runnable).start();
			break;
		case R.id.btn_show:
			// 获得定位数据
			mLocClient.start();
			break;
		case R.id.btn_submit:
			new Thread(runnable2).start();
			break;
		default:
			break;
		}
	} 
		private Handler handler = new HttpHandler(this) {
		    @Override  
		    protected void succeed(JSONObject jObject) { //自己处理成功后的操作  
		        super.succeed(jObject);		        
		    } //也可以在这重写start() failed()方法  
		    
		}; 
		Runnable runnable=new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
					driverSignin();
			}
		};
       Runnable runnable2=new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub				
				while(true){
					mLocClient.start();//开始定位
					try {
						Thread.sleep(1000);
						addPoint();
						Thread.sleep(10000*6);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}					
				}
			}
		};
		private void driverSignin() {  
			 String name="";
			 String identity_number="";
			 String plate_number="";
			 String phone_number="";
			 name =edt_name.getText().toString();
			identity_number =edt_identity_number.getText().toString();
			plate_number =edt_plate_number.getText().toString();
			phone_number =edt_phone_number.getText().toString();
		    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();  
		    params.add(new BasicNameValuePair("driverName",name));  
		    params.add(new BasicNameValuePair("idNumber", identity_number));
		    params.add(new BasicNameValuePair("plateNumber", plate_number));
		    params.add(new BasicNameValuePair("phoneNumber",phone_number));
		    String urlString ="http://192.168.253.3:8080/callCar/driver/driverSignin.php";   
		    new HttpConnectionUtils(handler).post(urlString, params);
		}
		private void addPoint(){
			 ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();  
			    try {
					  params.add(new BasicNameValuePair("phoneNmuber",loc_json.getString("phoneNumber")));
					   params.add(new BasicNameValuePair("time",loc_json.getString("time")));  
					    params.add(new BasicNameValuePair("error_code", loc_json.getString("error_code")));
					    params.add(new BasicNameValuePair("latitude", loc_json.getString("latitude")));
					    params.add(new BasicNameValuePair("lontitude",loc_json.getString("lontitude")));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    String urlString ="http://192.168.253.3:8080/callCar/driver/insertLocation.php";   
			    new HttpConnectionUtils(handler).post(urlString, params);
		}
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * 显示通知
	 * 
	 * @param msg
	 */
	private void showToast(String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
		}
		mToast.setText(msg);
		mToast.show();
	}

}
