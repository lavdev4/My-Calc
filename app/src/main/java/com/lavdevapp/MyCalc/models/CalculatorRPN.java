package com.lavdevapp.MyCalc.models;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

public class CalculatorRPN {
    private final HashMap<String, Integer> priorityComputation = new HashMap<>();
    private final String signsRegex = "[+\\-*/]";
    private final String integerNumberRegex = "-?[0-9]+";
    private final String anyNumberRegex = "-?[0-9]+\\.?([0-9]+)?";
    private final DecimalFormat calculationFormat;
    public final DecimalFormat answerFormat;

    public CalculatorRPN() {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        calculationFormat = new DecimalFormat("#.##########", dfs);
        answerFormat = new DecimalFormat("#.######", dfs);

        priorityComputation.put("+", 1);
        priorityComputation.put("-", 1);
        priorityComputation.put("*", 2);
        priorityComputation.put("/", 2);
    }

    private Stack<String> createCountStack(String expression) {
        Stack<String> mainStack = new Stack<>();
        Stack<String> buffer = new Stack<>();

        String[] expressionElements = expression.split(" ");
        for (String symbol : expressionElements) {
            if (symbol.matches(anyNumberRegex)) {
                mainStack.push(symbol);
            } else if (symbol.matches(signsRegex)) {
                try {
                    while (priorityComputation.get(buffer.peek()) >= priorityComputation.get(symbol)) {
                        mainStack.push(buffer.pop());
                    }
                    buffer.push(symbol);
                } catch (EmptyStackException | NullPointerException e) {
                    buffer.push(symbol);
                }
            } else if (symbol.equals("(")) {
                buffer.push(symbol);
            } else if (symbol.equals(")")) {
                while (!buffer.peek().equals("(")) {
                    mainStack.push(buffer.pop());
                }
                buffer.pop();
            }
        }
        while (!buffer.empty()) {
            mainStack.push(buffer.pop());
        }
        buffer.clear();
        while (!mainStack.empty()) {
            buffer.push(mainStack.pop());
        }
        return buffer;
    }

    private String calculate(String expression) throws ArithmeticException {
        Stack<String> mainStack;
        Stack<Double> buffer = new Stack<>();

        mainStack = createCountStack(expression);

        while (!mainStack.empty()) {
            String stackSymbol = mainStack.pop();
            if (stackSymbol.contains(".")) {
                buffer.push(Double.parseDouble(stackSymbol));
            } else if (!stackSymbol.contains(".") && stackSymbol.matches(integerNumberRegex)) {
                buffer.push(Double.parseDouble(stackSymbol + ".0"));
            } else if (stackSymbol.matches(signsRegex)) {
                Double b = Double.parseDouble(calculationFormat.format(buffer.pop()));
                Double a = Double.parseDouble(calculationFormat.format(buffer.pop()));
                switch (stackSymbol) {
                    case "+":
                        buffer.push(Double.parseDouble(calculationFormat.format(a + b)));
                        break;
                    case "-":
                        buffer.push(Double.parseDouble(calculationFormat.format(a - b)));
                        break;
                    case "*":
                        buffer.push(Double.parseDouble(calculationFormat.format(a * b)));
                        break;
                    case "/":
                        if (b == 0.0) {
                            throw new ArithmeticException();
                        }
                        buffer.push(Double.parseDouble(calculationFormat.format(a / b)));
                        break;
                }
            }
        }
        return answerFormat.format(buffer.pop());
    }

    public Answer tryCalculate(String expression) {
        try {
            String answer = calculate(expression);
            return new Answer(Answer.ANSWER_OK, answer);
        } catch (ArithmeticException arithmeticException) {
            return new Answer(Answer.ANSWER_ARITHMETIC_ERROR, "Arithmetic error.");
        } catch (Exception exception) {
            return new Answer(Answer.ANSWER_ERROR, "Error.");
        }
    }

    public static class Answer {
        public int type;
        public String content;

        public static int ANSWER_OK = 0;
        public static int ANSWER_ARITHMETIC_ERROR = 1;
        public static int ANSWER_ERROR = 2;

        public Answer(int type, String content) {
            this.type = type;
            this.content = content;
        }
    }
}
