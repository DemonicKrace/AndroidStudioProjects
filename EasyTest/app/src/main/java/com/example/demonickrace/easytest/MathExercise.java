package com.example.demonickrace.easytest;

/**
 * Created by demonickrace on 16/6/18.
 */
public class MathExercise {

    private MathExerciseData exerciseData;
    private String[] exerciseContent;
    private String[] exerciseAnswer;
    private int exerciseNumber;


    MathExercise(){
        exerciseData = new MathExerciseData();

        //if exerciseData is not empty
        if(!exerciseData.isEmpty()){
            this.exerciseContent = exerciseData.getExerciseContent();
            this.exerciseAnswer = exerciseData.getExerciseAnswer();
            this.exerciseNumber = exerciseData.getExerciseNumber();
        }
    }

    /*
    //reset exercise data ...
    public void setExerciseData(String[] content,String[] answer){

    }
    */

    //index is between 1 ~ exerciseNumber
    public String getExerciseContent(int index){
        if(index > 0 && index <= exerciseNumber)
            return exerciseContent[index - 1];
        else
            return null;
    }

    //index is between 1 ~ exerciseNumber
    public String getExerciseAnswer(int index){
        if(index > 0 && index <= exerciseNumber)
            return exerciseAnswer[index - 1];
        else
            return null;
    }

    public int getExerciseNumber(){
        return exerciseNumber;
    }

    //index is between 1 ~ exerciseNumber
    public boolean getAnswerResult(int index,String answer){
        return index > 0 && index <= exerciseNumber && exerciseAnswer[index - 1].equals(answer);
    }
}
