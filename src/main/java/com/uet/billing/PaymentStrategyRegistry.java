package com.uet.billing;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Looks up payment strategies by name. New methods (e.g. JazzCash) are added with
 * register(...) - no existing class has to change (Open/Closed Principle).
 */
public class PaymentStrategyRegistry {

    private final Map<String, PaymentStrategy> strategies = new LinkedHashMap<>();

    public static PaymentStrategyRegistry withDefaults() {
        PaymentStrategyRegistry registry = new PaymentStrategyRegistry();
        registry.register(new CreditCardPayment());
        registry.register(new EasyPaisaPayment());
        registry.register(new CashOnDeliveryPayment());
        return registry;
    }

    public void register(PaymentStrategy strategy) {
        strategies.put(strategy.getName(), strategy);
    }

    public PaymentStrategy get(String name) {
        PaymentStrategy strategy = (name == null) ? null : strategies.get(name);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + name);
        }
        return strategy;
    }
}
