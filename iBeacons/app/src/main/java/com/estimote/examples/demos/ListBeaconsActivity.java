package com.estimote.examples.demos;

import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.estimote.sdk.utils.L;

/**
 * Displays list of found beacons sorted by RSSI. Starts new activity with
 * selected beacon if activity was provided.
 *
 * @author wiktorgworek@google.com (Wiktor Gworek)
 */
public class ListBeaconsActivity extends Activity implements OnClickListener {

	private static final String TAG = ListBeaconsActivity.class.getSimpleName();

	public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
	public static final String EXTRAS_BEACON = "extrasBeacon";

	private static final int REQUEST_ENABLE_BT = 1234;
	private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid",
			null, null, null);

	private BeaconManager beaconManager;
	private LeDeviceListAdapter adapter;

	// 校正常數c，累計值n
	private static double c = 1.00;
	private static double n = 0.05;

	// UI
	Button start;
	Button stop;
	Button upload;
	Button c_increase;
	Button c_reduce;
	Button show_log;
	TextView time;
	TextView c_value;
	TextView state;
	TextView all_count_tx;
	TextView pos_count_tx;
	TextView rssi_avg;
	EditText position;

	// 上傳參數
	String Create_LogNoTable_Url = "http://www.demonickrace.byethost7.com/ibeacon/create_table_beta.php";
	String InsertInto_LogNoTable_Url = "http://www.demonickrace.byethost7.com/ibeacon/ibeacon_data_process.php";
	String Msg_LogNo;
	final String key1 = "1234";
	final String key2 = "1234";

	// ibeacon資料暫存
	String log_str = "";
	String all_log = "";

	// [座標所記錄的資料個數]的訊息
	String count_info = "";
	String count_info_x = "";

	// Msg_LogNo表單的欄位
	String table_colum = "(Time,Position,MAC,Major,Minor,Rssi,TxPower,Distance,Proximity,Vector,NorthDegree,Algorithm) VALUES ";

	SimpleDateFormat sdf;// SimpleDateFormat("yyyy-MM-dd hh:mm:ss.ssssss");
	SimpleDateFormat log_t;// SimpleDateFormat("hh:mm:ss.ssssss");

	// 其他參數
	boolean isFirstInput = true;
	boolean isLoading = false;
	boolean isFisrtChange = true;
	int all_count = 0; // 全部紀錄筆數
	int pos_count = 0; // 座標紀錄筆數
