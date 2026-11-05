package com.uet.billing;

public class CashOnDeliveryPayment implements PaymentStrategy {

    private static final double FLAT_FEE = 100;

    @Override
    public String getName() {
        return "COD";
    }

    @Override
    public void validate(String paymentDetails) {
        // Cash on delivery needs no payment details.
    }

    @Override
    public double calculateFee(double amount) {
        return FLAT_FEE;
    }
}
