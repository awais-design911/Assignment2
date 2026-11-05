package com.uet.billing;

/** Single responsibility: work out the discount amount for a customer type. */
public class DiscountCalculator {

    private static final double VIP_RATE = 0.10;
    private static final double STUDENT_RATE = 0.05;
    private static final double CORPORATE_SMALL_RATE = 0.08;
    private static final double CORPORATE_LARGE_RATE = 0.15;
    private static final double CORPORATE_LARGE_THRESHOLD = 5000;

    public double calculate(CustomerType type, double subtotal) {
        return subtotal * rateFor(type, subtotal);
    }

    private double rateFor(CustomerType type, double subtotal) {
        return switch (type) {
            case REGULAR -> 0;
            case VIP -> VIP_RATE;
            case STUDENT -> STUDENT_RATE;
            case CORPORATE -> subtotal >= CORPORATE_LARGE_THRESHOLD
                    ? CORPORATE_LARGE_RATE : CORPORATE_SMALL_RATE;
        };
    }
}
