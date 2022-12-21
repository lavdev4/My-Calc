package com.lavdevapp.MyCalc.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lavdevapp.MyCalc.R;

public class CalcViewModel extends ViewModel {
    private MutableLiveData<String> screenText;
    private MutableLiveData<String> answerScreenText;
    private final InputController inputController;
    private final ScreenLineBuilder lineBuilder;

    public CalcViewModel() {
        this.lineBuilder = new ScreenLineBuilder();
        this.inputController = new InputController(lineBuilder);
    }

    public LiveData<String> getScreenText() {
        if (screenText == null) {
            screenText = new MutableLiveData<>("");
        }
        return screenText;
    }

    public LiveData<String> getAnswerScreenText() {
        if (answerScreenText == null) {
            answerScreenText = new MutableLiveData<>("");
        }
        return answerScreenText;
    }

    public void onButtonPressed(int buttonId) {
        if (buttonId == R.id.oneButton) {
            inputController.init("1");
        } else if (buttonId == R.id.twoButton) {
            inputController.init("2");
        } else if (buttonId == R.id.threeButton) {
            inputController.init("3");
        } else if (buttonId == R.id.fourButton) {
            inputController.init("4");
        } else if (buttonId == R.id.fiveButton) {
            inputController.init("5");
        } else if (buttonId == R.id.sixButton) {
            inputController.init("6");
        } else if (buttonId == R.id.sevenButton) {
            inputController.init("7");
        } else if (buttonId == R.id.eightButton) {
            inputController.init("8");
        } else if (buttonId == R.id.nineButton) {
            inputController.init("9");
        } else if (buttonId == R.id.zeroButton) {
            inputController.init("0");
        } else if (buttonId == R.id.plusButton) {
            inputController.init("+");
        } else if (buttonId == R.id.minusButton) {
            inputController.init("-");
        } else if (buttonId == R.id.multiplyButton) {
            inputController.init("*");
        } else if (buttonId == R.id.divideButton) {
            inputController.init("/");
        } else if (buttonId == R.id.leftBraceButton) {
            inputController.init("(");
        } else if (buttonId == R.id.rightBraceButton) {
            inputController.init(")");
        } else if (buttonId == R.id.deleteButton) {
            inputController.delete();
        } else if (buttonId == R.id.commaButton) {
            inputController.init(".");
        } else if (buttonId == R.id.equalsButton) {
            inputController.init("=");
        } else if (buttonId == R.id.clearButton) {
            inputController.clear();
        }
        screenText.setValue(lineBuilder.getCalculations());
        answerScreenText.setValue(lineBuilder.getPosition());
    }
}
