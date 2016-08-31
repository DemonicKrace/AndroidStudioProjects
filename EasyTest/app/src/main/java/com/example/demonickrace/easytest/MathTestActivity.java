package com.example.demonickrace.easytest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MathTestActivity extends AppCompatActivity implements View.OnClickListener{

    final boolean DEBUG_MODE = true;
    TextView titleTextView;
    TextView testContentTextView;
    TextView operationTextView;
    TextView answerTextView;
    TextView answerReplyTextView;
    EditText answerEditText;
    Button solutionBtn;
    Button nextQuestionBtn;

    MathExercise mathExercise;

    MathExerciseError mathExerciseError;

    int exerciseCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_test);

        Intent mIntent = new Intent();
        //mIntent.putExtra("key", "value");
        this.setResult(MainActivity.requestCodeForMath, mIntent);

        mathExercise = new MathExercise();
        mathExerciseError = new MathExerciseError();
        initView();
    }

    // init all view
    public void initView(){
        titleTextView = (TextView)findViewById(R.id.testTitleTextView);
        testContentTextView = (TextView)findViewById(R.id.testContentTextView);
        operationTextView = (TextView)findViewById(R.id.operationTextView);
        answerTextView = (TextView)findViewById(R.id.answerEditText);
        answerReplyTextView = (TextView)findViewById(R.id.answerReplyTextView);
        answerEditText = (EditText)findViewById(R.id.answerEditText);

        solutionBtn = (Button)findViewById(R.id.solutionBtn);
        solutionBtn.setOnClickListener(this);

        nextQuestionBtn = (Button)findViewById(R.id.nextQuestionBtn);
        nextQuestionBtn.setOnClickListener(this);

        //init exerciseCount => 1 , and update view
        if(mathExercise.getExerciseNumber() > 0){
            exerciseCount = 1;
            updateQuestion(exerciseCount);
        }
    }

    //set listener
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.solutionBtn:

                if(DEBUG_MODE) Log.d("onClick","solutionBtn is clicked");
                if(DEBUG_MODE) Log.d("exerciseCount = ",String.valueOf(exerciseCount));

                String answer = answerEditText.getText().toString();

                if(answer.equals("請輸入答案")||answer.equals("")){
                    Toast.makeText(this, "尚未輸入答案，請輸入答案!", Toast.LENGTH_SHORT).show();
                }else if(solutionResult(exerciseCount,answer)){
                    if(DEBUG_MODE) Log.d("solutionResult = ","correct");
                    answerReplyTextView.setText("答案正確!");
                }else{
                    //analyze error type
                    if(DEBUG_MODE) Log.d("solutionResult = ","incorrect");
                    //answerReplyTextView.setText("答案錯誤");
                    String msg = "答案錯誤!\n提示:" + mathExerciseError.getAnalysisErrorMsg(exerciseCount,answer);
                    answerReplyTextView.setText(msg);
                }
                break;
            case R.id.nextQuestionBtn:

                if(DEBUG_MODE) Log.d("onClick","nextQuestionBtn is clicked");

                if(nextQuestion(exerciseCount)) {
                    if(DEBUG_MODE) Log.d("nextQuestion = ","true");

                    Toast.makeText(this, "第" + exerciseCount + "題", Toast.LENGTH_SHORT).show();
                    updateQuestion(exerciseCount);
                }else{
                    if(DEBUG_MODE) Log.d("nextQuestion = ","true");

                    Toast.makeText(this, "無下一題", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    String getErrorMsg(){
        String msg = null;

        return msg;
    }

    int analyzeErrorType(){
        int errorCode = 0;
        return errorCode;
    }

    //when solutionBtn is clicked
    boolean solutionResult(int number,String answer){

        if(DEBUG_MODE) Log.d("solutionResult","answerEditText = "+ answer);
        if(DEBUG_MODE) Log.d("solutionResult","number = "+ String.valueOf(number));

        return mathExercise.getAnswerResult(exerciseCount,answer);
    }

    //when nextQuestionBtn is clicked,change to next exercise to (testContentTextView & operationTextView & titleTextView)
    boolean nextQuestion(int number){
        boolean hasNextQuestion = false;

        if(DEBUG_MODE) Log.d("nextQuestion","number = "+ String.valueOf(number));

        if(number < mathExercise.getExerciseNumber()){
            exerciseCount++;
            hasNextQuestion = true;
        }

        return hasNextQuestion;
    }

    void updateQuestion(int number){

        if(DEBUG_MODE) Log.d("updateQuestion","number = "+ String.valueOf(number));

        titleTextView.setText("第" + number + "題");
        answerReplyTextView.setText("");
        testContentTextView.setText("");
        operationTextView.setText("");
        answerEditText.setText("請輸入答案");

        //change content and operation
        switch (number){
            //16,24,27
            case 16:case 24:case 27:
                testContentTextView.setText(mathExercise.getExerciseContent(number));
                break;
            default:
                operationTextView.setText(mathExercise.getExerciseContent(number));
                break;
        }

    }
}
