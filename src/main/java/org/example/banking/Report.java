package org.example.banking;

import java.time.LocalDateTime;

public record Report(
        String accountType,
        double balance,
        String transactionId,
        LocalDateTime timestamp) {
}