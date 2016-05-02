package com.estimote.examples.demos;

import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.text.Normalizer.Form;
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

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShopActivity extends Activity {
	private static final String TAG = ListBeaconsActivity.class.getSimpleName();

	public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
	public static final String EXTRAS_BEACON = "extrasBeacon";

	private static final int REQUEST_ENABLE_BT = 1234;
	private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
	private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);


	private BeaconManager beaconManager;
	//private LeDeviceListAdapter adapter;

	//my code
	private ImageButton img1;
	private ImageButton img2;
	private ImageButton img3;
	private ImageButton img4;
	private ImageButton img5;
	private ImageButton img6;
	private ImageButton img7;
	private ImageButton img8;
	private ImageButton img9;
	private ImageButton img10;
	private ImageButton img11;
	private ImageButton img12;

	private Button bt_log;
	private Button bt_clear;
	private Button bt_calibrated_value_up;
	private Button bt_calibrated_value_down;
	private Button bt_deviation_value_up;
	private Button bt_deviation_value_down;
	private Button bt_rssi_filter_value_up;
	private Button bt_rssi_filter_value_down;
	private Button bt_reset;
	private Button bt_start_log;
	private Button bt_stop_log;

	private TextView textview_tp;
	private TextView textview_calibrated;
	private TextView textview_deviation;
	private TextView textview_filter;
	private TextView textview_log_count;
	private TextView textview_closest_beacon;


	//define
	private static final boolean DEBUG_MODE = true;
	private static final boolean SHOW_DETAIL_DATA = false;
	private static final int BEACON_COUNT = 8;
	private static final int TRAINING_PLACE_COUNT = 12;
	private static final double MIN_RSSI = -90.0;
	private static final double RSSI_FILTER_MAX_VALUE = -60.0;//-90.0 -85.0 -80.0
	private static final double RSSI_FILTER_MIN_VALUE = -80.0;//
	private static final double CALIBRATED_VALUE = 0.0;
	private static final double DEVIATION_VALUE = 20.0;
	private static final double PER_VALUE = 0.5;

	int beacon_count = BEACON_COUNT;
	int training_palce_count = TRAINING_PLACE_COUNT;
	int theClosestTp = 0;//the most closest training_palce's index
	int theCloseedBeacon = 0;//the closest Beacon's major
	int calibrated_value_of_filterMinValue = 5;
	double min_rssi = MIN_RSSI;
	double rssi_filter_max_value = RSSI_FILTER_MAX_VALUE; //rssi filter value of min
	double rssi_filter_min_value = RSSI_FILTER_MIN_VALUE; //rssi filter value of min
	double calibrated_value = CALIBRATED_VALUE; //get from experience to correct rssi_table
	double deviation_value = DEVIATION_VALUE; //Multipath Fading & Body Shadowing
	double per_value = PER_VALUE; //increase or decrease other value
    /* table01
	double rssi_table[][] = //[beacon_count][training_palce_count] , offline data
				           {{-77.118 ,-74.322 ,-85.408 ,-89.4868,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,-89.6316,-89.6316,-84.447 },
							{-82.762 ,-76.27  ,-72.351 ,-75.146 ,-77.711 ,-78.645 ,-86.8158,min_rssi,min_rssi,min_rssi,min_rssi,-88.5921},
							{-89.8421,-89.386 ,-77.487 ,-70.214	,-67.579 ,-78.554 ,-87.5132,min_rssi,min_rssi,-89.6579,min_rssi,min_rssi},
							{min_rssi,min_rssi,-89.4868,-85.999	,-79.013 ,-73.236 ,-70.658 ,-84.804 ,-88.1711,min_rssi,min_rssi,min_rssi},
							{min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,-87.5526,-80.5   ,-77.562	,-86.9079,-89.9444,min_rssi,min_rssi},
							{min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,-85.9474,-77.684 ,-73.066	,-76.544 ,-82.813 ,-82.135 ,-86.1053},
							{min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,-89.5526,min_rssi,-89.7895,-78.213 ,-72.333 ,-70.789 ,-80.618 },
							{-75.803 ,-83.084 ,-89.9868,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,-79.057 ,-77.043 ,-78.368 ,-73.601 }};
	*/

	// table02
    double rssi_table[][] = //[beacon_count][training_palce_count] , offline data
                   {{-77.118 ,-74.322 ,-85.408 ,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,-84.447 },
                    {-82.762 ,-76.27  ,-72.351 ,-72     ,-77.711 ,-78.645 ,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi},
                    {min_rssi,min_rssi,-77.487 ,-64	    ,-67.579 ,-78.554 ,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi},
                    {min_rssi,min_rssi,min_rssi,-72	    ,-79.013 ,-73.236 ,-70.658 ,-84.804 ,min_rssi,min_rssi,min_rssi,min_rssi},
                    {min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,-80.5   ,-77.562	,min_rssi,min_rssi,min_rssi,min_rssi},
                    {min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,-77.684 ,-73.066	,-76.544 ,-70     ,-75     ,min_rssi},
                    {min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,-78.213 ,-61.333 ,-61     ,-80.618 },
                    {-75.803 ,-83.084 ,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,min_rssi,-79.057 ,-73.043 ,-70     ,-73.601 }};

	double checked_table[][];//[beacon_count][training_palce_count]
	double checked_default_value = 100.0;//init checked_table

	String shop_log = "";
	int pos_count = 0;
	int all_count = 0;
	boolean logging = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop);

		findView();
		setOnclickListerer();
		init_rssi_table(); //to correct rssi_table

		beaconManager = new BeaconManager(this);
		beaconManager.setRangingListener(new BeaconManager.RangingListener() {
			@Override
			public void onBeaconsDiscovered(Region region,
											final List<Beacon> beacons) {
				// Note that results are not delivered on UI thread.
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						// Note that beacons reported here are already
						// sorted by rssi
						// estimated
						// distance between device and beacon.
						if (DEBUG_MODE)
							Log.e("beacons size = ", String.valueOf(beacons.size()));

						renewShop(checkTheCloestShop(beacons));
					}
				});
			}
		});

	}

	private void findView(){
		img1 = (ImageButton) findViewById(R.id.ImageButton1);
		img2 = (ImageButton) findViewById(R.id.ImageButton2);
		img3 = (ImageButton) findViewById(R.id.ImageButton3);
		img4 = (ImageButton) findViewById(R.id.ImageButton4);
		img5 = (ImageButton) findViewById(R.id.ImageButton5);
		img6 = (ImageButton) findViewById(R.id.ImageButton6);
		img7 = (ImageButton) findViewById(R.id.ImageButton7);
		img8 = (ImageButton) findViewById(R.id.ImageButton8);
		img9 = (ImageButton) findViewById(R.id.ImageButton9);
		img10 = (ImageButton) findViewById(R.id.ImageButton10);
		img11 = (ImageButton) findViewById(R.id.ImageButton11);
		img12 = (ImageButton) findViewById(R.id.ImageButton12);

		bt_log = (Button) findViewById(R.id.log);
		bt_clear = (Button) findViewById(R.id.clear);
		bt_calibrated_value_up = (Button) findViewById(R.id.calibrated_value_up);
		bt_calibrated_value_down = (Button) findViewById(R.id.calibrated_value_down);
		bt_deviation_value_up = (Button) findViewById(R.id.deviation_value_up);
		bt_deviation_value_down = (Button) findViewById(R.id.deviation_value_down);
		bt_rssi_filter_value_up = (Button) findViewById(R.id.rssi_filter_value_up);
		bt_rssi_filter_value_down = (Button) findViewById(R.id.rssi_filter_value_down);
		bt_reset = (Button) findViewById(R.id.reset);
		bt_start_log = (Button)findViewById(R.id.start_log);
		bt_stop_log = (Button)findViewById(R.id.stop_log);

		textview_tp = (TextView) findViewById(R.id.tp);
		textview_calibrated = (TextView) findViewById(R.id.calibrated_value);
		textview_deviation = (TextView) findViewById(R.id.deviation_value);
		textview_filter = (TextView) findViewById(R.id.rssi_filter_value);
		textview_log_count = (TextView) findViewById(R.id.log_count);
		textview_closest_beacon = (TextView) findViewById(R.id.closest_beacon);
	}


	private void init_rssi_table(){
		for(int i = 0 ; i < beacon_count ; i++){
			for(int j = 0 ;j < training_palce_count ; j++) {
				rssi_table[i][j] -= calibrated_value ;
				if(rssi_table[i][j] < rssi_filter_min_value)
					rssi_table[i][j] = min_rssi;
			}
		}
	}

	private void init_checked_table(){ //init checked_table
		checked_table = new double[beacon_count][training_palce_count];
		for(int i = 0 ; i < beacon_count ; i++){
			for(int j = 0 ;j < training_palce_count ; j++) {
				checked_table[i][j] = checked_default_value ;
			}
		}
	}

	public ImageButton checkTheCloestShop(List<Beacon> beacons) {
		ImageButton theCloestShop = null;

		int patternMatching = pattern_matching(beacons);
		Pair closestBeacons = getClosestBeacon(beacons);

        int major1 = closestBeacons.getN1();
        int major2 = closestBeacons.getN2();


        Pair p1 = majorToTp(major1);
        int p1_tp1 = p1.getN1();
        int p1_tp2 = p1.getN2();

        Pair p2 = majorToTp(major2);
        int p2_tp1 = p2.getN1();
        int p2_tp2 = p2.getN2();

        if(patternMatching != p1_tp1 && patternMatching != p1_tp2){
            theClosestTp = p1_tp2;
            /*
            if(patternMatching != p2_tp1 || patternMatching != p2_tp2){
                //...
                theClosestTp = p1_tp1;
            }else{
                theClosestTp = patternMatching;
            }*/
        }else{
            theClosestTp = patternMatching;
        }

		switch (theClosestTp){
			case 1:
				theCloestShop = img1;
				break;
			case 2:
				theCloestShop = img2;
				break;
			case 3:
				theCloestShop = img3;
				break;
			case 4:
				theCloestShop = img4;
				break;
			case 5:
				theCloestShop = img5;
				break;
			case 6:
				theCloestShop = img6;
				break;
			case 7:
				theCloestShop = img7;
				break;
			case 8:
				theCloestShop = img8;
				break;
			case 9:
				theCloestShop = img9;
				break;
			case 10:
				theCloestShop = img10;
				break;
			case 11:
				theCloestShop = img11;
				break;
			case 12:
				theCloestShop = img12;
				break;
		}

		//store log
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		Date date = new Date();
		String strDate = sdFormat.format(date);

		if(logging){
			pos_count++;
			all_count++;
			shop_log += pos_count + ". " + strDate + " theClosestTp = " + theClosestTp + "\n";
		}

		if(DEBUG_MODE)
			Log.e("theClosestTp = ",String.valueOf(theClosestTp));

		return theCloestShop;
	}



	private Pair getClosestBeacon(List<Beacon> beacons){
        Pair p = new Pair();

		int closestBeacon = 0;
		int size = beacons.size();
		if(size == 1) {
			closestBeacon = beacons.get(0).getMajor();
            p.setN1(closestBeacon);
            p.setN2(closestBeacon);
		}else if(size >= 2){
            closestBeacon = beacons.get(0).getMajor();
            p.setN1(beacons.get(0).getMajor());
            p.setN2(beacons.get(1).getMajor());
		}
		theCloseedBeacon = closestBeacon;

		if(DEBUG_MODE)
			Log.e("closestBeacon=" + closestBeacon + " size=",String.valueOf(size));

        return p;
	}


    class Pair{
        private int n1 = 0;
        private int n2 = 0;
        void setN1(int major){
            this.n1 = major;
        }
        void setN2(int major){
            this.n2 = major;
        }
        int getN1(){
            return this.n1;
        }
        int getN2(){
            return this.n2;
        }
    }

    private Pair majorToTp(int major){
        Pair p = new Pair();
        int tp1 = 0;
        int tp2 = 0;
        switch (major){
            case 1:
                tp1 = 1;
                tp2 = 2;
                break;
            case 2:
                tp1 = 2;
                tp2 = 3;
                break;
            case 3:
                tp1 = 4;
                tp2 = 5;
                break;
            case 4:
                tp1 = 6;
                tp2 = 7;
                break;
            case 5:
                tp1 = 7;
                tp2 = 8;
                break;
            case 6:
                tp1 = 8;
                tp2 = 9;
                break;
            case 7:
                tp1 = 10;
                tp2 = 11;
                break;
            case 8:
                tp1 = 11;
                tp2 = 12;
                break;
        }
        p.setN1(tp1);
        p.setN2(tp2);

        return  p;
    }

    private int pattern_matching(List<Beacon> beacons){

        int closetTp = 0;
		/* get beacons information and return tp with shop*/

        int rssi[] = new int[beacon_count];
        String patterMatchingInfo = "";
        String patterMatchingSelected = "";

        for (Beacon b : beacons) {
            int index = b.getMajor() - 1;
            int rssi_value = b.getRssi();

            if (index >= 0 && index < beacon_count && SHOW_DETAIL_DATA && DEBUG_MODE)
                patterMatchingInfo += "major = " + String.valueOf(b.getMajor()) + " rssi = " + String.valueOf(rssi_value) + " ,";

            if (index >= 0 && index < beacon_count && rssi_value > rssi_filter_min_value) {
				if(rssi_value < rssi_filter_max_value) //add by 20150915 ,
                	rssi[index] = rssi_value - calibrated_value_of_filterMinValue;
				else
					rssi[index] = rssi_value;

				if(SHOW_DETAIL_DATA && DEBUG_MODE)
                    patterMatchingSelected += "major = " + String.valueOf(b.getMajor()) + " rssi = " + String.valueOf(rssi_value) + " ,";
            }
        }

        if (SHOW_DETAIL_DATA && DEBUG_MODE){
            Log.e("patterMatchingInfo = ", patterMatchingInfo);
            Log.e("beaconsInserted = ", patterMatchingSelected);
        }
        //renew checked_table
        init_checked_table();
        for(int j = 0 ;j < training_palce_count ; j++) {
            for(int i = 0 ; i < beacon_count ; i++){
                if(rssi[i]!=0 && Math.abs(rssi[i] - rssi_table[i][j]) <= deviation_value) {
                    checked_table[i][j] = Math.abs(rssi[i] - rssi_table[i][j]);
                    if(SHOW_DETAIL_DATA && DEBUG_MODE)
                        Log.e("checked_table[" + i + "][" + j + "] = ",String.valueOf(checked_table[i][j])); //
                }
            }
        }

        //find the minimum of checked_table[training_palce_count]'s sum
        double sum[] = new double[training_palce_count];
        for(int j = 0 ;j < training_palce_count ; j++) {
            for(int i = 0 ; i < beacon_count ; i++){
                sum[j] += checked_table[i][j];
            }
            if(DEBUG_MODE)
                Log.e("sum[] = ",String.valueOf(sum[j]));
        }

        boolean findMin = false;
        boolean min = false;
        for(int i = 0 ; i < training_palce_count ; i++){
            for(int j = 0 ; j <training_palce_count ; j++){
                if(sum[i] > sum[j]){
                    min = false;
                    break;
                }
                if(sum[i] != (double)beacon_count * checked_default_value)
                    min = true;

                if(SHOW_DETAIL_DATA && DEBUG_MODE){
                    String str  ="i = " + String.valueOf(i) + " sum[" +String.valueOf(i) + "] = " + String.valueOf(sum[i])
                            + ", j = " + String.valueOf(j) + " sum[" +String.valueOf(j) + "] = " + String.valueOf(sum[j]);
                    Log.e("str = ",  str);
                }

            }
            if(min)
                closetTp = i + 1;
        }
        if(DEBUG_MODE)
            Log.e("pattern_matching Tp = ",String.valueOf(closetTp));
        return closetTp;
    }

    public void renewShop(ImageButton shop){
		img1.setImageResource(R.drawable.a);
		img2.setImageResource(R.drawable.a);
		img3.setImageResource(R.drawable.a);
		img4.setImageResource(R.drawable.a);
		img5.setImageResource(R.drawable.a);
		img6.setImageResource(R.drawable.a);
		img7.setImageResource(R.drawable.a);
		img8.setImageResource(R.drawable.a);
		img9.setImageResource(R.drawable.a);
		img10.setImageResource(R.drawable.a);
		img11.setImageResource(R.drawable.a);
		img12.setImageResource(R.drawable.a);

		img1.setClickable(false);
		img2.setClickable(false);
		img3.setClickable(false);
		img4.setClickable(false);
		img5.setClickable(false);
		img6.setClickable(false);
		img7.setClickable(false);
		img8.setClickable(false);
		img9.setClickable(false);
		img10.setClickable(false);
		img11.setClickable(false);
		img12.setClickable(false);


		if(shop!=null) {
			shop.setImageResource(R.drawable.b);
			shop.setClickable(true);
		}
		textview_closest_beacon.setText("major = " + theCloseedBeacon);
		textview_tp.setText("Tp = " + theClosestTp);
		textview_calibrated.setText("calibrated = " + calibrated_value);
		textview_deviation.setText("deviation = " + deviation_value);
		textview_filter.setText("rssi_filter = " + rssi_filter_min_value);
		textview_log_count.setText(String.valueOf(pos_count) + "/" + String.valueOf(all_count));
	}

	private void openWebForShop(int shopNum){
		//show web
		if(shopNum > 0 && shopNum < TRAINING_PLACE_COUNT + 1) {
			String num = (shopNum < 10 ? "0" : "") + String.valueOf(shopNum);
			String shopUrl = "https://teststore" + num + ".wordpress.com/";
			Uri shopWeb = Uri.parse(shopUrl);
			Intent it = new Intent(Intent.ACTION_VIEW, shopWeb);
			startActivity(it);
		}
	}

    private void setOnclickListerer(){

        img1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DEBUG_MODE)
                    Log.e("img1 = ","cilckable");
				openWebForShop(1);
            }
        });
        img2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DEBUG_MODE)
                    Log.e("img2 = ","cilckable");
				openWebForShop(2);
            }
        });
        img3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DEBUG_MODE)
                    Log.e("img3 = ","cilckable");
				openWebForShop(3);
            }
        });
        img4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DEBUG_MODE)
					Log.e("img4 = ","cilckable");
				openWebForShop(4);
            }
        });
        img5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DEBUG_MODE)
					Log.e("img5 = ","cilckable");
				openWebForShop(5);
            }
        });
        img6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
					Log.e("img6 = ", "cilckable");
				openWebForShop(6);
            }
        });
        img7.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
					Log.e("img7 = ", "cilckable");
				openWebForShop(7);
            }
        });
        img8.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
                    Log.e("img8 = ", "cilckable");
				openWebForShop(8);
            }
        });
        img9.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
					Log.e("img9 = ", "cilckable");
				openWebForShop(9);
            }
        });
        img10.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
					Log.e("img10 = ", "cilckable");
				openWebForShop(10);
            }
        });
        img11.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
					Log.e("img11 = ", "cilckable");
				openWebForShop(11);
            }
        });
        img12.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
					Log.e("img12 = ", "cilckable");
				openWebForShop(12);
            }
        });


        bt_log.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
                    Log.e("bt_log = ", "cilckable");

                Intent intent = new Intent(ShopActivity.this,
                        Shop_log.class);
                intent.putExtra("log", shop_log); // info
                startActivity(intent);
            }
        });

        bt_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
                    Log.e("bt_clear = ", "cilckable");
                shop_log = "";
            }
        });

        bt_calibrated_value_up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
                    Log.e("bt_calibrated_up = ", "cilckable");
                calibrated_value += per_value;
            }
        });

        bt_calibrated_value_down.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
                    Log.e("bt_calibrated_down = ", "cilckable");
                calibrated_value -= per_value;
            }
        });

        bt_deviation_value_up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
                    Log.e("bt_deviation_up = ", "cilckable");
                deviation_value += per_value;
            }
        });

        bt_deviation_value_down.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
                    Log.e("bt_deviation_down = ", "cilckable");
                deviation_value -= per_value;
            }
        });

        bt_rssi_filter_value_up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
                    Log.e("bt_rssi_filter_up = ", "cilckable");
				rssi_filter_min_value += per_value;
            }
        });

        bt_rssi_filter_value_down.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
                    Log.e("bt_rssi_filter_down = ", "cilckable");
				rssi_filter_min_value -= per_value;
            }
        });

        bt_reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
                    Log.e("bt_reset = ", "clickable");
				rssi_filter_min_value = RSSI_FILTER_MIN_VALUE;
                calibrated_value = CALIBRATED_VALUE;
                deviation_value = DEVIATION_VALUE;
            }
        });

        bt_start_log.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
                    Log.e("bt_start_log = ", "cilckable");

                bt_start_log.setEnabled(false);
                bt_stop_log.setEnabled(true);
                logging = true;
                pos_count = 0;
            }
        });

        bt_stop_log.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_MODE)
                    Log.e("bt_stop_log = ", "cilckable");

                bt_start_log.setEnabled(true);
                bt_stop_log.setEnabled(false);
                logging = false;
                shop_log += "\n";
            }
        });

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
			beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
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
		//adapter.replaceWith(Collections.<Beacon> emptyList(), c);
		beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
			@Override
			public void onServiceReady() {
				try {
					beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
				} catch (RemoteException e) {
					Toast.makeText(
							ShopActivity.this,
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
						Intent intent = new Intent(ShopActivity.this,
								clazz);
						//intent.putExtra(EXTRAS_BEACON,
						//		adapter.getItem(position));
						startActivity(intent);
					} catch (ClassNotFoundException e) {
						Log.e(TAG, "Finding class by name failed", e);
					}
				}
			}
		};
	}
}
