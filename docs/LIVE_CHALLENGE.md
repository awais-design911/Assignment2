# Practice for the 3-minute live challenge

Because of the Strategy pattern you add features by writing **new classes only**.

## Add JazzCash

```java
package com.uet.billing;

public class JazzCashPayment implements PaymentStrategy {
    @Override public String getName() { return "JAZZCASH"; }

    @Override public void validate(String paymentDetails) {
        if (paymentDetails == null || !paymentDetails.matches("03\\d{9}")) {
            throw new IllegalArgumentException("Invalid JazzCash number");
        }
    }

    @Override public double calculateFee(double amount) { return amount * 0.01; }
}
```

Use it (no existing class edited):

```java
OrderBillingEngine engine = new OrderBillingEngine("orders.txt");
engine.registerPaymentStrategy(new JazzCashPayment());
engine.processOrder("Ali", "ali@example.com", null, "REGULAR",
        List.of("Pen"), List.of(100.0), List.of(1), "JAZZCASH", "03001234567");
```

## Add WhatsApp

```java
package com.uet.billing;

public class WhatsAppNotificationService implements NotificationService {
    @Override public void send(Customer customer, double total) {
        if (customer.phone() == null || customer.phone().isEmpty()) return;
        System.out.println("WhatsApp sent to " + customer.phone() + ": Order confirmed. Total Rs. " + total);
    }
}
```

```java
engine.addNotificationService(new WhatsAppNotificationService());
```

Also practise: add a new customer type (this one *does* touch `CustomerType`
and `DiscountCalculator` - be ready to explain why, and how you would make it
open for extension too).
