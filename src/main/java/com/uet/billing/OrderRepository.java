package com.uet.billing;

/** Abstraction for saving orders (file today, database tomorrow). */
public interface OrderRepository {

    void save(Customer customer, String paymentMethod, double total);
}
