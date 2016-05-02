package com.example.demonickrace.alertdialog_example;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private Button alertDialog1;
    private Button alertDialog2;
    private Button alertDialog3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getButtonView();
        setButtonEvent();

    }

    public void getButtonView(){
        alertDialog1 = (Button)findViewById(R.id.alertdialog1);
        alertDialog2 = (Button)findViewById(R.id.alertdialog2);
        alertDialog3 = (Button)findViewById(R.id.alertdialog3);
    }
    public void setButtonEvent(){
        alertDialog1.setOnClickListener(buttonListener);
        alertDialog2.setOnClickListener(buttonListener);
        alertDialog3.setOnClickListener(buttonListener);
    }

    private View.OnClickListener buttonListener = new View.OnClickListener(){
        @Override
        public void onClick(final View view) {
            switch(view.getId()){
                case R.id.alertdialog1:
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("午餐時間")
                            .setMessage("要吃飯了嗎?")
                            .setPositiveButton("好", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "走吧！一起吃", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("等下再吃", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(),"可是我好餓耶", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNeutralButton("不餓", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(),"你減肥嗎？", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                    break;
                case R.id.alertdialog2:
                    final String[] lunch = {
                            "A","B","C","D","E","F"
                    };
                    new AlertDialog.Builder(MainActivity.this)
                            .setItems(lunch, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "your choose is which one?" + lunch[which], Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                    break;
                case R.id.alertdialog3:

                    break;
            }
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
