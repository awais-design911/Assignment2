package com.uet.billing;

public class SmsNotificationService implements NotificationService {

    @Override
    public void send(Customer customer, double total) {
        String phone = customer.phone();
        if (phone == null || phone.isEmpty()) {
            return; // no phone number, nothing to send
        }
        System.out.println("SMS sent to " + phone + ": Order confirmed. Total Rs. " + total);
    }
}
