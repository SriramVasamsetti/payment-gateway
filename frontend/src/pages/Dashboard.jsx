import { useEffect, useState } from "react";
import axios from "axios";

const API_BASE = "http://localhost:8000";

export default function Dashboard() {
  const [payments, setPayments] = useState([]);

  useEffect(() => {
    axios
      .get(`${API_BASE}/api/v1/payments`, {
        headers: {
          "X-Api-Key": "key_test_abc123",
          "X-Api-Secret": "secret_test_xyz789",
        },
      })
      .then((res) => setPayments(res.data))
      .catch(() => setPayments([]));
  }, []);

  const totalTransactions = payments.length;

  const successfulPayments = payments.filter(
    (p) => p.status === "success"
  );

  const totalAmount = successfulPayments.reduce(
    (sum, p) => sum + p.amount,
    0
  );

  const successRate =
    totalTransactions === 0
      ? 0
      : Math.round(
          (successfulPayments.length / totalTransactions) * 100
        );

  return (
    <div data-test-id="dashboard">
      <div data-test-id="api-credentials">
        <div>
          <label>API Key</label>
          <span data-test-id="api-key">key_test_abc123</span>
        </div>
        <div>
          <label>API Secret</label>
          <span data-test-id="api-secret">secret_test_xyz789</span>
        </div>
      </div>

      <div data-test-id="stats-container">
        <div data-test-id="total-transactions">
          {totalTransactions}
        </div>
        <div data-test-id="total-amount">
          ₹{totalAmount}
        </div>
        <div data-test-id="success-rate">
          {successRate}%
        </div>
      </div>
    </div>
  );
}
