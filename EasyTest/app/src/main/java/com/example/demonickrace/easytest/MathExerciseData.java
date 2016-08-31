package com.example.demonickrace.easytest;

/**
 * Created by demonickrace on 16/6/18.
 */
public class MathExerciseData {

    private final int exerciseNumber = 30;

    private boolean dataIsEmpty;

    private String[] exerciseContent = {
            "(-15)×0×8", //1
            "7×(-15)÷3×0",//2
            "(-312)÷(-24)×0×(-11)",//3
            "(-28)÷7×0÷(-3)",//4
            "0÷(-36)",//5
            "(-81)×0÷9",//6
            "(-7)×16",//7
            "81÷(-9)×3",//8
            "18×(-6)",//9
            "(-13)×(-6)",//10
            "(-16)×8×5",//11
            "(-96)÷(-8)",//12
            "(-111)÷3",//13
            "75÷(-15)",//14
            "(-15)×9÷(-3)",//15
            "試問媽媽到超市買東西，超市的民生用品的標價如下：" +
            "130抽的平版衛生紙每包15元，泡麵每包12元，兩公升裝的鮮奶每罐150元，土雞蛋每盒90元。" +
            "如果每星期家裏必須消費衛生紙2包，泡麵8包，兩公升裝的鮮奶2罐，土雞蛋一盒。" +
            "請問媽媽一個星期花在超市購買這些民生用品的費用需要多少錢？",//16
            "6×(-125)-6×(-25)",//17
            "(-12)×43+(-12)×57",//18
            "(-168)÷[24+(-28)]",//19
            "8-(-6)×4+28÷[(-7)+3]",//20
            "[(-18)+27÷(-3)]×7+6-[23×(-2)-(-8)]",//21
            "[21-(-15)÷3]×7+(-37)",//22
            "(-80)÷(-16)+[(-3)-(-4)]",//23
            "試問宅急便公司郵寄小型包裹，每件不超過3公斤，郵費一律100元，超過3公斤則以一公斤計算，" +
            "今郵寄11.876公斤重的東西，需郵資多少錢？",//24
            "26-260÷(-13)",//25
            "27×(-8)+136÷(-4)",//26
            "試問一艘潛水艇在海平面下方172公尺處發射一枚魚雷，此魚雷以每秒14公尺的速率上升，經過12秒後爆炸，" +
            "則爆炸處位於海平面的上方或下方幾公尺處？",//27
            "39÷[(-3)+6]+(-8)×6+|(-6)×10-1|=",//28
            "|72÷(-12)+(-12)|-4×(-9)÷[36÷(-12)+(-6)]",//29
            "{33+6×[9-(-51)÷(-3)]}-|2×(-12)+24|÷48",//30
    };
    private String[] exerciseAnswer = {
            "0",//1
            "0",//2
            "0",//3
            "0",//4
            "0",//5
            "0",//6
            "-112",//7
            "-27",//8
            "-108",//9
            "78",//10
            "-640",//11
            "12",//12
            "-37",//13
            "-5",//14
            "45",//15
            "516",//16
            "-600",//17
            "-1200",//18
            "42",//19
            "25",//20
            "-145",//21
            "145",//22
            "6",//23
            "235",//24
            "46",//25
            "-182",//26
            "-4",//27
            "26",//28
            "14",//29
            "-15",//30
    };



    MathExerciseData(){

        //if exerciseContent's count equal exerciseAnswer's count and are not empty
        if(exerciseContent.length == exerciseNumber && exerciseAnswer.length == exerciseNumber && exerciseNumber > 0){
            dataIsEmpty = false;
        }else {
            exerciseContent = null;
            exerciseAnswer = null;
            dataIsEmpty = true;
        }
    }

    public boolean isEmpty(){
        return dataIsEmpty;
    }

    public int getExerciseNumber(){
        return exerciseNumber;
    }

    public String[] getExerciseContent(){
        return exerciseContent;
    }

    public String[] getExerciseAnswer(){
        return exerciseAnswer;
    }
}
