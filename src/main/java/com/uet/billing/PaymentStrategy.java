package com.uet.billing;

/** Strategy interface: every payment option implements this. */
public interface PaymentStrategy {

    /** Key used by callers, e.g. "CREDIT_CARD". */
    String getName();

    /** Throws IllegalArgumentException if the payment details are not valid. */
    void validate(String paymentDetails);

    /** Extra fee charged for this method on the given amount. */
    double calculateFee(double amount);
}
