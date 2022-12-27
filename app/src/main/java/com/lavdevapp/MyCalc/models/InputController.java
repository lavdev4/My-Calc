package com.lavdevapp.MyCalc.models;

import com.lavdevapp.MyCalc.R;

import java.util.Stack;

public class InputController {
    private String block = "";
    private final Stack<String> blockStack;
    private CalculatorRPN.Answer answer = null;
    private int leftBraceCount = 0;
    private int rightBraceCount = 0;
    private final ScreenLineBuilder lineBuilder;
    private final CalculatorRPN calculator;

    private final String anyNumber = "-?[0-9]+\\.?([0-9]+)?";
    private final String multiplyOrDivide = "[*/]";
    private final String allSigns = "[+\\-*/]";

    public InputController(ScreenLineBuilder lineBuilder) {
        this.lineBuilder = lineBuilder;
        this.blockStack = new Stack<>();
        this.calculator = new CalculatorRPN();
    }

    public void init(String symbol) {
        if (validate(symbol)) {
            checkForContinuingInput(symbol);
            checkForSignReplacement(symbol);
            addSymbol(symbol);
        }
    }

    private void checkForContinuingInput(String symbol) {
        if (answer != null && answer.type == CalculatorRPN.Answer.ANSWER_OK) {
            if (blockStack.empty() && block.isEmpty() && symbol.matches(allSigns)) {
                char[] splitAnswer = answer.content.toCharArray();
                for (char element : splitAnswer) {
                    addSymbol(String.valueOf(element));
                }
            }
            answer = null;
        }
    }

    private void checkForSignReplacement(String symbol) {
        if (!blockStack.empty() && block.isEmpty()) {
            if (symbol.matches("[+*/]") && blockStack.peek().matches(allSigns)) {
                if (!(symbol.equals("-") && blockStack.peek().equals("-"))) {
                    remove();
                }
            }
        }
    }

    private void addSymbol(String symbol) {
        if (symbol.matches("[+*/]")) {
            addPlusMinusDivide(symbol);
        } else if (symbol.equals("-")) {
            addMinus(symbol);
        } else if (symbol.equals("(")) {
            addLeftBrace(symbol);
        } else if (symbol.equals(")")) {
            addRightBrace(symbol);
        } else if (symbol.equals("=")) {
            calculateAnswer();
        } else {
            addNumber(symbol);
        }
        setCurrentPosition();
    }

    private void addNumber(String symbol) {
        block += symbol;
        lineBuilder.addPortion(symbol);
    }

    private void addPlusMinusDivide(String symbol) {
        if (blockStack.empty()) {
            blockStack.push(block);
            blockStack.push(symbol);
            block = "";
            lineBuilder.addDistinct(symbol);
        } else if (!blockStack.empty()) {
            if (!block.isEmpty()) {
                blockStack.push(block);
            }
            blockStack.push(symbol);
            block = "";
            lineBuilder.addDistinct(symbol);
        }
    }

    private void addMinus(String symbol) {
        if (isNegativeNumber()) {
            addNumber(symbol);
        } else {
            if (blockStack.empty()) {
                blockStack.push(block);
                blockStack.push(symbol);
                block = "";
                lineBuilder.addDistinct(symbol);
            } else if (!blockStack.empty()) {
                if (!block.isEmpty()) {
                    blockStack.push(block);
                }
                blockStack.push(symbol);
                block = "";
                lineBuilder.addDistinct(symbol);
            }
        }
    }

    private void addLeftBrace(String symbol) {
        if (block.equals("-")) {
            block = "";
            blockStack.push("-");
        }
        blockStack.push(symbol);
        lineBuilder.addPortion(symbol);
        leftBraceCount++;
    }

    private void addRightBrace(String symbol) {
        if (!block.isEmpty()) {
            blockStack.push(block);
        }
        blockStack.push(symbol);
        block = "";
        lineBuilder.addPortion(symbol);
        rightBraceCount++;
    }

    private void calculateAnswer() {
        if (!block.isEmpty()){
            blockStack.push(block);
        }
        answer = calculator.tryCalculate(prepareExpression());
        if (answer.type == CalculatorRPN.Answer.ANSWER_OK) {
            lineBuilder.addAnswer(answer.content);
            clearCurrentCalculations();
        }
    }

    public void remove() {
        if (blockStack.empty() && block.isEmpty()) {
            return;
        } else {
            if (!blockStack.empty() && block.isEmpty()) {
                removeInBlock();
            } else {
                removePartly();
            }
            lineBuilder.remove();
        }
        setCurrentPosition();
    }

    private void removeInBlock() {
        String toBeRemoved = blockStack.peek();
        if (toBeRemoved.equals("(")) {
            leftBraceCount--;
        } else if (toBeRemoved.equals(")")) {
            rightBraceCount--;
        }
        blockStack.pop();
        String staysBeforeRemovable = blockStack.peek();
        if (staysBeforeRemovable.matches(anyNumber)) {
            block = staysBeforeRemovable;
            blockStack.pop();
        }
    }

    private void removePartly() {
        block = block.substring(0, block.length() - 1);
    }

