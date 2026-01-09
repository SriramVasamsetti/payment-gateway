import { useEffect, useState } from "react";
import axios from "axios";

const API_BASE = "http://localhost:8000";

export default function Transactions() {
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

  return (
    <table data-test-id="transactions-table">
      <thead>
        <tr>
          <th>Payment ID</th>
          <th>Order ID</th>
          <th>Amount</th>
          <th>Method</th>
          <th>Status</th>
          <th>Created</th>
        </tr>
      </thead>
      <tbody>
        {payments.map((p) => (
          <tr
            key={p.id}
            data-test-id="transaction-row"
            data-payment-id={p.id}
          >
            <td data-test-id="payment-id">{p.id}</td>
            <td data-test-id="order-id">{p.order_id}</td>
            <td data-test-id="amount">{p.amount}</td>
            <td data-test-id="method">{p.method}</td>
            <td data-test-id="status">{p.status}</td>
            <td data-test-id="created-at">
              {p.created_at}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
