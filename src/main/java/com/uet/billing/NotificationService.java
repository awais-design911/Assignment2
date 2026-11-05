package com.uet.billing;

/** Abstraction for any way of telling the customer about their order. */
public interface NotificationService {

    void send(Customer customer, double total);
}
