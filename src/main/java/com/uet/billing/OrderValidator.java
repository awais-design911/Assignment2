package com.uet.billing;

import java.util.ArrayList;
import java.util.List;

/** Single responsibility: validate cart and email input. */
public class OrderValidator {

    public void validateCartShape(List<String> names, List<Double> prices, List<Integer> quantities) {
        if (names == null || names.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }
        if (prices == null || quantities == null
                || prices.size() != names.size() || quantities.size() != names.size()) {
            throw new IllegalArgumentException("Items, prices and quantities must have the same size");
        }
    }

    public void validateEmail(String email) {
        if (email == null || !email.contains("@") || !email.contains(".")
                || email.indexOf('@') > email.lastIndexOf('.')) {
            throw new IllegalArgumentException("Invalid email");
        }
    }

    public List<OrderItem> buildItems(List<String> names, List<Double> prices, List<Integer> quantities) {
        List<OrderItem> items = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            if (prices.get(i) < 0) {
                throw new IllegalArgumentException("Negative price for item: " + names.get(i));
            }
            if (quantities.get(i) <= 0) {
                throw new IllegalArgumentException("Invalid quantity for item: " + names.get(i));
            }
            items.add(new OrderItem(names.get(i), prices.get(i), quantities.get(i)));
        }
        return items;
    }
}
