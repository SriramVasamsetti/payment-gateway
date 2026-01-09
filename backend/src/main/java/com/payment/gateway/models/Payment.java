package com.payment.gateway.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "payments",
    indexes = {
        @Index(name = "idx_payments_order_id", columnList = "order_id"),
        @Index(name = "idx_payments_status", columnList = "status")
    }
)
public class Payment {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @Column(name = "merchant_id", nullable = false)
    private String merchantId;

    @Column(nullable = false)
    private Integer amount;

    @Column(length = 3)
    private String currency = "INR";

    @Column(length = 20, nullable = false)
    private String method; // upi | card

    @Column(length = 20)
    private String status = "processing";

    // ---- UPI ----
    @Column(length = 255)
    private String vpa;

    // ---- CARD ----
    @Column(name = "card_network", length = 20)
    private String cardNetwork;

    @Column(name = "card_last4", length = 4)
    private String cardLast4;

    // ---- ERROR ----
    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "error_description", columnDefinition = "TEXT")
    private String errorDescription;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    // -------- GETTERS / SETTERS --------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getVpa() { return vpa; }
    public void setVpa(String vpa) { this.vpa = vpa; }

    public String getCardNetwork() { return cardNetwork; }
    public void setCardNetwork(String cardNetwork) { this.cardNetwork = cardNetwork; }

    public String getCardLast4() { return cardLast4; }
    public void setCardLast4(String cardLast4) { this.cardLast4 = cardLast4; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public String getErrorDescription() { return errorDescription; }
    public void setErrorDescription(String errorDescription) { this.errorDescription = errorDescription; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
