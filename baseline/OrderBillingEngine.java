package com.uet.billing;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * LEGACY CODE - intentionally messy baseline for Assignment 02.
 * Does everything: validation, discounts, tax, payment, notifications, file saving.
 */
public class OrderBillingEngine {

    private String filePath = "orders.txt";

    public OrderBillingEngine() {
    }

    public OrderBillingEngine(String filePath) {
        this.filePath = filePath;
    }

    public double processOrder(String name, String email, String phone, String type,
                               List<String> items, List<Double> prices, List<Integer> qtys,
                               String payMethod, String payDetails) {
        // ---- validation ----
        if (items == null || items.size() == 0) {
            throw new IllegalArgumentException("Cart is empty");
        }
        if (prices == null || qtys == null || prices.size() != items.size() || qtys.size() != items.size()) {
            throw new IllegalArgumentException("Items, prices and quantities must have the same size");
        }
        if (email == null || !email.contains("@") || !email.contains(".")
                || email.indexOf('@') > email.lastIndexOf('.')) {
            throw new IllegalArgumentException("Invalid email");
        }

        // ---- subtotal ----
        double subtotal = 0;
        for (int i = 0; i < items.size(); i++) {
            if (prices.get(i) < 0) {
                throw new IllegalArgumentException("Negative price for item: " + items.get(i));
            }
            if (qtys.get(i) <= 0) {
                throw new IllegalArgumentException("Invalid quantity for item: " + items.get(i));
            }
            subtotal += prices.get(i) * qtys.get(i);
        }

        // ---- discount ----
        double discount = 0;
        if (type == null) {
            throw new IllegalArgumentException("Unknown customer type: " + type);
        }
        if (type.equals("REGULAR")) {
            discount = 0;
        } else if (type.equals("VIP")) {
            discount = subtotal * 0.10;
        } else if (type.equals("STUDENT")) {
            discount = subtotal * 0.05;
        } else if (type.equals("CORPORATE")) {
            if (subtotal >= 5000) {
                discount = subtotal * 0.15;
            } else {
                discount = subtotal * 0.08;
            }
        } else {
            throw new IllegalArgumentException("Unknown customer type: " + type);
        }
        double afterDiscount = subtotal - discount;

        // ---- tax ----
        double tax = afterDiscount * 0.17;
        double total = afterDiscount + tax;

        // ---- payment ----
        double fee = 0;
        if (payMethod == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + payMethod);
        }
        if (payMethod.equals("CREDIT_CARD")) {
            if (payDetails == null || payDetails.length() != 16 || !payDetails.matches("\\d+")) {
                throw new IllegalArgumentException("Invalid card number");
            }
            fee = total * 0.025;
        } else if (payMethod.equals("EASYPAISA")) {
            if (payDetails == null || !payDetails.matches("03\\d{9}")) {
                throw new IllegalArgumentException("Invalid EasyPaisa number");
            }
            fee = total * 0.015;
        } else if (payMethod.equals("COD")) {
            fee = 100;
        } else {
            throw new IllegalArgumentException("Unsupported payment method: " + payMethod);
        }
        total = total + fee;
        total = Math.round(total * 100.0) / 100.0;

        // ---- notifications ----
        System.out.println("Email sent to " + email + ": Hello " + name + ", your order total is Rs. " + total);
        if (phone != null && !phone.isEmpty()) {
            System.out.println("SMS sent to " + phone + ": Order confirmed. Total Rs. " + total);
        }

        // ---- save to file ----
        try (FileWriter fw = new FileWriter(filePath, true)) {
            fw.write(name + "|" + type + "|" + payMethod + "|" + total + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Could not save order", e);
        }

        return total;
    }
}
