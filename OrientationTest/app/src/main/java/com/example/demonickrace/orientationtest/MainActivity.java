package com.example.demonickrace.orientationtest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    SensorManager sensorManager;
    boolean orientationPresent;
    Sensor orientationSensor;

    TextView textInfo, textX, textY, textZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInfo = (TextView)findViewById(R.id.info);
        textX = (TextView)findViewById(R.id.textx);
        textY = (TextView)findViewById(R.id.texty);
        textZ = (TextView)findViewById(R.id.textz);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);

        if(sensorList.size() > 0){
            orientationPresent = true;
            orientationSensor = sensorList.get(0);

            String strSensor  = "Name: " + orientationSensor.getName()
                    + "\nVersion: " + String.valueOf(orientationSensor.getVersion())
                    + "\nVendor: " + orientationSensor.getVendor()
                    + "\nType: " + String.valueOf(orientationSensor.getType())
                    + "\nMax: " + String.valueOf(orientationSensor.getMaximumRange())
                    + "\nResolution: " + String.valueOf(orientationSensor.getResolution())
                    + "\nPower: " + String.valueOf(orientationSensor.getPower())
                    + "\nClass: " + orientationSensor.getClass().toString();
            textInfo.setText(strSensor);
        }
        else{
            orientationPresent = false;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if(orientationPresent){
            sensorManager.registerListener(orientationListener, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(this, "Register accelerometerListener", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

        if(orientationPresent){
            sensorManager.unregisterListener(orientationListener);
            Toast.makeText(this, "Unregister accelerometerListener", Toast.LENGTH_LONG).show();
        }
    }

    private SensorEventListener orientationListener = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            textX.setText("X: " + String.valueOf(event.values[0])); //對z軸旋轉，方位角
            textY.setText("Y: " + String.valueOf(event.values[1])); //對x軸旋轉，
            textZ.setText("Z: " + String.valueOf(event.values[2])); //對y軸旋轉，
        }};

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
