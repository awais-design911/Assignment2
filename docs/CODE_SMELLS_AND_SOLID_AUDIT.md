# Code Smells & SOLID Violations Audit

File audited: `baseline/OrderBillingEngine.java` (113 lines, the legacy version).
Line numbers refer to that file.

## Code smells (8 found, 5 required)

| # | Smell | Lines | Why it is bad | How it was fixed |
|---|-------|-------|---------------|------------------|
| 1 | **God Class** | 11-113 | One class validates input, calculates discount and tax, charges fees, sends notifications and writes files. Any change risks breaking something unrelated. | Split into `OrderValidator`, `DiscountCalculator`, `TaxCalculator`, `PaymentStrategy` classes, `NotificationService` classes and `OrderRepository`; `OrderBillingEngine` only coordinates. |
| 2 | **Long Method** | 22-112 (91 lines) | `processOrder` mixes eight different jobs in one method, so it is hard to read, test and reuse. | Each job moved to its own class; `processOrder` is now a short sequence of calls. |
| 3 | **Magic Numbers** | 57, 59, 61, 62, 64 (discount rates, 5000 threshold), 72 (0.17 tax), 81 (16 digits), 84, 89 (fee rates), 91 (flat fee 100) | Numbers with no name; nobody knows what `0.15` or `100` means, and changing a rate means hunting through the method. | Named `private static final` constants inside the class that owns each rule (e.g. `TaxCalculator.TAX_RATE`). |
| 4 | **Primitive Obsession** | 22-24 (9 loose parameters), 23 + 40-46 (three parallel lists), 51-68 and 80-93 (customer type and payment method as raw Strings) | Strings and parallel lists can be mistyped or get out of sync, and the compiler cannot help. | `CustomerType` enum, `Customer` record, `OrderItem` record, `PaymentStrategy` objects. |
| 5 | **Deeply nested if-else chains** (switch on type) | 54-68 (customer type, nested if at 61-65), 80-94 (payment method) | Every new type or method adds another branch inside the same method. | Switch expression inside `DiscountCalculator`; Strategy pattern for payments. |
| 6 | **Direct File I/O** | 13 (hard-coded `"orders.txt"`), 105-109 | Business logic is tied to the file system; cannot be tested or swapped for a database. | `OrderRepository` interface + `FileOrderRepository`. |
| 7 | **Hard-coded console notifications** | 99-102 | Output is tied to `System.out`; adding WhatsApp means editing the billing method. | `NotificationService` interface + `EmailNotificationService`, `SmsNotificationService`. |
| 8 | **Long Parameter List** | 22-24 | Nine parameters in a row are easy to pass in the wrong order. | Parameters grouped into `Customer`, `OrderItem` internally (public method kept the same so the tests do not change). |

## Broken SOLID principles (3 found, 3 required)

| # | Principle | Lines | How it is broken | How it was fixed |
|---|-----------|-------|------------------|------------------|
| 1 | **S - Single Responsibility** | 22-112 | The class has many reasons to change: tax law, discount policy, payment rules, message format, storage format. | One class per responsibility (see smell 1). |
| 2 | **O - Open/Closed** | 54-68, 80-94, 99-102 | Adding JazzCash, a new customer type or WhatsApp means editing `processOrder`. | New payment methods / notifications are new classes plugged in through `registerPaymentStrategy(...)` and `addNotificationService(...)`; existing classes stay untouched. |
| 3 | **D - Dependency Inversion** | 99, 101, 105 | The high-level billing logic depends directly on low-level details (`System.out`, `FileWriter`). | The engine depends on the `NotificationService` and `OrderRepository` interfaces, injected through the constructor. |
