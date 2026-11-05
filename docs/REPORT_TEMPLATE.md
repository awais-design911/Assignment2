# Assignment 02 - Report (max 3 pages)

**Name:** Muhammad Younas  **Reg. No:** 24ABELT1000
**Course:** Software Construction & Development  **Instructor:** Engr. Rizwan Shah
**Repository:** <paste your private GitHub/GitLab link>

## 1. Code smells & SOLID audit
Copy the two tables from `docs/CODE_SMELLS_AND_SOLID_AUDIT.md`.

## 2. New class diagram

```
                         +---------------------+
                         |  OrderBillingEngine |  (coordinator only)
                         +----------+----------+
   uses                              | depends on
 +-------------+  +------------------+------------------+--------------------+
 |OrderValidator| | DiscountCalculator / TaxCalculator   |                    |
 +-------------+  +-------------------------------------+                    |
                                                                             |
   <<interface>> PaymentStrategy        <<interface>> NotificationService    <<interface>> OrderRepository
     |- CreditCardPayment                 |- EmailNotificationService          |- FileOrderRepository
     |- EasyPaisaPayment                  |- SmsNotificationService
     |- CashOnDeliveryPayment
   PaymentStrategyRegistry (looks up strategies by name)
   Data: Customer, OrderItem (records), CustomerType (enum)
```
(Redraw this neatly in Word / draw.io for the PDF.)

## 3. Before vs after

| Metric | Before (legacy) | After (refactored) |
|--------|-----------------|--------------------|
| Source files (main) | 1 | 17 |
| Lines in `OrderBillingEngine.java` | 113 | 94 |
| Lines in `processOrder` | 91 | ~25 |
| Total main lines of code | 113 | 376 |
| Longest if-else chain | 3 branches + nested if | none (Strategy / switch expression) |
| Tests run | <fill from `mvn test`> | <same number> |
| Tests passing | <all> | <all> |

Note: total code grew because responsibilities were separated into small, documented classes;
the *billing engine itself* became simpler and every class has one reason to change.
Fill the test counts from your own `mvn test` output (Tests run: X, Failures: 0).

## 4. `git log --oneline`
<paste output>
