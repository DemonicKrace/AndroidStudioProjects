package com.estimote.examples.demos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Show_log extends Activity {

	//Intent intent;
	
	TextView pos;
	TextView all;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_log);
		pos = (TextView) findViewById(R.id.pos_log);
		all = (TextView) findViewById(R.id.all_log);;
		
		pos.setText(this.getIntent().getStringExtra("pos_log"));
		all.setText(this.getIntent().getStringExtra("all_log"));
	}
}