    private void setCurrentPosition() {
        if (answer != null) {
            lineBuilder.setCurrentPosition(answer.content);
            if (answer.type != CalculatorRPN.Answer.ANSWER_OK) {
                answer = null;
            }
        } else if (!block.isEmpty()) {
            lineBuilder.setCurrentPosition(block);
        } else if (!blockStack.empty()) {
            lineBuilder.setCurrentPosition(blockStack.peek());
        } else {
            lineBuilder.setCurrentPosition("");
        }
    }

    private void clearCurrentCalculations() {
        blockStack.clear();
        block = "";
        leftBraceCount = 0;
        rightBraceCount = 0;
        lineBuilder.clearCurrentCalculations();
    }

    public void clearAllCalculations() {
        blockStack.clear();
        block = "";
        answer = null;
        leftBraceCount = 0;
        rightBraceCount = 0;
        lineBuilder.clearAllCalculations();
    }

    private boolean isNegativeNumber() {
        if (block.isEmpty() && blockStack.empty()) {
            return true;
        } else if (!block.isEmpty() && blockStack.empty()) {
            return false;
        } else if (blockStack.peek().matches(anyNumber)) {
            return false;
        } else if (blockStack.peek().equals(")")) {
            return false;
        } else {
            return block.isEmpty();
        }
    }

    private String prepareExpression() {
        return String.join(" ", blockStack);
    }

    private boolean validate(String symbol) {
        if (symbol.matches(allSigns)) {
            if (!block.isEmpty()) {
                if (block.equals("-")) {
                    return false;
                }
                if (block.endsWith(".")) {
                    return false;
                }
            }
        }
        if (symbol.matches(multiplyOrDivide) || symbol.equals("+")) {
            if (block.isEmpty()) {
                if (blockStack.empty()) {
                    if (answer == null) {
                        return false;
                    }
                }
                if (!blockStack.empty()) {
                    if (blockStack.peek().equals("(")) {
                        return false;
                    }
                }
            }
        } else if (symbol.equals("-")) {
            if (!block.isEmpty()) {
                if (block.equals("-")) {
                    return false;
                }
            }
        } else if (symbol.equals(".")) {
            if (block.isEmpty()) {
                if (blockStack.empty()) {
                    return false;
                }
                if (!blockStack.empty()) {
                    if (blockStack.peek().contains(".")) {
                        return false;
                    }
                    if (blockStack.peek().matches(allSigns)) {
                        return false;
                    }
                    if (blockStack.peek().equals("(")) {
                        return false;
                    }
                    if (blockStack.peek().equals(")")) {
                        return false;
                    }
                }
            }
            if (!block.isEmpty()) {
                if (block.equals("-")) {
                    return false;
                }
                if (block.contains(".")) {
                    return false;
                }
                if (!blockStack.empty()) {
                    if (blockStack.peek().contains(".")) {
                        return false;
                    }
                }
            }
        } else if (symbol.equals("(")) {
            if (block.equals("-")) {
                return false;
            }
            if (blockStack.empty()) {
                if (block.matches(anyNumber)) {
                    return false;
                }
            }
            if (!blockStack.empty()) {
                if (blockStack.peek().matches(anyNumber)) {
                    return false;
                }
                if (block.matches(anyNumber)) {
                    return false;
                }
                if (blockStack.peek().equals("(")) {
                    return false;
                }
                if (blockStack.peek().equals(")")) {
                    return false;
                }
            }
        } else if (symbol.equals(")")) {
            if (rightBraceCount == leftBraceCount) {
                return false;
            }
            if (!blockStack.empty()) {
                if (blockStack.search("(") < 3) {
                    return false;
                }
            }
            if (block.isEmpty()) {
                if (blockStack.empty()) {
                    return false;
                }
                if (!blockStack.empty()) {
                    if (blockStack.peek().equals("(")) {
                        return false;
                    }
                    if (blockStack.peek().matches(allSigns)) {
                        return false;
                    }
                }
            }
        } else if (symbol.equals("=")) {
            if (blockStack.size() < 2) {
                return false;
            }
            if (block.equals("-")) {
                return false;
            }
            if (!block.isEmpty()) {
                if (blockStack.empty()) {
                    return false;
                }
                if (block.endsWith(".")) {
                    return false;
                }
            }
            if (block.isEmpty()) {
                if (blockStack.empty()) {
                    return false;
                }
                if (!blockStack.empty()) {
                    if (blockStack.peek().equals("(")) {
                        return false;
                    }
                    if (blockStack.peek().equals("=")) {
                        return false;
                    }
                    if (blockStack.peek().matches(allSigns)) {
                        return false;
                    }
                    if (blockStack.search("=") == 2) {
                        return false;
                    }
                }
            }
            if (!blockStack.empty()) {
                if (blockStack.contains("(") && !blockStack.contains(")")) {
                    return false;
                }
            }
            if (leftBraceCount != rightBraceCount) {
                return false;
            }
        } else if (symbol.matches(anyNumber)) {
            if (block.equals("0") || block.equals("-0")) {
                return false;
            }
            if (!blockStack.empty()) {
                if (blockStack.peek().equals(")")) {
                    return false;
                }
            }
        }
        return true;
    }
}


