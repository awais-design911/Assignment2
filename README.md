# Order Billing Engine - Legacy Code Refactoring

Software Construction & Development - Assignment 02 (UET Abbottabad)
Author: Muhammad Awais (24abswe0013)

Refactoring of a messy, untested `OrderBillingEngine` into clean Java using a
JUnit 5 safety net, SOLID principles and the Strategy pattern.

## Project layout

```
baseline/OrderBillingEngine.java      legacy version (reference, not compiled)
src/main/java/com/uet/billing/        refactored code
src/test/java/com/uet/billing/        JUnit 5 characterization tests
docs/                                 audit, commit guide, report template, live-challenge practice
```

## Run the tests

Requires JDK 17+ and Maven.

```bash
mvn test
```

## Design summary

| Concern | Class(es) |
|---------|-----------|
| Coordination | `OrderBillingEngine` |
| Input validation | `OrderValidator` |
| Discount / tax | `DiscountCalculator`, `TaxCalculator` |
| Payments (Strategy) | `PaymentStrategy`, `CreditCardPayment`, `EasyPaisaPayment`, `CashOnDeliveryPayment`, `PaymentStrategyRegistry` |
| Notifications | `NotificationService`, `EmailNotificationService`, `SmsNotificationService` |
| Persistence | `OrderRepository`, `FileOrderRepository` |
| Data | `Customer`, `OrderItem`, `CustomerType` |

New payment methods or notification channels are added by writing a new class and
calling `registerPaymentStrategy(...)` / `addNotificationService(...)` - no existing
class is modified (Open/Closed Principle). See `docs/LIVE_CHALLENGE.md`.

## Business rules

- Discounts: REGULAR 0%, VIP 10%, STUDENT 5%, CORPORATE 8% (15% when subtotal >= 5000)
- Tax: 17% on the discounted amount
- Payment fee: CREDIT_CARD 2.5%, EASYPAISA 1.5%, COD flat 100
- Invalid input (empty cart, bad email, negative price, bad quantity, unknown type/payment,
  bad card/EasyPaisa number) throws `IllegalArgumentException`
