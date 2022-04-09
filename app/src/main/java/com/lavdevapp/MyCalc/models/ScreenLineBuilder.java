package com.lavdevapp.MyCalc.models;

import java.util.ArrayList;

public class ScreenLineBuilder {
    private String screenLine;
    private String answerLine;
    private final CalculatorRPN calculator;
    private final ArrayList<String> buffer;
    private final String multiplySymbol = Character.toString((char) 215);
    private final String divideSymbol = Character.toString((char) 247);

    public ScreenLineBuilder() {
        buffer = new ArrayList<>();
        calculator = new CalculatorRPN();
    }

    public void addSymbol(String symbol) {
        if (symbol.equals("*")) {
            buffer.add(" " + multiplySymbol + " ");
        } else if (symbol.equals("/")) {
            buffer.add(" " + divideSymbol + " ");
        } else {
            buffer.add(" " + symbol + " ");
        }
        setLine();
    }

    public void addNumber(String symbol) {
        buffer.add(symbol);
        setLine();
    }

    public void deleteSymbol() {
        buffer.remove(buffer.size() - 1);
        setLine();
    }

    public void deleteNumber() {
        String element;
        if (buffer.size() < 2) {
            element = buffer.get(0);
            if (element.length() < 2) {
                clearLines();
            } else {
                buffer.set(0, element.substring(0, element.length() - 1));
            }
        } else {
            element = buffer.get(buffer.size() - 1);
            if (element.length() < 2) {
                buffer.remove(buffer.size() - 1);
            } else {
                buffer.set(buffer.size() - 1, element.substring(0, element.length() - 1));
            }
        }
        setLine();
    }

    private void setLine() {
        StringBuilder text = new StringBuilder();
        for (String element : buffer) {
            text.append(element);
        }
        screenLine = text.toString();
    }

    public String getLine() {
        return screenLine;
    }

    private void setAnswerLine(String answer) {
        answerLine = answer;
    }

    public String getAnswerLine() {
        return answerLine;
    }

    public void clearLines() {
        buffer.clear();
        setAnswerLine("");
        setLine();
    }

    public String calculateAnswer() {
        String answer = "";
        try {
            if (screenLine.contains("=")) {
                screenLine = screenLine.substring(screenLine.lastIndexOf("=") + 2);
            }
            answer = calculator.calculate(screenLine);
            setAnswerLine(answer);
        } catch (ArithmeticException e) {
            setAnswerLine("Div. by 0 error");
        }
        return answer;
    }
}
