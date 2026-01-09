package com.payment.gateway.services;

import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
public class ValidationService {

    // ---------------- VPA VALIDATION ----------------
    public boolean isValidVpa(String vpa) {
        if (vpa == null) return false;
        return vpa.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$");
    }

    // ---------------- CARD NUMBER (LUHN) ----------------
    public boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null) return false;

        String cleaned = cardNumber.replaceAll("[ -]", "");
        if (!cleaned.matches("\\d{13,19}")) return false;

        int sum = 0;
        boolean alternate = false;

        for (int i = cleaned.length() - 1; i >= 0; i--) {
            int n = cleaned.charAt(i) - '0';

            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }

            sum += n;
            alternate = !alternate;
        }

        return sum % 10 == 0;
    }

    // ---------------- CARD NETWORK DETECTION ----------------
    public String detectCardNetwork(String cardNumber) {
        String cleaned = cardNumber.replaceAll("[ -]", "");

        if (cleaned.startsWith("4")) {
            return "visa";
        }

        if (cleaned.matches("^5[1-5].*")) {
            return "mastercard";
        }

        if (cleaned.matches("^3[47].*")) {
            return "amex";
        }

        if (cleaned.matches("^(60|65|8[1-9]).*")) {
            return "rupay";
        }

        return "unknown";
    }

    // ---------------- EXPIRY VALIDATION ----------------
    public boolean isValidExpiry(String month, String year) {
        try {
            int mm = Integer.parseInt(month);
            if (mm < 1 || mm > 12) return false;

            int yy = Integer.parseInt(year);
            if (year.length() == 2) {
                yy += 2000;
            }

            YearMonth expiry = YearMonth.of(yy, mm);
            YearMonth now = YearMonth.now();

            return !expiry.isBefore(now);
        } catch (Exception e) {
            return false;
        }
    }
}
