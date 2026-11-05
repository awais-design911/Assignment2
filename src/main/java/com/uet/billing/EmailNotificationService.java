package com.uet.billing;

public class EmailNotificationService implements NotificationService {

    @Override
    public void send(Customer customer, double total) {
        System.out.println("Email sent to " + customer.email() + ": Hello " + customer.name()
                + ", your order total is Rs. " + total);
    }
}
