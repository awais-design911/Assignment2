package com.uet.billing;

import java.io.FileWriter;
import java.io.IOException;

/** Appends one line per order to a text file. */
public class FileOrderRepository implements OrderRepository {

    private final String filePath;

    public FileOrderRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void save(Customer customer, String paymentMethod, double total) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(customer.name() + "|" + customer.type() + "|" + paymentMethod + "|" + total + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Could not save order", e);
        }
    }
}