//	double sum = 0; //sum of pos' of rssi
	String tmp_pos_str = "0";// 現在設定的座標
	String now_pos_str = "0";// 紀錄時的座標

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// 時間t格式化
		sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		log_t = new SimpleDateFormat("HH:mm:ss");

		// Configure device list.
		adapter = new LeDeviceListAdapter(this);
		ListView list = (ListView) findViewById(R.id.device_list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(createOnItemClickListener());

		// 設定UI
		start = (Button) findViewById(R.id.StartLog);
		stop = (Button) findViewById(R.id.StopLog);
		upload = (Button) findViewById(R.id.UploadLog);
		c_increase = (Button) findViewById(R.id.c_increase);
		c_reduce = (Button) findViewById(R.id.c_reduce);
		show_log = (Button) findViewById(R.id.show_log);
		time = (TextView) findViewById(R.id.time);
		c_value = (TextView) findViewById(R.id.c_value);
		position = (EditText) findViewById(R.id.position);

		rssi_avg = (TextView) findViewById(R.id.c_tag);

		state = (TextView) findViewById(R.id.state);

		all_count_tx = (TextView) findViewById(R.id.all_count);
		pos_count_tx = (TextView) findViewById(R.id.pos_count);

		start.setOnClickListener(this);
		stop.setOnClickListener(this);
		upload.setOnClickListener(this);
		c_increase.setOnClickListener(this);
		c_reduce.setOnClickListener(this);
		show_log.setOnClickListener(this);
		// 更新時間
		final Handler mHandler = new Handler();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHandler.postDelayed(this, 500);
				Date date = new Date();
				SimpleDateFormat t = new SimpleDateFormat(
						"yyyy/MM/dd  HH:mm:ss");
				time.setText(t.format(date));

				//rssi_avg.setText(String.valueOf(sum/pos_count));

				// 設置的座標
				if (position.getText().toString().isEmpty())
					tmp_pos_str = "0";
				else
					tmp_pos_str = position.getText().toString();
				// time.setText("tmp_pos_str = "+tmp_pos_str+"\nnow_pos_str = "+now_pos_str);
			}
		};
		mHandler.postDelayed(runnable, 500);

		// Configure verbose debug logging.
		L.enableDebugLogging(true);

		// Configure BeaconManager.
		beaconManager = new BeaconManager(this);
		beaconManager.setRangingListener(new BeaconManager.RangingListener() {
			@Override
			public void onBeaconsDiscovered(Region region,
					final List<Beacon> beacons) {
				// Note that results are not delivered on UI thread.
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// 紀錄資料
						if (isLoading) {
							all_count++; // 每收到一次廣播就+1
							pos_count++;

							String t = sdf.format(new Date()); // 時間
							String s = log_t.format(new Date());
							long now = System.currentTimeMillis(); // 現在微秒時間

							// 檢查座標是否變更
							if (!(now_pos_str.equals(tmp_pos_str))) {
								count_info += ("position = " + now_pos_str
										+ " , log_count = " + pos_count + "\n");
								if (isFisrtChange) {
									count_info = "";
									isFisrtChange = false;
								}
								pos_count = 0; // 座標變更，重置pos_count
								now_pos_str = tmp_pos_str;// 更新紀錄時的座標
							}
							pos_count_tx.setText(String.valueOf(pos_count + 1));
							all_count_tx.setText(String.valueOf(all_count));
							for (Beacon b : beacons) {
								// String t = sdf.format(new Date()); //取得時間
								now++;
								Calendar cal = Calendar.getInstance();
								cal.setTimeInMillis(now);
								String us = String.valueOf(cal
										.get(Calendar.MILLISECOND)); // 微秒

								// polyfit
								/*
								 * int rssi = b.getRssi(); double a =
								 * 0.1446792178409801; double b1 =
								 * -3.553438914027098; double c =
								 * -50.4276373626382 - rssi; double d =
								 * b1*b1-4*a*c; double distance = 0; double
								 * distance1 = 0; double distance2 = 0; if(d <
								 * 0){ distance = (-b1)/(2*a); }else{ distance1
								 * = (-b1 + Math.sqrt(b1*b1-4*a*c) )/(2*a);
								 * distance2 = (-b1 - Math.sqrt(b1*b1-4*a*c)
								 * )/(2*a); distance = (distance1 <
								 * distance2)?distance1 :distance2; }
								 */
								double distance = Utils.computeAccuracy(b) * c;
								// *


								//sum += b.getRssi();

								// show_log的資料
								String tmp = s + "." + us + " " + "座標:"
										+ now_pos_str + " " + "Major="
										+ b.getMajor() + " " + "Minor="
										+ b.getMinor() + " " + "rssi="
										+ b.getRssi() + " " + "距離:" + distance
										+ "m\n"; // 保留Utils.computeAccuracy(b) *
													// c
								String temp = String.valueOf(pos_count) +  ". 座標:" + now_pos_str + "   Major =" + b.getMajor() + "   rssi = " + b.getRssi() + "\n";
								all_log += temp;

								// upload的資料
								String log = "(" + "'" + t + "." + us + "'," // Time
																				// CHAR(30)
																				// ,PRIMARY
																				// KEY(Time)

										+ "'" + now_pos_str + "'," // Position
																	// CHAR(20)
																	// NOT NULL,

										+ "'" + b.getMacAddress() + "'," // MAC
																			// CHAR(20)
																			// NOT
																			// NULL,

										+ "'" + b.getMajor() + "'," // Major
																	// INT(10)
																	// NOT NULL,

										+ "'" + b.getMinor() + "'," // Minor
																	// INT(10)
																	// NOT NULL,

										+ "'" + b.getRssi() + "'," // Rssi
																	// INT(10)
																	// NOT NULL,

										+ "'" + b.getMeasuredPower() + "'," // TxPower
																			// INT(10)
																			// NOT
																			// NULL,

										+ "'" + distance // 保留Utils.computeAccuracy(b)
															// * c
										+ "'," // Distance CHAR(10) NOT NULL,

										+ "NULL" + "," // Proximity CHAR(10) ,

										+ "NULL" + "," // Vector CHAR(10) ,

										+ "NULL" + "," // NorthDegree CHAR(20) ,

										+ "NULL" // Algorithm CHAR(10) ,

										+ ")";
								;
								if (!isFirstInput)
									log_str += ("," + log);
								else {
									log_str += log;
									isFirstInput = false;
								}
								Log.e("log_msg = ", log);
							}
							all_log += "\n";

						}
						// Note that beacons reported here are already
						// sorted by
						// estimated
						// distance between device and beacon.
						getActionBar().setSubtitle(
								"Found beacons: " + beacons.size());
						adapter.replaceWith(beacons, c);

					}
				});
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.show_log:
			if (!isLoading && !all_log.isEmpty()) {
				Intent intent = new Intent(ListBeaconsActivity.this,
						Show_log.class);
				intent.putExtra("pos_log", count_info); // 座標，資料計數
				intent.putExtra("all_log", all_log); // 總資料
				startActivity(intent);
			}
			break;
		case R.id.StartLog:
			if (tmp_pos_str.equals("0"))
				Toast.makeText(this, "position不能為0...", Toast.LENGTH_SHORT)
						.show();
			else if (!isLoading) {
				// process...
				Toast.makeText(this, "開始紀錄資料...", Toast.LENGTH_SHORT).show();
				isLoading = true; // 開始紀錄資料
				position.setEnabled(false);// 開始紀錄時，鎖定座標
			}
			break;
		case R.id.StopLog:
			if (isLoading) {
				// process...
				Toast.makeText(this, "停止紀錄資料...", Toast.LENGTH_SHORT).show();
				isLoading = false; // 停止紀錄資料
				position.setEnabled(true);
				; // 開始紀錄時，解鎖座標
			}
			break;
		case R.id.UploadLog:
			if (isLoading)
				Toast.makeText(this, "檔案紀錄中...請先停止紀錄..", Toast.LENGTH_SHORT)
						.show();
			else if (isFirstInput)
				Toast.makeText(this, "無紀錄資料可上傳...請先開始紀錄..", Toast.LENGTH_SHORT)
						.show();

			// 當log_str存有一筆以上的資料狀態且要為停止紀錄才允許上傳
			if ((!isLoading) && (!isFirstInput)) {
				// Log.e("upload", "uploading");
				Toast.makeText(this, "上傳資料中...", Toast.LENGTH_SHORT).show();
				final Handler mHandler = new Handler();
				// process..
				new Thread(new Runnable() {
					public void run() {
						// Log.e("Runnable", "run");
						final String msg = sendhttppost();
						Log.e("sendhttppost() echo = ", msg);
						// call mHandler
						mHandler.post(new Runnable() {
							public void run() {
								if (msg == "true") {
									Toast.makeText(ListBeaconsActivity.this,
											"上傳成功...", Toast.LENGTH_LONG)
											.show();
									state.setText("紀錄資料新增至:" + Msg_LogNo);
								} else if (msg == "false") {
									Toast.makeText(ListBeaconsActivity.this,
											"上傳失敗...", Toast.LENGTH_LONG)
											.show();
									state.setText("ID驗證錯誤...上傳失敗...");
								} else if (msg == "HttpError") {
									Toast.makeText(ListBeaconsActivity.this,
											"上傳失敗...", Toast.LENGTH_LONG)
											.show();
									state.setText("HttpError... 上傳失敗...");
								} else {
									Toast.makeText(ListBeaconsActivity.this,
											"上傳失敗...", Toast.LENGTH_LONG)
											.show();
									state.setText("無網路或未知原因... 上傳失敗...");
								}
							}
						});
					}
				}).start();
			}

			break;
		case R.id.c_increase:
			if (this.c < 2.0) {
				this.c += this.n;
				c_value.setText(String.format("%.02f", this.c));
			}
			break;
		case R.id.c_reduce:
			if (this.c > 0.1) {
				this.c -= this.n;
				c_value.setText(String.format("%.02f", this.c));
			}
			break;
		default:
			// ...
			break;
		}
	}

	private String sendhttppost() {

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Create_LogNoTable_Url);

		// Log.e("sendhttppost()", "sendhttppost");

		try {
			// LogDate紀錄時間
			String LogDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
					.format(new Date());

			// Add your data
			List<NameValuePair> parmas = new ArrayList<NameValuePair>(2);
			// 逐一增加POST所需的Key、值
			parmas.add(new BasicNameValuePair("hashkey", key1));
			parmas.add(new BasicNameValuePair("Id", "krace"));
			parmas.add(new BasicNameValuePair("Pw", "1234"));
			parmas.add(new BasicNameValuePair("Enviroment", "MCU大學S棟6樓"));
			parmas.add(new BasicNameValuePair("Company", "ASUS"));
			parmas.add(new BasicNameValuePair("PhoneType", "ZonFone5"));
			parmas.add(new BasicNameValuePair("OsVersion", "Andriod 4.3"));
			parmas.add(new BasicNameValuePair("LogDate", LogDate));
			parmas.add(new BasicNameValuePair("ProgramId", "a"));
			httppost.setEntity(new UrlEncodedFormEntity(parmas, HTTP.UTF_8));
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			Log.e("HttpResponse response", "HttpResponse response");
			if (response.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(response.getEntity(),
						HTTP.UTF_8);
				strResult = Normalizer.normalize(strResult, Form.NFD);
				strResult = strResult.replaceAll("[^A-Za-z0-9_]", "");
				Log.e("sendhttppost_strResult=", strResult);

				Msg_LogNo = strResult;
				// 插入資料至Id_LogNo_Table
				String msg = inserthttppost();
				return msg;
			} else {
				return "HttpError";
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.e("ClientProtocolException", e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IOException", e.toString());
		}
		return "upload excute error";
	}

	private String inserthttppost() {

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(InsertInto_LogNoTable_Url);

		Log.e("inserthttppost()", "inserthttppost");

		try {
			// Add your data
			List<NameValuePair> parmas = new ArrayList<NameValuePair>(2);
			// 逐一增加POST所需的Key、值
			parmas.add(new BasicNameValuePair("hashkey", key2));

			Log.e("Msg_LogNo = ", Msg_LogNo);

			// sql_guery
			String insert_query = "INSERT INTO " + Msg_LogNo + table_colum
					+ log_str;
			Log.e("insert_query = ", insert_query);
			parmas.add(new BasicNameValuePair("ibeacon_data", insert_query));
			httppost.setEntity(new UrlEncodedFormEntity(parmas, HTTP.UTF_8));
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(response.getEntity(),
						HTTP.UTF_8);
				strResult = Normalizer.normalize(strResult, Form.NFD);
				strResult = strResult.replaceAll("[^A-Za-z0-9_]", "");

				// 重置ibeacon資料暫存
				log_str = "";
				all_log = "";
				// 重置為第一筆資料"尚未"插入的狀態
				isFirstInput = true;
				// 重置 [座標所記錄的資料個數]的訊息
				count_info = "";
				all_count = 0; // 重置計數
				pos_count = 0;

				Log.e("sendhttppost_strResult=", strResult);

				return "true";
			} else {
				return "false";
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.scan_menu, menu);
		MenuItem refreshItem = menu.findItem(R.id.refresh);
		refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		beaconManager.disconnect();

		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Check if device supports Bluetooth Low Energy.
		if (!beaconManager.hasBluetooth()) {
			Toast.makeText(this, "Device does not have Bluetooth Low Energy",
					Toast.LENGTH_LONG).show();
			return;
		}

		// If Bluetooth is not enabled, let user enable it.
		if (!beaconManager.isBluetoothEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			connectToService();
		}
	}

	@Override
	protected void onStop() {
		try {
			beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
		} catch (RemoteException e) {
			Log.d(TAG, "Error while stopping ranging", e);
		}

		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				connectToService();
			} else {
				Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG)
						.show();
				getActionBar().setSubtitle("Bluetooth not enabled");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void connectToService() {
		getActionBar().setSubtitle("Scanning...");
		adapter.replaceWith(Collections.<Beacon> emptyList(), c);
		beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
			@Override
			public void onServiceReady() {
				try {
					beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
				} catch (RemoteException e) {
					Toast.makeText(
							ListBeaconsActivity.this,
							"Cannot start ranging, something terrible happened",
							Toast.LENGTH_LONG).show();
					Log.e(TAG, "Cannot start ranging", e);
				}
			}
		});
	}

	private AdapterView.OnItemClickListener createOnItemClickListener() {
		return new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY) != null) {
					try {
						Class<?> clazz = Class.forName(getIntent()
								.getStringExtra(EXTRAS_TARGET_ACTIVITY));
						Intent intent = new Intent(ListBeaconsActivity.this,
								clazz);
						intent.putExtra(EXTRAS_BEACON,
								adapter.getItem(position));
						startActivity(intent);
					} catch (ClassNotFoundException e) {
						Log.e(TAG, "Finding class by name failed", e);
					}
				}
			}
		};
	}

}
