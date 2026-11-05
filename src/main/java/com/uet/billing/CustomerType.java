package com.uet.billing;

/** Replaces the raw "VIP"/"STUDENT"/... strings (fixes Primitive Obsession). */
public enum CustomerType {
    REGULAR, VIP, STUDENT, CORPORATE;

    public static CustomerType parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unknown customer type: null");
        }
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown customer type: " + value);
        }
    }
}
