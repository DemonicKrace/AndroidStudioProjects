package com.example.demonickrace.sensortest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView text_x;
    private TextView text_y;
    private TextView text_z;
    private TextView info;
    private TextView text_count;
    private TextView text_gforce;
    /** Called when the activity is first created. */

    SensorManager sensorManager;
    boolean accelerometerPresent;
    Sensor accelerometerSensor;

    private  static final boolean DEG_MODE = true;
    private static final float SPEED_SHRESHOLD = 50.0f;
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.0F;
    private static final int SHAKE_SLOP_TIME_MS = 500;

    long lastUpdateTime= 0;
    long UPTATE_INTERVAL_TIME = 500; //ms
    long mShakeTimestamp;

    float lastX;
    float lastY;
    float lastZ;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        text_x = (TextView)findViewById(R.id.TextView01);
        text_y = (TextView)findViewById(R.id.TextView02);
        text_z = (TextView) findViewById(R.id.TextView03);
        info = (TextView) findViewById(R.id.Info);
        text_count = (TextView) findViewById(R.id.Count);
        text_gforce = (TextView) findViewById(R.id.GForce);

        //getSystemService(SENSOR_SERVICE)取得感測器服務
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        //取得全部加速感測器 TYPE_ACCELEROMETER
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensorList.size() > 0){
            accelerometerPresent = true;

            //取得第一個 加速感測器
            accelerometerSensor = sensorList.get(0);

            String strSensor  = "Name: " + accelerometerSensor.getName()
                    + "\nVersion: " + String.valueOf(accelerometerSensor.getVersion())
                    + "\nVendor: " + accelerometerSensor.getVendor()
                    + "\nType: " + String.valueOf(accelerometerSensor.getType())
                    + "\nMax: " + String.valueOf(accelerometerSensor.getMaximumRange())
                    + "\nResolution: " + String.valueOf(accelerometerSensor.getResolution())
                    + "\nPower: " + String.valueOf(accelerometerSensor.getPower())
                    + "\nClass: " + accelerometerSensor.getClass().toString();
                    info.setText(strSensor);
        }
        else{
            accelerometerPresent = false;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        /*
        Sensor更新速度如下表，控制方式為程式碼淺藍色標記部分
        Sensor.manager.SENSOR_DELAY_FASTEST	  0ms
        Sensor.manager.SENSOR_DELAY_GAME	  20ms
        Sensor.manager.SENSOR_DELAY_UI	  60ms
        Sensor.manager.SENSOR_DELAY_NORMAL	  200ms
        */
        if(accelerometerPresent){
            //(Listener,SensorType,SensorDelay)
            sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            //sensorManager.registerListener(accelerometerListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

            Toast.makeText(this, "Register accelerometerListener", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

        if(accelerometerPresent){
            sensorManager.unregisterListener(accelerometerListener);
            Toast.makeText(this, "Unregister accelerometerListener", Toast.LENGTH_LONG).show();
        }
    }

    private SensorEventListener accelerometerListener = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // TODO Auto-generated method stub
            //當感應器的精準度改變時會呼叫此方法
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            text_x.setText("X: " + String.valueOf(event.values[0]));
            text_y.setText("Y: " + String.valueOf(event.values[1]));
            text_z.setText("Z: " + String.valueOf(event.values[2]));

            //f1
            /*
            long currentUpdateTime = System.currentTimeMillis();
            long timeInterval = currentUpdateTime - lastUpdateTime;
            if (timeInterval < UPTATE_INTERVAL_TIME)
                return;
            lastUpdateTime = currentUpdateTime;
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float deltaX = x - lastX;
            float deltaY = y - lastY;
            float deltaZ = z - lastZ;

            lastX = x;
            lastY = y;
            lastZ = z;
            double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                    * deltaZ)
                    / timeInterval * 10000;
            Log.v("thelog", "===========log===================");
            if (speed >= SPEED_SHRESHOLD) {
                if(DEG_MODE)
                    Log.e("speed = ",String.valueOf(speed));
                count++;
                text_count.setText("Count = " + count);
            }
            */
            //f2

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = FloatMath.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }

                /*
                // reset the shake count after 3 seconds of no shakes
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0;
                }
                */

                mShakeTimestamp = now;
                count++;

                if(DEG_MODE)
                    Log.e("speed = ",String.valueOf(gForce));
                count++;
                text_count.setText("Count = " + count);
            }
            text_gforce.setText("gForece = "+ gForce);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
