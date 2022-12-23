package com.lavdevapp.MyCalc.models;

import java.util.ArrayList;

public class ScreenLineBuilder {
    private StringBuilder calculationsHistory;
    private final ArrayList<String> currentCalculations;
    private String position = "";
    private final CalculatorRPN calculator;
    private final String multiplySymbol = Character.toString((char) 215);
    private final String divideSymbol = Character.toString((char) 247);

    public ScreenLineBuilder() {
        calculationsHistory = new StringBuilder();
        currentCalculations = new ArrayList<>();
        calculator = new CalculatorRPN();
    }

    public void addDistinct(String symbol) {
        if (symbol.equals("*")) {
            currentCalculations.add(" " + multiplySymbol + " ");
        } else if (symbol.equals("/")) {
            currentCalculations.add(" " + divideSymbol + " ");
        } else {
            currentCalculations.add(" " + symbol + " ");
        }
//        setCalculations();
    }

        public void addPortion(String symbol) {
        currentCalculations.add(symbol);
//        setCalculations();
    }

    public void addAnswer(String answer) {
        currentCalculations.add("\n");
        currentCalculations.add(" = ");
        currentCalculations.add(answer);
        currentCalculations.add("\n");
//            setCalculations();
        calculationsHistory.append(collectBuffer());
        currentCalculations.clear();
    }

    public void delete() {
        currentCalculations.remove(currentCalculations.size() - 1);
//        setCalculations();
    }

    public void deletePortion() {
        String element;
        if (currentCalculations.size() < 2) {
            element = currentCalculations.get(0);
            if (element.length() < 2) {
                currentCalculations.clear();
            } else {
                currentCalculations.set(0, element.substring(0, element.length() - 1));
            }
        } else {
            element = currentCalculations.get(currentCalculations.size() - 1);
            if (element.length() < 2) {
                currentCalculations.remove(currentCalculations.size() - 1);
            } else {
                currentCalculations.set(currentCalculations.size() - 1, element.substring(0, element.length() - 1));
            }
        }
//        setCalculations();
    }

//    private void setCalculations() {
////        int lastIndexEquals = calculations.lastIndexOf("=");
////        if (lastIndexEquals != -1) {
////            calculations = calculations + collectBuffer();
////        } else {
////            calculations = collectBuffer();
////        }
//        calculations = calculations + bufferGetLast();
//    }

    public String getCalculations() {
//        String answerLineRegex = "= -?[0-9]+\\.?([0-9]+)?";
//        return setLineBreaks(calculations, answerLineRegex);
        return calculationsHistory + collectBuffer();
    }

//    private String setLineBreaks(String text, String regex) {
//        StringBuffer buffer = new StringBuffer();
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(text);
//        while (matcher.find()) {
//            matcher.appendReplacement(buffer, "\n" + matcher.group() + "\n");
//        }
//        matcher.appendTail(buffer);
//        return buffer.toString();
//    }

    public void setPosition(String text) {
        position = text;
    }

    public String getPosition() {
        return position
                .replaceAll("\\*", multiplySymbol)
                .replaceAll("/", divideSymbol)
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll("=", "");
    }

    public void clearAllCalculations() {
        calculationsHistory = new StringBuilder();
        currentCalculations.clear();
        setPosition("");
//        setCalculations();
    }

    private String collectBuffer() {
        StringBuilder text = new StringBuilder();
        for (String element : currentCalculations) {
            text.append(element);
        }
        return text.toString();
    }

    private String bufferGetLast() {
        if (currentCalculations.isEmpty()) {
            return "";
        } else {
            if (currentCalculations.contains(" = ")) {
                StringBuilder text = new StringBuilder();
                int equalsIndex = currentCalculations.lastIndexOf(" = ");
                text.append("\n");
                for (int index = equalsIndex; index < currentCalculations.size(); index++) {
                    text.append(currentCalculations.get(index));
                }
                text.append("\n");
                return text.toString();
            } else {
                return currentCalculations.get(currentCalculations.size() - 1);
            }
        }
    }

    public CalculatorRPN.Answer calculateAnswer() {
        return calculator.tryCalculate(collectBuffer());
    }
}
