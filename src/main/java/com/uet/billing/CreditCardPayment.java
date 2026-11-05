package com.uet.billing;

public class CreditCardPayment implements PaymentStrategy {

    private static final double FEE_RATE = 0.025;
    private static final int CARD_LENGTH = 16;

    @Override
    public String getName() {
        return "CREDIT_CARD";
    }

    @Override
    public void validate(String paymentDetails) {
        if (paymentDetails == null || paymentDetails.length() != CARD_LENGTH
                || !paymentDetails.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid card number");
        }
    }

    @Override
    public double calculateFee(double amount) {
        return amount * FEE_RATE;
    }
}
