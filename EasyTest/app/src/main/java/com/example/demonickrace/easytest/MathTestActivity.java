package com.example.demonickrace.easytest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MathTestActivity extends AppCompatActivity {

    TextView title;
    TextView testContent;
    TextView operation;
    TextView answer;
    TextView answerReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_test);
        Intent mIntent = new Intent();
        //mIntent.putExtra("key", "value");
        this.setResult(MainActivity.requestCodeForMath, mIntent);
    }

    public void init(){
        title = (TextView)findViewById(R.id.testTitleTextView);
        testContent = (TextView)findViewById(R.id.testContentTextView);
        operation = (TextView)findViewById(R.id.operationTextView);
        answer = (TextView)findViewById(R.id.answerEditText);
        answerReply = (TextView)findViewById(R.id.answerReplyTextView);
    }
}
