package com.uet.billing;

/** Groups the customer data that used to travel as loose String parameters. */
public record Customer(String name, String email, String phone, CustomerType type) {
}
