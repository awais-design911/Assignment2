package com.uet.billing;

/** Single responsibility: sales tax (GST). */
public class TaxCalculator {

    private static final double TAX_RATE = 0.17;

    public double calculate(double amount) {
        return amount * TAX_RATE;
    }
}
