package com.example.demonickrace.servicetest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Date;

public class MyService extends Service {

    public class ServiceBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    private final IBinder mBinder = new ServiceBinder();
    private Handler handler = new Handler();

    private Runnable showTime = new Runnable() {
        public void run() {
            // log目前時間
            Log.i("time:", new Date().toString());
            handler.postDelayed(this, 1000);
        }
    };

    public void custom()
    {
        handler.postDelayed(showTime, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.i("服務", "呼叫onStartCommand方法");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        Log.i("服務", "呼叫onBind方法");
        return mBinder;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i("服務", "建立");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i("服務", "銷毀");
        handler.removeCallbacks(showTime);
    }
}