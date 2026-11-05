# Step-by-step Git commit guide (10 commits)

The assignment checks that your history shows *real, gradual progress*, so make
each commit yourself, in this order. Run `mvn test` before every commit from
step 2 onwards: it must be green each time.

Start with an **empty** repository (do not copy this whole folder in one go).
The final version of every file is in this project - add them step by step.

| # | Commit message | What to add / change |
|---|----------------|----------------------|
| 1 | `chore: add baseline legacy OrderBillingEngine file` | `pom.xml`, `.gitignore`, and copy `baseline/OrderBillingEngine.java` to `src/main/java/com/uet/billing/OrderBillingEngine.java` (also keep the copy in `baseline/`). |
| 2 | `test: add characterization tests for discount calculations` | Create `OrderBillingEngineTest.java` with the `setUp`/`tearDown`, the `order(...)` helper and only the `DiscountRules` nested class. Run against the legacy code - all green. |
| 3 | `test: add tests for payment options and invalid error paths` | Add the `PaymentOptions`, `InvalidInput` and `SideEffects` nested classes. Still against legacy code - all green. |
| 4 | `docs: document baseline code smells and SOLID violations` | Add `docs/CODE_SMELLS_AND_SOLID_AUDIT.md`. |
| 5 | `refactor: extract tax and discount calculator classes` | Add `CustomerType`, `DiscountCalculator`, `TaxCalculator`. In the engine, replace the discount block (lines 49-69) and tax lines (72-73) with calls to them. `mvn test`. |
| 6 | `refactor: implement strategy pattern for payments` | Add `PaymentStrategy`, `CreditCardPayment`, `EasyPaisaPayment`, `CashOnDeliveryPayment`, `PaymentStrategyRegistry`. Replace the payment if-else block with `registry.get(method)`, `validate`, `calculateFee`. `mvn test`. |
| 7 | `refactor: decouple email and sms notification dispatchers` | Add `Customer`, `NotificationService`, `EmailNotificationService`, `SmsNotificationService`. Replace the two `System.out.println` blocks with a loop over notification services. `mvn test`. |
| 8 | `refactor: move file persistence behind repository interface` | Add `OrderRepository`, `FileOrderRepository`. Replace the `FileWriter` block with `repository.save(...)`. `mvn test`. |
| 9 | `refactor: clean up main service to follow single responsibility` | Add `OrderItem`, `OrderValidator`; move the validation and item loop out of the engine; add the constructor injection and `registerPaymentStrategy` / `addNotificationService`. Engine should now only coordinate. `mvn test`. |
| 10 | `test: verify all unit tests pass with clean architecture` | Add `docs/REPORT_TEMPLATE.md` filled with your numbers (lines of code, test count from `mvn test`), README. Run the full suite one last time. |

Extra commits are welcome (for example splitting step 6 into "add interface",
"add CreditCardPayment", ...). More small commits = a more convincing history.

## Commands

```bash
git init
git add pom.xml .gitignore baseline src/main
git commit -m "chore: add baseline legacy OrderBillingEngine file"
# ... repeat add/commit for each step ...
git log --oneline          # paste this output in your PDF report

# create an EMPTY PRIVATE repo on GitHub first, then:
git branch -M main
git remote add origin https://github.com/<your-username>/order-billing-refactor.git
git push -u origin main
```

Remember to give your instructor access to the private repository (Settings -> Collaborators)
or submit the link as the assignment asks.
