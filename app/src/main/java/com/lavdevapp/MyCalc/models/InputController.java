package com.lavdevapp.MyCalc.models;

import java.util.Stack;

public class InputController {
    private static int leftBraceCount = 0;
    private static int rightBraceCount = 0;
    private static String block = "";
    private static final Stack<String> blockStack = new Stack<>();
    private final ScreenLineBuilder lineCollector;

    public InputController(ScreenLineBuilder lineCollector) {
        this.lineCollector = lineCollector;
    }

    public void init(String symbol) {
        if (InputValueValidator.validate(symbol)) {
            add(symbol);
        }
    }

    public void delete() {
        if (blockStack.empty() && block.isEmpty()) {
            return;
        } else if (block.isEmpty()) {
            block = blockStack.pop();
            if (block.matches("[+\\-*/=]") && block.length() == 1) {
                lineCollector.deleteSymbol();
            } else if (block.equals("(")) {
                leftBraceCount--;
                lineCollector.deleteNumber();
            } else if (block.equals(")")) {
                rightBraceCount--;
                lineCollector.deleteNumber();
            } else {
                lineCollector.deleteNumber();
            }
        } else {
            lineCollector.deleteNumber();
        }
        block = block.substring(0, block.length() - 1);
    }

    public void clear() {
        blockStack.clear();
        block = "";
        leftBraceCount = 0;
        rightBraceCount = 0;
        lineCollector.clearLines();
    }

    private void add(String symbol) {
        if (symbol.matches("[+*/]")) {
            if (blockStack.empty()) {
                blockStack.push(block);
                blockStack.push(symbol);
                block = "";
                lineCollector.addSymbol(symbol);
            } else if (!blockStack.empty()) {
                if (!block.isEmpty()) {
                    blockStack.push(block);
                }
                blockStack.push(symbol);
                block = "";
                lineCollector.addSymbol(symbol);
            }

        } else if (symbol.equals("-")) {
            if (isNegativeNumber()) {
                block += symbol;
                lineCollector.addNumber(symbol);
            } else {
                if (blockStack.empty()) {
                    blockStack.push(block);
                    blockStack.push(symbol);
                    block = "";
                    lineCollector.addSymbol(symbol);
                } else if (!blockStack.empty()) {
                    if (!block.isEmpty()) {
                        blockStack.push(block);
                    }
                    blockStack.push(symbol);
                    block = "";
                    lineCollector.addSymbol(symbol);
                }
            }
        } else if (symbol.equals("(")) {
            if (block.equals("-")) {
                block = "";
                blockStack.push("-");
            }
            blockStack.push(symbol);
            lineCollector.addNumber(symbol);
        } else if (symbol.equals(")")) {
            if (!block.isEmpty()) {
                blockStack.push(block);
            }
            blockStack.push(symbol);
            block = "";
            lineCollector.addNumber(symbol);
        } else if (symbol.equals("=")) {
            String answer = lineCollector.calculateAnswer();
            if (!block.isEmpty()) {
                blockStack.push(block);
            }
            blockStack.push(symbol);
            blockStack.push(answer);
            block = "";
            lineCollector.addSymbol(symbol);
            lineCollector.addNumber(answer);
        } else {
            block += symbol;
            lineCollector.addNumber(symbol);
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

        public static boolean validate(String symbol) {
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
                        return false;
                    }
                    if (!blockStack.empty()) {
                        if (blockStack.peek().matches(allSigns)) {
                            return false;
                        }
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
            } else if (symbol.matches(number)) {
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


