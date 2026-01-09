package com.payment.gateway.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "orders",
    indexes = {
        @Index(name = "idx_orders_merchant_id", columnList = "merchant_id")
    }
)
public class Order {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "merchant_id", nullable = false)
    private String merchantId;

    @Column(nullable = false)
    private Integer amount;

    @Column(length = 3)
    private String currency = "INR";

    @Column(length = 255)
    private String receipt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(length = 20)
    private String status = "created";

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    

    /* Getters & Setters */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
}

}
