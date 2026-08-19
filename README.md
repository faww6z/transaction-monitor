# Transaction Monitoring & Risk Scoring System

![CI](https://github.com/faww6z/transaction-monitor/actions/workflows/ci.yml/badge.svg)

A backend-focused transaction monitoring system inspired by real-world financial
risk and fraud-detection workflows. Transactions are ingested through a REST API,
scored by a rule-based risk engine, and high-risk activity automatically raises
alerts that analysts review through a secured web interface. Every review decision
is recorded in an audit trail.

The whole system is containerized with Docker and has been deployed on AWS EC2.

<img width="1920" alt="Dashboard" src="https://github.com/user-attachments/assets/4f7b9022-fb99-46ad-a675-4c08fda8f84a" />

<img width="1920" alt="Alerts" src="https://github.com/user-attachments/assets/5fa09319-9dae-4fcf-9579-ce4c2e17bd90" />

---

## What it does

1. A transaction is submitted to the API (amount, type, origin/destination balances).
2. A rule-based engine scores it 0–100 and assigns a risk level (LOW / MEDIUM / HIGH).
3. MEDIUM and HIGH transactions automatically generate an alert.
4. Analysts sign in and review alerts, resolving them or marking false positives.
5. Every status change is recorded in an audit trail (who, when, from → to, note).
6. A dashboard summarizes throughput, the open-alert workload, and risk distribution.
7. All data is stored in PostgreSQL, with the schema managed by Flyway migrations.

---

## Risk rules

The engine composes independent rules (each contributes points if triggered):

| Rule | Triggers when | Points |
|------|---------------|--------|
| `AMOUNT_HIGH` | amount ≥ $10,000 | +50 |
| `TYPE_RISKY` | type is TRANSFER or CASH_OUT | +30 |
| `BALANCE_MISMATCH` | balances don't reconcile (beyond a $1 tolerance) | +40 |

The score is capped at 100. Level thresholds: **MEDIUM ≥ 40, HIGH ≥ 70.** Rules and
thresholds are wired in `RiskConfig`, so adding a rule is a one-line change.

---

## Tech stack

- **Backend:** Java 21, Spring Boot 4 (Web MVC, Data JPA, Validation, Security)
- **Database:** PostgreSQL 16, schema managed by **Flyway**
- **Frontend:** Thymeleaf server-rendered templates
- **Money:** `BigDecimal` throughout (`numeric(19,2)`)
- **Build / CI:** Maven, GitHub Actions
- **Infra:** Docker, Docker Compose, AWS EC2

---

## Security

- **`/api/**`** — stateless HTTP Basic auth (for machine-to-machine ingestion).
- **Web UI** — browser form login with CSRF protection.
- A single demo analyst user guards both. Credentials default to `analyst` / `analyst`
  and can be overridden with `APP_SECURITY_USERNAME` / `APP_SECURITY_PASSWORD`.
  Passwords are BCrypt-hashed at startup.

---

## Run it locally

Prerequisites: Docker + Docker Compose.

```bash
git clone https://github.com/faww6z/transaction-monitor
cd transaction-monitor
docker compose up --build
```

The app is served at **http://localhost**. Sign in at `/` with `analyst` / `analyst`.

### Ingest a transaction (API requires Basic auth)

```bash
curl -X POST http://localhost/api/transactions \
  -u analyst:analyst \
  -H "Content-Type: application/json" \
  -d '{
    "type": "CASH_OUT",
    "amount": 15000,
    "oldBalanceOrig": 20000,
    "newBalanceOrig": 3000,
    "oldBalanceDest": 100,
    "newBalanceDest": 200
  }'
```

Example response:

```json
{
  "transactionId": 1,
  "riskScoreId": 1,
  "score": 100,
  "riskLevel": "HIGH",
  "triggeredRules": "AMOUNT_HIGH,TYPE_RISKY,BALANCE_MISMATCH"
}
```

### Web interface

- `/` — dashboard (totals, open alerts, risk distribution)
- `/alerts` — alert queue, filterable by status
- `/alerts/{id}` — alert detail, review controls, and audit trail

---

## Tests

```bash
./mvnw test
```

The risk engine and all rules are covered by fast, database-free unit tests. CI runs
the full suite (including the Spring context) against a PostgreSQL service on every
push and pull request.

---

## Possible future improvements

- Asynchronous ingestion for higher throughput
- Configurable / DB-driven risk rules
- Role separation (dedicated ingest service account vs. analysts)
- Metrics and monitoring (Actuator)
- HTTPS with a custom domain

---

## Author

Fawwaz Ibrahim
