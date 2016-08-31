package com.example.demonickrace.easytest;

/**
 * Created by demonickrace on 16/6/19.
 */
public class MathExerciseError {

    /* define by customer
    private final String[] integerArithmeticErrorMsg = {
            "不知從式子的括號開始計算", //1
            "四則運算規則的錯誤(不知先乘除後加減)", //2
            "含絕對值四則運算的錯誤" //3
    };

    private final String[] integerMultipyAndDivideErrorMsg = {
            "不知零與任何一整數的乘積為零", //1
            "不知零除以一個不為零的整數其結果為零",//2
            "不熟悉九九乘法表，乘除法錯誤", //3
            "不熟悉乘法運算規則：交換率、結合率", //4
            "不懂除法算法，誤用乘法計算", //5
            "不熟悉運算規則：分配律", //6
            "使用乘除法的運算規則後忘記加或去除負號" //7
    };

    //index is between 1 ~ 3
    public String getIntegerArithmeticErrorMsg(int index){
        if(index > 0 && index <= integerArithmeticErrorMsg.length)
            return integerArithmeticErrorMsg[index - 1];
        else
            return null;
    }

    //index is between 1 ~ 7
    public String getIntegerMultipyAndDivideErrorMsg(int index){
        if(index > 0 && index <= integerMultipyAndDivideErrorMsg.length)
            return integerMultipyAndDivideErrorMsg[index - 1];
        else
            return null;
    }
    */

    //define by Demonickrace
    private final String[] errorMsg = {
        "不知零的乘除法運算結果為零", //1.   1~6
        "乘除法運算錯誤或正負號錯誤",//2.    7~15
        "不熟悉乘法運算規則: 交換律,結合率,分配率",//3.   16~18
        "四則運算規則錯誤: 括號內部先做,先乘除後加減",//4.  19~27
        "含絕對值四則運算的錯誤"//5.   28~30
    };

    public String getErrorMsg(int index){
        if(index > 0 && index <= errorMsg.length)
            return errorMsg[index - 1];
        else
            return null;
    }

    /*
        題型錯誤解析法則
        1~6
            不知零與任何一整數的乘機為零
            不知零除以一個不為零的整數其結果為零
        7~15
            不熟悉九九乘法表，乘除法錯誤
            不懂除法算法，誤用乘法計算
            使用乘除法的運算規則後忘記加或去除負號
        16~18
            不熟悉乘法運算規則：交換率、結合率
            不熟悉運算規則：分配律
        19~27
            不知從式子的括號開始計算
            四則運算規則的錯誤(不知先乘除後加減)
        28~30
            含絕對值四則運算的錯誤
     */

    public String getAnalysisErrorMsg(int number ,String answer) {
        String msg = "";
        switch (number) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                //customer defination
                //不知零與任何一整數的乘機為零
                //msg = getIntegerMultipyAndDivideErrorMsg(1);

                //use DemonicKrace defination
                msg = getErrorMsg(1);
                break;
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                //customer defination
                //不熟悉九九乘法表，乘除法錯誤
                //msg = getIntegerMultipyAndDivideErrorMsg(3);

                //use DemonicKrace defination
                msg = getErrorMsg(2);
                break;
            case 16:
            case 17:
            case 18:
                //customer defination
                //不熟悉乘法運算規則：交換率、結合率
                //msg = getIntegerMultipyAndDivideErrorMsg(4);

                //use DemonicKrace defination
                msg = getErrorMsg(3);
                break;
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                //customer defination
                //四則運算規則的錯誤(不知先乘除後加減)
                //msg = getIntegerArithmeticErrorMsg(2);

                //use DemonicKrace defination
                msg = getErrorMsg(4);
                break;
            case 28:
            case 29:
            case 30:
                //customer defination
                //含絕對值四則運算的錯誤
                //msg = getIntegerArithmeticErrorMsg(3);

                //use DemonicKrace defination
                msg = getErrorMsg(5);
                break;
        }

        return msg;
    }
}
