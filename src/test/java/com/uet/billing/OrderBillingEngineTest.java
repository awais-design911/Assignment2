package com.uet.billing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Characterization (safety-net) tests for OrderBillingEngine.
 * Written against the LEGACY code first; they must keep passing, unchanged,
 * after the refactoring.
 */
class OrderBillingEngineTest {

    private static final double DELTA = 0.001;
    private static final String EMAIL = "ali@example.com";
    private static final String VALID_CARD = "1234567812345678";
    private static final String VALID_EASYPAISA = "03001234567";

    @TempDir
    Path tempDir;

    private Path orderFile;
    private OrderBillingEngine engine;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream console;

    @BeforeEach
    void setUp() {
        orderFile = tempDir.resolve("orders.txt");
        engine = new OrderBillingEngine(orderFile.toString());
        console = new ByteArrayOutputStream();
        System.setOut(new PrintStream(console));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    /** One item, price x qty, with the given customer type and payment. */
    private double order(String type, double price, int qty, String payMethod, String payDetails) {
        return engine.processOrder("Ali", EMAIL, "03001112222", type,
                List.of("Laptop bag"), List.of(price), List.of(qty), payMethod, payDetails);
    }

    // ------------------------------------------------------------------
    // Standard orders and discounts
    // ------------------------------------------------------------------
    @Nested
    class DiscountRules {

        @Test
        void regularCustomerGetsNoDiscount() {
            // 2000 + 17% tax = 2340, + 100 COD fee
            assertEquals(2440.00, order("REGULAR", 1000, 2, "COD", null), DELTA);
        }

        @Test
        void vipCustomerGetsTenPercent() {
            // 2000 - 200 = 1800, + 306 tax = 2106, + 100
            assertEquals(2206.00, order("VIP", 1000, 2, "COD", null), DELTA);
        }

        @Test
        void studentGetsFivePercent() {
            // 2000 - 100 = 1900, + 323 tax = 2223, + 100
            assertEquals(2323.00, order("STUDENT", 1000, 2, "COD", null), DELTA);
        }

        @Test
        void corporateBelowThresholdGetsEightPercent() {
            // 2000 - 160 = 1840, + 312.8 tax = 2152.8, + 100
            assertEquals(2252.80, order("CORPORATE", 1000, 2, "COD", null), DELTA);
        }

        @Test
        void corporateAboveThresholdGetsFifteenPercent() {
            // 6000 - 900 = 5100, + 867 tax = 5967, + 100
            assertEquals(6067.00, order("CORPORATE", 3000, 2, "COD", null), DELTA);
        }

        @Test
        void corporateExactlyAtThresholdGetsFifteenPercent() {
            // 5000 - 750 = 4250, + 722.5 tax = 4972.5, + 100
            assertEquals(5072.50, order("CORPORATE", 5000, 1, "COD", null), DELTA);
        }

        @Test
        void multipleItemsAreSummed() {
            // 500*1 + 250*2 = 1000, + 170 tax = 1170, + 100
            double total = engine.processOrder("Ali", EMAIL, null, "REGULAR",
                    Arrays.asList("Pen", "Notebook"), Arrays.asList(500.0, 250.0),
                    Arrays.asList(1, 2), "COD", null);
            assertEquals(1270.00, total, DELTA);
        }

        @Test
        void zeroPriceItemOnlyPaysTheFee() {
            assertEquals(100.00, order("REGULAR", 0, 1, "COD", null), DELTA);
        }
    }

    // ------------------------------------------------------------------
    // Payment options
    // ------------------------------------------------------------------
    @Nested
    class PaymentOptions {

        @Test
        void creditCardAddsTwoAndHalfPercentFee() {
            // 2340 * 1.025
            assertEquals(2398.50, order("REGULAR", 1000, 2, "CREDIT_CARD", VALID_CARD), DELTA);
        }

        @Test
        void easyPaisaAddsOneAndHalfPercentFee() {
            // 2340 * 1.015
            assertEquals(2375.10, order("REGULAR", 1000, 2, "EASYPAISA", VALID_EASYPAISA), DELTA);
        }

        @Test
        void cashOnDeliveryAddsFlatFee() {
            assertEquals(2440.00, order("REGULAR", 1000, 2, "COD", null), DELTA);
        }

        @Test
        void cashOnDeliveryIgnoresPaymentDetails() {
            assertEquals(2440.00, order("REGULAR", 1000, 2, "COD", "anything"), DELTA);
        }

        @Test
        void discountAndCreditCardFeeCombine() {
            // VIP: 2106 * 1.025 = 2158.65
            assertEquals(2158.65, order("VIP", 1000, 2, "CREDIT_CARD", VALID_CARD), DELTA);
        }
    }

    // ------------------------------------------------------------------
    // Invalid input / error paths
    // ------------------------------------------------------------------
    @Nested
    class InvalidInput {

        @Test
        void emptyCartIsRejected() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> engine.processOrder("Ali", EMAIL, null, "REGULAR",
                            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                            "COD", null));
            assertEquals("Cart is empty", ex.getMessage());
        }

