package com.estimote.examples.demos;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;


/**
 * Displays basic information about beacon.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class LeDeviceListAdapter extends BaseAdapter {

  private ArrayList<Beacon> beacons;
  private LayoutInflater inflater;

  private static double c=1.0;
  
  public LeDeviceListAdapter(Context context) {
    this.inflater = LayoutInflater.from(context);
    this.beacons = new ArrayList<Beacon>();
  }

  public void replaceWith(Collection<Beacon> newBeacons,double c) {
	this.c=c;
    this.beacons.clear();
    this.beacons.addAll(newBeacons);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return beacons.size();
  }

  @Override
  public Beacon getItem(int position) {
    return beacons.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    view = inflateIfRequired(view, position, parent);
    bind(getItem(position), view);
    return view;
  }

	  private void bind(Beacon beacon, View view) {
    ViewHolder holder = (ViewHolder) view.getTag();
    /*
        log_table = krace_LogNo_1
		major = 1
		minor = 32968
		start_pos = 1
		end_pos = 15
		y = 0.1446792178409801x2 + -3.553438914027098x1 + -50.4276373626382x0
    */
    
    //polyfit
    /*
    	int rssi = beacon.getRssi();
    	double a = 0.1446792178409801;
    	double b = -3.553438914027098;
    	double c =  -50.4276373626382 - rssi;
    	double d = b*b-4*a*c;
    	double distance = 0;
    	double distance1 = 0;
    	double distance2 = 0;
    	//distance1 = (-b + Math.sqrt(b*b-4*a*c) )/(2*a);
    	//distance2 = (-b - Math.sqrt(b*b-4*a*c) )/(2*a);
    	
    	Log.e("rssi = ",String.format("%d",rssi));
    	Log.e("b*b-4*a*c =  ",String.format("%.2f",(b*b-4*a*c)));
    	Log.e("distance1 = ",String.format("%.2f", (-b + Math.sqrt(b*b-4*a*c) )/(2*a)));
    	Log.e("distance2 = ",String.format("%.2f", (-b - Math.sqrt(b*b-4*a*c) )/(2*a)));
    	
    	if(d < 0){
    		distance = (-b)/(2*a);
    	}else{
	    	distance1 = (-b + Math.sqrt(b*b-4*a*c) )/(2*a);
	    	distance2 = (-b - Math.sqrt(b*b-4*a*c) )/(2*a);
	    	distance = (distance1 <  distance2)?distance1 :distance2;
    	}
	*/
    double distance = Utils.computeAccuracy(beacon)*c;
    
    //
    holder.macTextView.setText(String.format("MAC: %s (%.2fm)", beacon.getMacAddress(), distance)); //Utils.computeAccuracy(b) * c
    holder.majorTextView.setText("Major: " + beacon.getMajor());
    holder.minorTextView.setText("Minor: " + beacon.getMinor());
    holder.measuredPowerTextView.setText("MPower: " + beacon.getMeasuredPower());
    holder.rssiTextView.setText("RSSI: " + beacon.getRssi());
  }

  private View inflateIfRequired(View view, int position, ViewGroup parent) {
    if (view == null) {
      view = inflater.inflate(R.layout.device_item, null);
      view.setTag(new ViewHolder(view));
    }
    return view;
  }

  static class ViewHolder {
    final TextView macTextView;
    final TextView majorTextView;
    final TextView minorTextView;
    final TextView measuredPowerTextView;
    final TextView rssiTextView;

    ViewHolder(View view) {
      macTextView = (TextView) view.findViewWithTag("mac");
      majorTextView = (TextView) view.findViewWithTag("major");
      minorTextView = (TextView) view.findViewWithTag("minor");
      measuredPowerTextView = (TextView) view.findViewWithTag("mpower");
      rssiTextView = (TextView) view.findViewWithTag("rssi");
    }
  }
}
