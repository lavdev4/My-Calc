package com.lavdevapp.MyCalc.models;

import java.util.Stack;

public class InputController {
    private static int leftBraceCount = 0;
    private static int rightBraceCount = 0;
    private static String block = "";
    private static final Stack<String> blockStack = new Stack<>();
    private static CalculatorRPN.Answer answer = null;
    private final ScreenLineBuilder lineCollector;

    public InputController(ScreenLineBuilder lineCollector) {
        this.lineCollector = lineCollector;
    }

    public void init(String symbol) {
        if (InputValueValidator.validate(symbol)) {
            checkForContinuingInput(symbol);
            checkForSignReplacement(symbol);
            add(symbol);
        }
    }

    private void checkForContinuingInput(String symbol) {
        if (answer != null && answer.type == CalculatorRPN.Answer.ANSWER_OK) {
            if (blockStack.empty() && block.isEmpty() && symbol.matches("[+\\-*/]")) {
                char[] splitAnswer = answer.content.toCharArray();
                for (char element : splitAnswer) {
                    add(String.valueOf(element));
                }
            }
            answer = null;
        }
    }

    private void checkForSignReplacement(String symbol) {
        if (!blockStack.empty() && block.isEmpty()) {
            if (symbol.matches("[+*/]") && blockStack.peek().matches("[+\\-*/]")) {
                if (!(symbol.equals("-") && blockStack.peek().equals("-"))) {
                    delete();
                }
            }
        }
    }

    public void delete() {
        if (blockStack.empty() && block.isEmpty()) {
            return;
        } else {
            if (!blockStack.empty() && block.isEmpty()) {
                block = blockStack.pop();
            }
            if (block.matches("[+\\-*/=]") && block.length() == 1) {
                // TODO: 23.12.2022 kills app on empty blockStack
                block = blockStack.pop();
            } else if (block.equals("(")) {
                leftBraceCount--;
                block = blockStack.pop();
            } else if (block.equals(")")) {
                rightBraceCount--;
                block = blockStack.pop();
            } else {
                block = block.substring(0, block.length() - 1);
            }
            lineCollector.delete();
        }
        setInput();
    }

    public void clear() {
        blockStack.clear();
        block = "";
        answer = null;
        leftBraceCount = 0;
        rightBraceCount = 0;
        lineCollector.clearAllCalculations();
    }

    private void clearStack() {
        blockStack.clear();
        block = "";
        leftBraceCount = 0;
        rightBraceCount = 0;
    }

    private void add(String symbol) {
        if (symbol.matches("[+*/]")) {
            if (blockStack.empty()) {
                blockStack.push(block);
                blockStack.push(symbol);
                block = "";
                lineCollector.addDistinct(symbol);
            } else if (!blockStack.empty()) {
                if (!block.isEmpty()) {
                    blockStack.push(block);
                }
                blockStack.push(symbol);
                block = "";
                lineCollector.addDistinct(symbol);
            }
        } else if (symbol.equals("-")) {
            if (isNegativeNumber()) {
                block += symbol;
                lineCollector.addPortion(symbol);
            } else {
                if (blockStack.empty()) {
                    blockStack.push(block);
                    blockStack.push(symbol);
                    block = "";
                    lineCollector.addDistinct(symbol);
                } else if (!blockStack.empty()) {
                    if (!block.isEmpty()) {
                        blockStack.push(block);
                    }
                    blockStack.push(symbol);
                    block = "";
                    lineCollector.addDistinct(symbol);
                }
            }
        } else if (symbol.equals("(")) {
            if (block.equals("-")) {
                block = "";
                blockStack.push("-");
            }
            blockStack.push(symbol);
            lineCollector.addPortion(symbol);
        } else if (symbol.equals(")")) {
            if (!block.isEmpty()) {
                blockStack.push(block);
            }
            blockStack.push(symbol);
            block = "";
            lineCollector.addPortion(symbol);
        } else if (symbol.equals("=")) {
            answer = lineCollector.calculateAnswer();
            if (answer.type == CalculatorRPN.Answer.ANSWER_OK) {
                lineCollector.addAnswer(answer.content);
                clearStack();
            }
        } else {
            block += symbol;
            lineCollector.addPortion(symbol);
        }
        setInput();
    }

    private void setInput() {
        if (answer != null) {
            lineCollector.setPosition(answer.content);
            if (answer.type != CalculatorRPN.Answer.ANSWER_OK) {
                answer = null;
            }
        } else if (!block.isEmpty()) {
            lineCollector.setPosition(block);
        } else if (!blockStack.empty()) {
            lineCollector.setPosition(blockStack.peek());
        } else {
            lineCollector.setPosition("");
        }
    }

    private boolean isNegativeNumber() {
        if (block.isEmpty() && blockStack.empty()) {
            return true;
        } else if (!block.isEmpty() && blockStack.empty()) {
            return false;
        } else if (blockStack.peek().matches("-?[0-9]+\\.?([0-9]+)?")) {
            return false;
        } else if (blockStack.peek().equals(")")) {
            return false;
        } else {
            return block.isEmpty();
        }
    }

    private static class InputValueValidator {
        final static String number = "-?[0-9]+\\.?([0-9]+)?";
        final static String multiplyOrDivide = "[*/]";
        final static String allSigns = "[+\\-*/]";

        private static boolean validate(String symbol) {
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
                    if (block.matches(number)) {
                        return false;
                    }
                }
                if (!blockStack.empty()) {
                    if (blockStack.peek().matches(number)) {
                        return false;
                    }
                    if (block.matches(number)) {
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
            } else if (symbol.matches(number)) {
                if (block.equals("0") || block.equals("-0")) {
                    return false;
                }
                if (!blockStack.empty()) {
                    if (blockStack.peek().equals(")")) {
                        return false;
                    }
                }
            }
            if (symbol.equals("(")) {
                leftBraceCount++;
            }
            if (symbol.equals(")")) {
                rightBraceCount++;
            }
            return true;
        }
    }
}


