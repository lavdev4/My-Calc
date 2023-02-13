package com.lavdevapp.MyCalc.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.Stack;

public class InputController {
    private String block = "";
    private final Stack<String> blockStack;
    private CalculatorRPN.Answer answer = null;
    private int leftBraceCount = 0;
    private int rightBraceCount = 0;
    private final ScreenLineBuilder lineBuilder;
    private final transient CalculatorRPN calculator;
    private final transient InputValueValidator validator;

    private final transient String ANY_NUMBER_REGEX = "-?[0-9]+\\.?([0-9]+)?";
    private final transient String ALL_SIGNS_REGEX = "[+\\-*/]";
    private final transient String ALL_SIGNS_NON_MINUS_REGEX = "[+*/]";

    public InputController() {
        this.lineBuilder = new ScreenLineBuilder();
        this.blockStack = new Stack<>();
        this.calculator = new CalculatorRPN();
        this.validator = new InputValueValidator();
    }

    public LiveData<String> getCalculationsHistory() {
        return lineBuilder.getCalculationHistory();
    }

    public LiveData<String> getCurrentPosition() {
        return lineBuilder.getCurrentPosition();
    }

    public boolean init(String symbol) {
        boolean validated = validator.validate(symbol);
        if (validated) {
            checkForContinuingInput(symbol);
            checkForSignReplacement(symbol);
            addSymbol(symbol);
        }
        return validated;
    }

