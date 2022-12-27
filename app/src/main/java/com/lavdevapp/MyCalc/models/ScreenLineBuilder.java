package com.lavdevapp.MyCalc.models;

import java.util.ArrayList;

public class ScreenLineBuilder {
    private StringBuilder calculationsHistory;
    private final ArrayList<String> currentCalculations;
    private String position = "";
    private final String multiplySymbol = Character.toString((char) 215);
    private final String divideSymbol = Character.toString((char) 247);

    public ScreenLineBuilder() {
        calculationsHistory = new StringBuilder();
        currentCalculations = new ArrayList<>();
    }

    public void addDistinct(String symbol) {
        if (symbol.equals("*")) {
            currentCalculations.add(" " + multiplySymbol + " ");
        } else if (symbol.equals("/")) {
            currentCalculations.add(" " + divideSymbol + " ");
        } else {
            currentCalculations.add(" " + symbol + " ");
        }
    }

    public void addPortion(String symbol) {
        currentCalculations.add(symbol);
    }

    public void addAnswer(String answer) {
        currentCalculations.add("\n");
        currentCalculations.add(" = ");
        currentCalculations.add(answer);
        currentCalculations.add("\n");
        calculationsHistory.append(getCurrentCalculations());
    }

    public void remove() {
        currentCalculations.remove(currentCalculations.size() - 1);
    }

    private String getCurrentCalculations() {
        StringBuilder sb = new StringBuilder();
        for (String element : currentCalculations) {
            sb.append(element);
        }
        return sb.toString();
    }

    public String getAllCalculations() {
        return calculationsHistory + getCurrentCalculations();
    }

    public void clearCurrentCalculations() {
        currentCalculations.clear();
    }

    public void clearAllCalculations() {
        calculationsHistory = new StringBuilder();
        currentCalculations.clear();
        setCurrentPosition("");
    }

    public void setCurrentPosition(String text) {
        position = text;
    }

    public String getCurrentPosition() {
        return position
                .replaceAll("\\*", multiplySymbol)
                .replaceAll("/", divideSymbol)
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll("=", "");
    }
}