        @Test
        void nullCartIsRejected() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> engine.processOrder("Ali", EMAIL, null, "REGULAR",
                            null, null, null, "COD", null));
            assertEquals("Cart is empty", ex.getMessage());
        }

        @Test
        void mismatchedListSizesAreRejected() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> engine.processOrder("Ali", EMAIL, null, "REGULAR",
                            List.of("A", "B"), List.of(10.0), List.of(1, 1), "COD", null));
            assertEquals("Items, prices and quantities must have the same size", ex.getMessage());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"bad-email", "user@domain", "a.b@c", "no-at.com", "   "})
        void invalidEmailsAreRejected(String badEmail) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> engine.processOrder("Ali", badEmail, null, "REGULAR",
                            List.of("A"), List.of(10.0), List.of(1), "COD", null));
            assertEquals("Invalid email", ex.getMessage());
        }

        @Test
        void negativePriceIsRejected() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> order("REGULAR", -50, 1, "COD", null));
            assertEquals("Negative price for item: Laptop bag", ex.getMessage());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -10})
        void zeroOrNegativeQuantityIsRejected(int badQty) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> order("REGULAR", 100, badQty, "COD", null));
            assertEquals("Invalid quantity for item: Laptop bag", ex.getMessage());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"GOLD", "vip", "Student"})
        void unknownCustomerTypeIsRejected(String badType) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> order(badType, 100, 1, "COD", null));
            assertEquals("Unknown customer type: " + badType, ex.getMessage());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"BITCOIN", "cod", "JAZZCASH"})
        void unsupportedPaymentMethodIsRejected(String badMethod) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> order("REGULAR", 100, 1, badMethod, null));
            assertEquals("Unsupported payment method: " + badMethod, ex.getMessage());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"123456781234567", "12345678123456789", "1234abcd12345678"})
        void invalidCreditCardNumbersAreRejected(String badCard) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> order("REGULAR", 100, 1, "CREDIT_CARD", badCard));
            assertEquals("Invalid card number", ex.getMessage());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"04001234567", "0300123456", "030012345678", "0300abcdefg"})
        void invalidEasyPaisaNumbersAreRejected(String badNumber) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> order("REGULAR", 100, 1, "EASYPAISA", badNumber));
            assertEquals("Invalid EasyPaisa number", ex.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Side effects: notifications and persistence
    // ------------------------------------------------------------------
    @Nested
    class SideEffects {

        @Test
        void orderIsSavedToFile() throws IOException {
            order("VIP", 1000, 2, "COD", null);
            assertEquals(List.of("Ali|VIP|COD|2206.0"), Files.readAllLines(orderFile));
        }

        @Test
        void multipleOrdersAreAppended() throws IOException {
            order("REGULAR", 1000, 2, "COD", null);
            order("STUDENT", 1000, 2, "COD", null);
            assertEquals(List.of("Ali|REGULAR|COD|2440.0", "Ali|STUDENT|COD|2323.0"),
                    Files.readAllLines(orderFile));
        }

        @Test
        void nothingIsSavedWhenOrderIsInvalid() {
            assertThrows(IllegalArgumentException.class, () -> order("REGULAR", -1, 1, "COD", null));
            assertFalse(Files.exists(orderFile));
        }

        @Test
        void nothingIsPrintedWhenOrderIsInvalid() {
            assertThrows(IllegalArgumentException.class, () -> order("REGULAR", 100, 1, "BITCOIN", null));
            assertEquals("", console.toString());
        }

        @Test
        void emailIsAlwaysSent() {
            order("REGULAR", 1000, 2, "COD", null);
            assertTrue(console.toString().contains(
                    "Email sent to ali@example.com: Hello Ali, your order total is Rs. 2440.0"));
        }

        @Test
        void smsIsSentWhenPhoneIsGiven() {
            order("REGULAR", 1000, 2, "COD", null);
            assertTrue(console.toString().contains(
                    "SMS sent to 03001112222: Order confirmed. Total Rs. 2440.0"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void smsIsSkippedWhenPhoneIsMissing(String noPhone) {
            engine.processOrder("Ali", EMAIL, noPhone, "REGULAR",
                    List.of("A"), List.of(1000.0), List.of(2), "COD", null);
            assertFalse(console.toString().contains("SMS sent"));
            assertTrue(console.toString().contains("Email sent"));
        }
    }
}