    private void checkForContinuingInput(String symbol) {
        if (answer != null && answer.type == CalculatorRPN.Answer.ANSWER_OK) {
            if (blockStack.empty() && block.isEmpty() && symbol.matches(ALL_SIGNS_REGEX)) {
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
            if (symbol.matches(ALL_SIGNS_NON_MINUS_REGEX) && blockStack.peek().matches(ALL_SIGNS_REGEX)) {
                if (!(symbol.equals("-") && blockStack.peek().equals("-"))) {
                    remove();
                }
            }
        }
    }

    private void addSymbol(String symbol) {
        if (symbol.matches(ALL_SIGNS_NON_MINUS_REGEX)) {
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
        setCalculations();
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
        if (!block.isEmpty()) {
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
        setCalculations();
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
        if (staysBeforeRemovable.matches(ANY_NUMBER_REGEX)) {
            block = staysBeforeRemovable;
            blockStack.pop();
        }
    }

    private void removePartly() {
        block = block.substring(0, block.length() - 1);
    }

    private void setCalculations() {
        lineBuilder.setCalculationHistory();
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
        } else if (blockStack.peek().matches(ANY_NUMBER_REGEX)) {
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

    @NonNull
    @Override
    public String toString() {
        return "block: " + block + "\n" +
                "blockStack: " + blockStack + "\n" +
                "answer: " + (answer == null ? "null" : answer.content) + "\n" +
                "leftBrace: " + leftBraceCount + "\n" +
                "rightBrace: " + rightBraceCount + "\n" +
                "calculatorRPN: " + (calculator == null ? "null" : calculator.toString()) + "\n" +
                "inputValueValidator: " + (validator == null ? "null" : validator.toString()) + "\n" +
                "------LineBuilder: " + "\n" + lineBuilder;
    }

    private class InputValueValidator {
        private static final int CASE_NUMBERS = 0;
        private static final int CASE_SIGNS = 1;
        private static final int CASE_DECIMAL_SEPARATOR = 2;
        private static final int CASE_LEFT_BRACE = 3;
        private static final int CASE_RIGHT_BRACE = 4;
        private static final int CASE_EQUALS = 5;

        public boolean validate(String input) {
            switch (getCase(input)) {
                case CASE_NUMBERS:
                    return validateNumber(input);
                case CASE_SIGNS:
                    return validateSigns(input);
                case CASE_DECIMAL_SEPARATOR:
                    return validateDecimalSeparator(input);
                case CASE_LEFT_BRACE:
                    return validateLeftBrace(input);
                case CASE_RIGHT_BRACE:
                    return validateRightBrace(input);
                case CASE_EQUALS:
                    return validateEquals(input);
                default:
                    throw new RuntimeException("Unknown validation case");
            }
        }

        private int getCase(String symbol) {
            if (symbol.matches(ANY_NUMBER_REGEX)) return CASE_NUMBERS;
            else if (symbol.matches(ALL_SIGNS_REGEX)) return CASE_SIGNS;
            else if (symbol.equals(".")) return CASE_DECIMAL_SEPARATOR;
            else if (symbol.equals("(")) return CASE_LEFT_BRACE;
            else if (symbol.equals(")")) return CASE_RIGHT_BRACE;
            else if (symbol.equals("=")) return CASE_EQUALS;
            else throw new RuntimeException("Unknown validation case");
        }

        private boolean validateNumber(String number) {
            if (block.equals("0") || block.equals("-0")) return false;
            if (!blockStack.empty()) {
                if (blockStack.peek().equals(")")) return false;
            }
            return true;
        }

        private boolean validateSigns(String sign) {
            if (!block.isEmpty()) {
                if (block.equals("-")) return false;
                if (block.endsWith(".")) return false;
            }
            if (sign.matches(ALL_SIGNS_NON_MINUS_REGEX)) {
                return validateSignsNonMinus(sign);
            }
            if (sign.equals("-")) {
                return validateMinus(sign);
            }
            return true;
        }

        private boolean validateSignsNonMinus(String sign) {
            if (block.isEmpty()) {
                if (blockStack.empty()) {
                    if (answer == null) return false;
                }
                if (!blockStack.empty()) {
                    if (blockStack.peek().equals("(")) return false;
                }
            }
            return true;
        }

        private boolean validateMinus(String sign) {
            if (!block.isEmpty()) {
                if (block.equals("-")) return false;
            }
            return true;
        }

        private boolean validateDecimalSeparator(String separator) {
            if (block.isEmpty()) {
                if (blockStack.empty()) return false;
                if (!blockStack.empty()) {
                    if (blockStack.peek().contains(".")) return false;
                    if (blockStack.peek().matches(ALL_SIGNS_REGEX)) return false;
                    if (blockStack.peek().equals("(")) return false;
                    if (blockStack.peek().equals(")")) return false;
                }
            }
            if (!block.isEmpty()) {
                if (block.equals("-")) return false;
                if (block.contains(".")) return false;
                if (!blockStack.empty()) {
                    if (blockStack.peek().contains(".")) return false;
                }
            }
            return true;
        }

        private boolean validateLeftBrace(String symbol) {
            if (block.equals("-")) return false;
            if (blockStack.empty()) {
                if (block.matches(ANY_NUMBER_REGEX)) return false;
            }
            if (!blockStack.empty()) {
                if (blockStack.peek().matches(ANY_NUMBER_REGEX)) return false;
                if (block.matches(ANY_NUMBER_REGEX)) return false;
                if (blockStack.peek().equals("(")) return false;
                if (blockStack.peek().equals(")")) return false;
            }
            return true;
        }

        private boolean validateRightBrace(String symbol) {
            if (rightBraceCount == leftBraceCount) return false;
            if (!blockStack.empty()) {
                if (blockStack.search("(") < 3) return false;
            }
            if (block.isEmpty()) {
                if (blockStack.empty()) return false;
                if (!blockStack.empty()) {
                    if (blockStack.peek().equals("(")) return false;
                    if (blockStack.peek().matches(ALL_SIGNS_REGEX)) return false;
                }
            }
            return true;
        }

        private boolean validateEquals(String symbol) {
            if (blockStack.size() < 2) return false;
            if (block.equals("-")) return false;
            if (!block.isEmpty()) {
                if (blockStack.empty()) return false;
                if (block.endsWith(".")) return false;
            }
            if (block.isEmpty()) {
                if (blockStack.empty()) return false;
                if (!blockStack.empty()) {
                    if (blockStack.peek().equals("(")) return false;
                    if (blockStack.peek().equals("=")) return false;
                    if (blockStack.peek().matches(ALL_SIGNS_REGEX)) return false;
                    if (blockStack.search("=") == 2) return false;
                }
            }
            if (!blockStack.empty()) {
                if (blockStack.contains("(") && !blockStack.contains(")")) return false;
            }
            if (leftBraceCount != rightBraceCount) return false;
            return true;
        }
    }
}


