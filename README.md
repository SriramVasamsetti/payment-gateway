# Payment Gateway

A complete payment gateway system built with Java Spring Boot backend, React frontend, and PostgreSQL database. Supports UPI and card payments with merchant authentication, order management, and checkout integration.

## Features

- **RESTful API** with merchant authentication
- **Payment Processing** for both UPI and card payments
- **Order Management** system with status tracking  
- **Hosted Checkout Page** for customers
- **Database Persistence** with proper schema and relationships
- **Docker Deployment** with docker-compose for all services
- **Payment Validation** including Luhn algorithm and VPA format validation

## Project Structure

```
payment-gateway/
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/payment/
│       │   ├── PaymentGatewayApplication.java
│       │   ├── config/SecurityConfig.java
│       │   ├── controllers/
│       │   │   ├── HealthController.java
│       │   │   ├── OrderController.java
│       │   │   └── PaymentController.java
│       │   ├── models/
│       │   │   ├── Merchant.java
│       │   │   ├── Order.java
│       │   │   └── Payment.java
│       │   ├── repositories/
│       │   ├── services/
│       │   │   ├── OrderService.java
│       │   │   ├── PaymentService.java
│       │   │   └── ValidationService.java
│       │   └── dto/
│       └── resources/
│           └── application.properties
├── frontend/
│   ├── Dockerfile
│   ├── package.json
│   └── src/
│       ├── pages/
│       │   ├── Dashboard.jsx
│       │   ├── Transactions.jsx
│       │   └── Login.jsx
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
```

## Quick Start

### Prerequisites

- Docker and Docker Compose
- Node.js 21+ (for local development)
- Java 21+ (for local backend development)
- PostgreSQL 15+ (for local database)

### Running with Docker

1. Clone the repository:
```bash
git clone https://github.com/SriramVasamsetti/payment-gateway.git
cd payment-gateway
```

2. Copy environment variables:
```bash
cp .env.example .env
```

3. Start all services:
```bash
docker-compose up -d
```

4. Access the applications:
   - API: http://localhost:8000
   - Dashboard: http://localhost:3000
   - Checkout: http://localhost:3001
   - Health Check: http://localhost:8000/health

### API Endpoints

#### Health Check
- `GET /health` - Check API health status

#### Orders
- `POST /api/v1/orders` - Create an order
- `GET /api/v1/orders/{order_id}` - Get order details

#### Payments
- `POST /api/v1/payments` - Create a payment
- `GET /api/v1/payments/{payment_id}` - Get payment status

#### Test Endpoints
- `GET /api/v1/test/merchant` - Get test merchant details

## Authentication

All protected endpoints require two headers:
- `X-Api-Key: key_test_abc123`
- `X-Api-Secret: secret_test_xyz789`

## Database Schema

### Merchants Table
- id (UUID, primary key)
- name (string, max 255)
- email (string, unique)
- api_key (string, unique)
- api_secret (string)
- webhook_url (text, optional)
- is_active (boolean, default true)
- created_at, updated_at (timestamps)

### Orders Table
- id (string, primary key) - Format: "order_" + 16 alphanumeric chars
- merchant_id (UUID, foreign key)
- amount (integer, in paise, min 100)
- currency (string, default 'INR')
- receipt (string, optional)
- notes (JSON, optional)
- status (string, default 'created')
- created_at, updated_at (timestamps)

### Payments Table
- id (string, primary key) - Format: "pay_" + 16 alphanumeric chars
- order_id (string, foreign key)
- merchant_id (UUID, foreign key)
- amount (integer, in paise)
- currency (string, default 'INR')
- method (string: 'upi' or 'card')
- status (string: 'processing', 'success', 'failed')
- vpa (string, optional - for UPI)
- card_network (string, optional - for cards)
- card_last4 (string, optional - last 4 digits only)
- error_code, error_description (optional - if failed)
- created_at, updated_at (timestamps)

## Payment Processing

### UPI Payments
- Validates VPA format: `^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$`
- 90% success rate (simulated)
- Processing delay: 5-10 seconds

### Card Payments
- Validates card using Luhn algorithm
- Detects card network (Visa, Mastercard, Amex, RuPay)
- Validates expiry date
- 95% success rate (simulated)
- Processing delay: 5-10 seconds
- Stores only last 4 digits (never stores full card number or CVV)

## Environment Variables

Key environment variables (see .env.example for all):

```
DATABASE_URL=postgresql://gateway_user:gateway_pass@postgres:5432/payment_gateway
PORT=8000
TEST_MODE=false
TEST_PAYMENT_SUCCESS=true
TEST_PROCESSING_DELAY=1000
UPI_SUCCESS_RATE=0.90
CARD_SUCCESS_RATE=0.95
```

## Test Merchant Credentials

Automatically seeded on startup:
- **Email**: test@example.com
- **API Key**: key_test_abc123
- **API Secret**: secret_test_xyz789
- **ID**: 550e8400-e29b-41d4-a716-446655440000

## Development

### Backend Development

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend Development

```bash
cd frontend
npm install
npm run dev
```

### Checkout Page Development

```bash
cd checkout-page
npm install
npm run dev
```

## Testing

Sample order creation:

```bash
curl -X POST http://localhost:8000/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "X-Api-Key: key_test_abc123" \
  -H "X-Api-Secret: secret_test_xyz789" \
  -d '{"amount": 50000, "currency": "INR", "receipt": "receipt_123"}'
```

## Architecture

The system follows a microservices architecture with the following components:

1. **API Gateway** (Spring Boot) - RESTful endpoints, authentication, payment validation
2. **Frontend Dashboard** (React) - Merchant interface for managing orders and payments
3. **Checkout Page** (React) - Customer-facing payment interface
4. **PostgreSQL Database** - Data persistence with proper relationships

## Security

- API key and secret authentication
- Card data validation without storage of sensitive info
- HTTPS recommended for production
- Database connection pooling
- Input validation on all endpoints

## Performance

- Indexed database queries on merchant_id, order_id, payment status
- Efficient pagination for transaction lists
- Asynchronous payment processing
- Connection pooling for database

## Deployment

The application is containerized and ready for deployment using Docker Compose:

```bash
docker-compose up -d
```

For production deployment, consider:
- Using environment-specific .env files
- Setting up SSL/TLS certificates
- Configuring proper logging and monitoring
- Scaling with load balancers
- Setting up automated backups

## License

MIT License

## Contact

Sriram Vasamsetti - GitHub: [@SriramVasamsetti](https://github.com/SriramVasamsetti)
