package com.example.demonickrace.easytest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
public class MainActivity extends AppCompatActivity implements OnClickListener{

    final String tag = "MainActivity";
    public static final int requestCodeForMath = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.startBtn).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.startBtn:
                Log.d(tag,"startBtn clicked");
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MathTestActivity.class);
                startActivityForResult(intent, requestCodeForMath);
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case requestCodeForMath:
                Log.d(tag,"get requestCodeForMath");
                break;
        }
    }
}
