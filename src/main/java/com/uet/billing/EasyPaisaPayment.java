package com.uet.billing;

public class EasyPaisaPayment implements PaymentStrategy {

    private static final double FEE_RATE = 0.015;

    @Override
    public String getName() {
        return "EASYPAISA";
    }

    @Override
    public void validate(String paymentDetails) {
        if (paymentDetails == null || !paymentDetails.matches("03\\d{9}")) {
            throw new IllegalArgumentException("Invalid EasyPaisa number");
        }
    }

    @Override
    public double calculateFee(double amount) {
        return amount * FEE_RATE;
    }
}
