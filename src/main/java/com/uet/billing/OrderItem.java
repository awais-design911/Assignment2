package com.uet.billing;

/** One cart line (replaces three parallel lists). */
public record OrderItem(String name, double price, int quantity) {

    public double lineTotal() {
        return price * quantity;
    }
}
