package com.example.demonickrace.listviewdynamic_example;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private EditText inputText;
    private ListView listInput;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = (EditText)findViewById(R.id.inputText);
        listInput = (ListView)findViewById(R.id.listInputText);
        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,items);
        listInput.setAdapter(adapter);


        listInput.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
                // TODO Auto-generated method stub
                final int pos = position;
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("刪除列")
                        .setMessage("你確定要刪除？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                items.remove(pos);
                                listInput.setAdapter(adapter);
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        })
                        .show();

                return false;
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        menu.add(0, 0, 0, "新增");
        menu.add(0, 1, 1, "離開程式");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case 0:
                if(!inputText.getText().toString().equals("")){
                    items.add(inputText.getText().toString());
                    listInput.setAdapter(adapter);
                    inputText.setText("");
                }
                break;
            case 1:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("離開此程式")
                        .setMessage("你確定要離開？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                finish();
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}
