package com.uet.billing;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates the billing workflow. All real work is delegated to small,
 * focused collaborators (validator, calculators, payment strategies,
 * notification services and repository).
 */
public class OrderBillingEngine {

    private static final String DEFAULT_FILE = "orders.txt";

    private final OrderValidator validator;
    private final DiscountCalculator discountCalculator;
    private final TaxCalculator taxCalculator;
    private final PaymentStrategyRegistry paymentRegistry;
    private final List<NotificationService> notificationServices;
    private final OrderRepository repository;

    public OrderBillingEngine() {
        this(DEFAULT_FILE);
    }

    public OrderBillingEngine(String filePath) {
        this(new FileOrderRepository(filePath));
    }

    public OrderBillingEngine(OrderRepository repository) {
        this(new OrderValidator(), new DiscountCalculator(), new TaxCalculator(),
                PaymentStrategyRegistry.withDefaults(), defaultNotifications(), repository);
    }

    public OrderBillingEngine(OrderValidator validator, DiscountCalculator discountCalculator,
                              TaxCalculator taxCalculator, PaymentStrategyRegistry paymentRegistry,
                              List<NotificationService> notificationServices, OrderRepository repository) {
        this.validator = validator;
        this.discountCalculator = discountCalculator;
        this.taxCalculator = taxCalculator;
        this.paymentRegistry = paymentRegistry;
        this.notificationServices = new ArrayList<>(notificationServices);
        this.repository = repository;
    }

    /** Extension point: add a new payment method without editing existing classes. */
    public void registerPaymentStrategy(PaymentStrategy strategy) {
        paymentRegistry.register(strategy);
    }

    /** Extension point: add a new notification channel without editing existing classes. */
    public void addNotificationService(NotificationService service) {
        notificationServices.add(service);
    }

    public double processOrder(String name, String email, String phone, String customerType,
                               List<String> itemNames, List<Double> prices, List<Integer> quantities,
                               String paymentMethod, String paymentDetails) {
        validator.validateCartShape(itemNames, prices, quantities);
        validator.validateEmail(email);
        List<OrderItem> items = validator.buildItems(itemNames, prices, quantities);

        CustomerType type = CustomerType.parse(customerType);
        Customer customer = new Customer(name, email, phone, type);

        double subtotal = 0;
        for (OrderItem item : items) {
            subtotal += item.lineTotal();
        }
        double afterDiscount = subtotal - discountCalculator.calculate(type, subtotal);
        double afterTax = afterDiscount + taxCalculator.calculate(afterDiscount);

        PaymentStrategy payment = paymentRegistry.get(paymentMethod);
        payment.validate(paymentDetails);
        double total = roundToCents(afterTax + payment.calculateFee(afterTax));

        for (NotificationService service : notificationServices) {
            service.send(customer, total);
        }
        repository.save(customer, paymentMethod, total);
        return total;
    }

    private static double roundToCents(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }

    private static List<NotificationService> defaultNotifications() {
        List<NotificationService> services = new ArrayList<>();
        services.add(new EmailNotificationService());
        services.add(new SmsNotificationService());
        return services;
    }
}
