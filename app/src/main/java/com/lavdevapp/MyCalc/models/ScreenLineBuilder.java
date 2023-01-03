package com.lavdevapp.MyCalc.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class ScreenLineBuilder {
    private final MutableLiveData<String> calculationsHistory;
    private final MutableLiveData<String> currentPosition;
    private StringBuilder calculationsHistoryBuilder;
    private final ArrayList<String> currentCalculationsBuilder;

    private final transient String multiplySymbol = Character.toString((char) 215);
    private final transient String divideSymbol = Character.toString((char) 247);

    public ScreenLineBuilder() {
        calculationsHistory = new MutableLiveData<>("");
        currentPosition = new MutableLiveData<>("");
        calculationsHistoryBuilder = new StringBuilder();
        currentCalculationsBuilder = new ArrayList<>();
    }

    public void addDistinct(String symbol) {
        if (symbol.equals("*")) {
            currentCalculationsBuilder.add(" " + multiplySymbol + " ");
        } else if (symbol.equals("/")) {
            currentCalculationsBuilder.add(" " + divideSymbol + " ");
        } else {
            currentCalculationsBuilder.add(" " + symbol + " ");
        }
    }

    public void addPortion(String symbol) {
        currentCalculationsBuilder.add(symbol);
    }

    public void addAnswer(String answer) {
        currentCalculationsBuilder.add("\n");
        currentCalculationsBuilder.add(" = ");
        currentCalculationsBuilder.add(answer);
        currentCalculationsBuilder.add("\n");
        calculationsHistoryBuilder.append(getCurrentCalculations());
    }

    public void remove() {
        currentCalculationsBuilder.remove(currentCalculationsBuilder.size() - 1);
    }

    private String getCurrentCalculations() {
        StringBuilder sb = new StringBuilder();
        for (String element : currentCalculationsBuilder) {
            sb.append(element);
        }
        return sb.toString();
    }

    public void setCalculationHistory() {
        calculationsHistory.setValue(calculationsHistoryBuilder + getCurrentCalculations());
    }

    public LiveData<String> getCalculationHistory() {
        return calculationsHistory;
    }

    public void clearCurrentCalculations() {
        currentCalculationsBuilder.clear();
    }

    public void clearAllCalculations() {
        calculationsHistoryBuilder = new StringBuilder();
        currentCalculationsBuilder.clear();
        setCurrentPosition("");
        calculationsHistory.setValue("");
    }

    public void setCurrentPosition(String text) {
        currentPosition.setValue(text
                .replaceAll("\\*", multiplySymbol)
                .replaceAll("/", divideSymbol)
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll("=", ""));
    }

    public LiveData<String> getCurrentPosition() {
        return currentPosition;
    }

    @NonNull
    @Override
    public String toString() {
        return "calculationHistory" + calculationsHistory + "\n" +
                "currentPosition" + currentPosition + "\n" +
                "calculationHistoryBuilder: " + calculationsHistoryBuilder + "\n" +
                "currentCalculationsBuilder: " + currentCalculationsBuilder;
    }
}
