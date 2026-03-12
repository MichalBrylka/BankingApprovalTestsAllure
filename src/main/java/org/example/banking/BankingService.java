package org.example.banking;

import java.time.LocalDateTime;
import java.util.UUID;

public class BankingService {

    public Report calculateInterest(String type, double initialBalance) {
        // Bogus calculation: 5% interest for PREMIUM, 2% otherwise
        double rate = "PREMIUM".equalsIgnoreCase(type) ? 0.05 : 0.02;
        double newBalance = initialBalance + (initialBalance * rate);

        // Generate dynamic data that usually breaks tests
        String txId = "TX-" + UUID.randomUUID().toString().substring(0, 8);

        return new Report(type, newBalance, txId, LocalDateTime.now());
    }
}