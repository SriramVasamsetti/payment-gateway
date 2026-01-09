package com.payment.gateway.services;

import com.payment.gateway.models.Order;
import com.payment.gateway.models.Payment;
import com.payment.gateway.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Random;


@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ValidationService validationService;
    private final Random random = new Random();

    @Value("${UPI_SUCCESS_RATE:0.90}")
    private double upiSuccessRate;

    @Value("${CARD_SUCCESS_RATE:0.95}")
    private double cardSuccessRate;

    @Value("${PROCESSING_DELAY_MIN:5000}")
    private int delayMin;

    @Value("${PROCESSING_DELAY_MAX:10000}")
    private int delayMax;

    @Value("${TEST_MODE:false}")
    private boolean testMode;

    @Value("${TEST_PAYMENT_SUCCESS:true}")
    private boolean testPaymentSuccess;

    @Value("${TEST_PROCESSING_DELAY:1000}")
    private int testDelay;

    public PaymentService(PaymentRepository paymentRepository,
                          ValidationService validationService) {
        this.paymentRepository = paymentRepository;
        this.validationService = validationService;
    }

    // ---------------- CREATE PAYMENT ----------------
    public Payment createPayment(
            Order order,
            String method,
            String vpa,
            String cardNumber,
            String expiryMonth,
            String expiryYear
    ) {

        Payment payment = new Payment();
        payment.setId(generatePaymentId());
        payment.setOrderId(order.getId());
        payment.setMerchantId(order.getMerchantId());
        payment.setAmount(order.getAmount());
        payment.setCurrency(order.getCurrency());
        payment.setMethod(method);
        payment.setStatus("processing");

        // -------- METHOD-SPECIFIC VALIDATION --------
        if ("upi".equals(method)) {
            if (!validationService.isValidVpa(vpa)) {
                throw new IllegalArgumentException("INVALID_VPA");
            }
            payment.setVpa(vpa);
        }

        if ("card".equals(method)) {
            if (!validationService.isValidCardNumber(cardNumber)) {
                throw new IllegalArgumentException("INVALID_CARD");
            }

            if (!validationService.isValidExpiry(expiryMonth, expiryYear)) {
                throw new IllegalArgumentException("EXPIRED_CARD");
            }

            String network = validationService.detectCardNetwork(cardNumber);
            payment.setCardNetwork(network);
            payment.setCardLast4(cardNumber.substring(cardNumber.length() - 4));
        }

        // Save immediately in PROCESSING state
        paymentRepository.save(payment);

        // ---------------- PROCESS PAYMENT ----------------
        simulateProcessing();

        boolean success = determineSuccess(method);

        if (success) {
            payment.setStatus("success");
        } else {
            payment.setStatus("failed");
            payment.setErrorCode("PAYMENT_FAILED");
            payment.setErrorDescription("Payment processing failed");
        }

        payment.setUpdatedAt(Instant.now());
        return paymentRepository.save(payment);
    }

    // ---------------- LIST PAYMENTS (NEW – REQUIRED) ----------------
    public List<Payment> getPaymentsByMerchant(String merchantId) {
        return paymentRepository.findByMerchantId(merchantId);
    }

    // ---------------- PROCESSING LOGIC ----------------
    private void simulateProcessing() {
        try {
            if (testMode) {
                Thread.sleep(testDelay);
            } else {
                int delay = random.nextInt(delayMax - delayMin + 1) + delayMin;
                Thread.sleep(delay);
            }
        } catch (InterruptedException ignored) {
        }
    }

    private boolean determineSuccess(String method) {
        if (testMode) {
            return testPaymentSuccess;
        }

        double chance = random.nextDouble();
        if ("upi".equals(method)) {
            return chance <= upiSuccessRate;
        }

        if ("card".equals(method)) {
            return chance <= cardSuccessRate;
        }

        return false;
    }

    // ---------------- ID GENERATION ----------------
    private String generatePaymentId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder("pay_");
        for (int i = 0; i < 16; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
