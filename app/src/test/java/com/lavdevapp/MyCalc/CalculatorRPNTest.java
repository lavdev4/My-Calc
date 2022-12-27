package com.lavdevapp.MyCalc;

import static org.junit.Assert.*;

import com.lavdevapp.MyCalc.models.CalculatorRPN;

import org.junit.Before;
import org.junit.Test;

import java.text.DecimalFormat;

public class CalculatorRPNTest {
    public CalculatorRPN calculator;
    public DecimalFormat answerDecimalFormat;
    public final String EXPRESSION_1 = "1.25 - -2.5 + 4 * ( -2 + 1.4 - ( 389 / 20.4 - 1 ) - -8 + 7 ) / -47.5748 / 24.374 + 1000";
    public final double EXPRESSION_1_ANSWER = 1003.7626549323;

    @Before
    public void before() {
        calculator = new CalculatorRPN();
        answerDecimalFormat = calculator.answerFormat;
    }

    @Test
    public void calculateExpression1() {
        Double expected = Double.parseDouble(answerDecimalFormat.format(EXPRESSION_1_ANSWER));
        Double actual = Double.parseDouble(calculator.tryCalculate(EXPRESSION_1).content);
        assertEquals(expected, actual);
    }
}