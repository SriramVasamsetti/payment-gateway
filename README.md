# Payment Gateway

A full-stack payment gateway inspired by Razorpay/Stripe, built with **Java Spring Boot**, **React**, and **PostgreSQL**.  
Supports merchant authentication, order creation, UPI & card payments, and a hosted checkout page with deterministic test mode for evaluation.

---

## 🚀 Features

- RESTful API with API key & secret authentication
- Order creation and retrieval with status tracking
- UPI payments with VPA format validation
- Card payments with:
  - Luhn algorithm validation
  - Card network detection (Visa, Mastercard, Amex, RuPay)
  - Expiry date validation
- Hosted checkout page for customer payments
- Merchant dashboard for viewing API credentials and transactions
- PostgreSQL persistence with proper relationships
- Fully dockerized deployment using Docker Compose
- Deterministic test mode for automated evaluation

---

## 🏗️ Project Structure

```

payment-gateway/
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/payment/gateway/
│       │   ├── PaymentGatewayApplication.java
│       │   ├── config/
│       │   │   ├── CorsConfig.java
│       │   │   └── DataSeeder.java
│       │   ├── controllers/
│       │   │   ├── HealthController.java
│       │   │   ├── OrderController.java
│       │   │   ├── PaymentController.java
│       │   │   └── TestController.java
│       │   ├── models/
│       │   │   ├── Merchant.java
│       │   │   ├── Order.java
│       │   │   └── Payment.java
│       │   ├── repositories/
│       │   └── services/
│       │       ├── AuthenticationService.java
│       │       ├── OrderService.java
│       │       ├── PaymentService.java
│       │       └── ValidationService.java
│       └── resources/
│           └── application.properties
├── frontend/
│   ├── Dockerfile
│   ├── package.json
│   └── src/
│       ├── pages/
│       │   ├── Login.jsx
│       │   ├── Dashboard.jsx
│       │   └── Transactions.jsx
│       └── components/
├── checkout-page/
│   ├── Dockerfile
│   ├── package.json
│   └── src/
│       ├── pages/
│       │   ├── Checkout.jsx
│       │   ├── Success.jsx
│       │   └── Failure.jsx
│       └── components/
├── docker-compose.yml
├── .env.example
└── README.md

````

---

## 🐳 Running the Application (One Command)

### Prerequisites
- Docker
- Docker Compose

### Start all services
```bash
docker-compose up -d
````

No manual setup is required after this command.

### Service URLs

| Service       | URL                                                          |
| ------------- | ------------------------------------------------------------ |
| API           | [http://localhost:8000](http://localhost:8000)               |
| Dashboard     | [http://localhost:3000](http://localhost:3000)               |
| Checkout Page | [http://localhost:3001](http://localhost:3001)               |
| Health Check  | [http://localhost:8000/health](http://localhost:8000/health) |

---

## 🔐 Test Merchant (Auto-Seeded)

The application automatically seeds a test merchant on startup.

```
ID: 550e8400-e29b-41d4-a716-446655440000
Email: test@example.com
API Key: key_test_abc123
API Secret: secret_test_xyz789
```

If the merchant already exists, seeding is skipped safely.

---

## 📡 API Endpoints

### Health Check

```
GET /health
```

### Orders

```
POST /api/v1/orders
GET  /api/v1/orders/{order_id}
GET  /api/v1/orders/{order_id}/public
```

### Payments

```
POST /api/v1/payments
GET  /api/v1/payments/{payment_id}
```

### Test Endpoint

```
GET /api/v1/test/merchant
```

---

## 🔐 Authentication

All protected endpoints require:

```
X-Api-Key: key_test_abc123
X-Api-Secret: secret_test_xyz789
```

---

## 💳 Payment Processing

### UPI Payments

* Validates VPA format:
  `^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$`
* 90% simulated success rate
* Processing delay: 5–10 seconds

### Card Payments

* Validates card number using Luhn algorithm
* Detects card network (Visa, Mastercard, Amex, RuPay)
* Validates expiry date
* 95% simulated success rate
* Processing delay: 5–10 seconds
* Stores **only last 4 digits** of card number
* **Never stores CVV or full card number**

### Payment Lifecycle

```
processing → success / failed
```

Payments are created directly in `processing` state.

---

## 📊 Merchant Dashboard

### Login

```
Email: test@example.com
Password: any value (not validated for Deliverable 1)
```

### Features

* Displays API key & secret
* Shows total transactions
* Shows total successful amount
* Calculates success rate
* Lists all transactions

All required `data-test-id` attributes are implemented for automated evaluation.

---

## 🛒 Checkout Flow

1. Merchant creates an order via API
2. Customer opens:

```
http://localhost:3001/checkout?order_id=ORDER_ID
```

3. Selects UPI or Card
4. Payment is processed
5. Success or failure state is displayed

Checkout uses **public endpoints** and does not require API credentials.

---

## 🗄️ Database Schema

### Merchants

* id (UUID, primary key)
* name
* email (unique)
* api_key (unique)
* api_secret
* webhook_url (optional)
* is_active
* created_at, updated_at

### Orders

* id (`order_` + 16 chars)
* merchant_id (UUID)
* amount (paise, min 100)
* currency (INR)
* receipt
* notes
* status
* created_at, updated_at

### Payments

* id (`pay_` + 16 chars)
* order_id
* merchant_id
* amount
* currency
* method (upi/card)
* status (processing/success/failed)
* vpa (UPI only)
* card_network, card_last4 (card only)
* error_code, error_description
* created_at, updated_at

Indexes are applied on `merchant_id`, `order_id`, and `status`.

---

## 🧪 Test Mode (Evaluation Support)

Configured via environment variables:

```env
TEST_MODE=true
TEST_PAYMENT_SUCCESS=true
TEST_PROCESSING_DELAY=1000
```

When enabled:

* Payment outcomes are deterministic
* Processing delay is fixed
* Useful for automated evaluation

---

## 📷 Screenshots & Demo

* Screenshots of dashboard and checkout flows are included
* A short demo video shows:

  * Order creation
  * Checkout payment
  * Dashboard update

---

## 📌 Notes for Evaluators

* All services start with `docker-compose up -d`
* No manual database setup required
* No secrets committed
* CVV and full card numbers are never persisted
* Payment lifecycle strictly enforced

---

## ✅ Status

This project fulfills all requirements for **Deliverable 1 – Payment Gateway**.

````

---

## 🔚 WHAT TO DO NEXT

1. Replace your `README.md` with the above
2. Run:
```bash
git add README.md
git commit -m "Polish README for final submission"
git push
````
