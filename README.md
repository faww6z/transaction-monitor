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

## Roadmap

Grouped roughly by payoff. The detection items come first: the engine currently
scores every transaction in isolation, and that is the widest gap between this
and a real monitoring system.

**Detection**

- **Give transactions an account identity.** They carry no sender or customer
  reference today, so nothing can be correlated across time. This is the
  keystone — most items below depend on it.
- **Velocity and aggregation rules** over a rolling window per account. Without
  them, structuring — splitting $50,000 into six $9,000 transfers to stay under
  a reporting threshold — passes cleanly, because no single transaction looks
  unusual.
- **Behavioural baselines**, scoring deviation from an account's own history
  instead of against fixed global thresholds.
- **Watchlist and sanctions screening** on the counterparty.

**Closing the feedback loop**

- **Measure per-rule precision from analyst dispositions.** Every alert records
  which rules fired and every review now records its outcome, so "BALANCE_MISMATCH
  is 80% false positives" is one query away. That number should drive tuning.
- **Alert-rate dashboards per rule**, so a bad threshold shows up within a day
  rather than being discovered by a drowning analyst.

**Configurable rules**

- Lift thresholds out of `RiskConfig` into external configuration, then into the
  database behind an admin screen.
- **Version the rule set** and stamp each score with the version that produced
  it, so a historical alert stays explainable after thresholds move. That is an
  audit requirement, not a nicety.

**Scale**

- Paginate `/alerts`, index `status` and `created_at`, and resolve the N+1 on the
  queue view.
- Asynchronous ingestion via a queue, so scoring never blocks the caller.
- Read endpoints on the API (`GET /api/transactions/{id}`, risk score lookup).

**Analyst workflow**

- **Cases** — group an account's related alerts into one investigation.
- Bulk triage, plus queue ageing and SLA indicators.
- Export a case file for regulatory reporting.

**Quality and ops**

- Testcontainers for integration tests, so `./mvnw verify` is self-contained
  rather than depending on a CI service container.
- **Mutation testing (PIT) on the risk package.** A green suite already missed a
  live scoring bug once, because the test fixture shared an assumption with the
  code under test; mutation coverage is what catches that class of blind spot.
- Actuator metrics, structured logging, HTTPS on a custom domain, and secrets
  moved out of `docker-compose.yml`.

---

## Author

Fawwaz Ibrahim
