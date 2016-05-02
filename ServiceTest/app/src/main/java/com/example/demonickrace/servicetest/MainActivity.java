package com.example.demonickrace.servicetest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

    Button open,stop;
    MyService myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        open = (Button) findViewById(R.id.button1);
        open.setOnClickListener(new OnClickListener () {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, MyService.class);
                bindService(intent, connc, Context.BIND_AUTO_CREATE);
            }});

        stop = (Button) findViewById(R.id.button2);
        stop.setOnClickListener(new OnClickListener () {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                unbindService(connc);
            }});
    }

    private ServiceConnection connc = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            myService=((MyService.ServiceBinder)service).getService();
            myService.custom();
            Log.i("MainActivity","onServiceConnected IBinder");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            myService=null;
            Log.i("MainActivity","onServiceConnected");
        }

    };
}