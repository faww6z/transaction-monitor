<img width="1920" height="991" alt="image" src="https://github.com/user-attachments/assets/4f7b9022-fb99-46ad-a675-4c08fda8f84a" />


<img width="1920" height="993" alt="image" src="https://github.com/user-attachments/assets/5fa09319-9dae-4fcf-9579-ce4c2e17bd90" />


```markdown
# Transaction Monitoring & Risk Scoring System

This project is a backend-focused transaction monitoring system inspired by 
real-world financial risk and fraud detection workflows.

It allows financial transactions to be ingested through an API, 
evaluates them using rule-based risk logic, and generates alerts for high-risk activity.
A simple web interface is provided to review and manage those alerts.

The entire system is containerized with Docker and deployed on AWS EC2.

---

## What This Project Does 

1. A transaction is submitted to the system (amount, type, balances, etc.)
2. The system analyzes the transaction using predefined risk rules
3. A risk score is calculated
4. High-risk transactions automatically generate alerts
5. Alerts can be reviewed and updated through a web interface
6. All data is stored in a PostgreSQL database

---

## Features

- REST API for ingesting transactions
- Rule-based risk scoring logic
- Automatic alert creation for high-risk transactions
- Web UI to review and manage alerts
- Persistent data storage using PostgreSQL
- Fully containerized with Docker
- Deployed on AWS EC2
- Database runs in a private container network

---

## Tools & Technologies Used

### Backend
- Java 21
- Spring Boot (Web, Data JPA, Validation)
- Hibernate / JPA

### Database
- PostgreSQL 16

### Frontend
- Thymeleaf (Spring MVC templates)

### DevOps / Infrastructure
- Docker
- Docker Compose
- AWS EC2 (Ubuntu)
- Linux / SSH

---

## System Architecture



Client (Browser / API Client)
|
v
Spring Boot Application
(API + Web UI)
|
v
PostgreSQL Database
(private Docker network)
```
````

- The application is publicly accessible via HTTP
- PostgreSQL is not exposed to the internet
- All database access goes through the backend service layer

---

## API Usage Example

### Create a Transaction

```bash
curl -X POST http://<EC2-IP>/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "type": "CASH_OUT",
    "amount": 15000,
    "oldBalanceOrig": 20000,
    "newBalanceOrig": 3000,
    "oldBalanceDest": 100,
    "newBalanceDest": 15100
  }'
````

### Example Response

```json
{
  "transactionId": 1,
  "riskScoreId": 1,
  "score": 100,
  "riskLevel": "HIGH",
  "triggeredRules": "AMOUNT_HIGH,TYPE_RISKY,BALANCE_MISMATCH"
}
```

---

## Web Interface

The project includes a simple web UI for managing alerts:

```
http://3.22.168.243/alerts
```

From the UI, alerts can be:

* Viewed
* Reviewed
* Marked as resolved or false positives

---

## How to Run Locally

### Prerequisites

* Docker
* Docker Compose

### Steps

1. Clone the repository

```bash
git clone <repo-url>
cd transaction-monitor
```

2. Start the application

```bash
docker compose up --build
```

3. Open the application

```
http://localhost
```

---

## Deployment Notes

* The application was deployed on AWS EC2 using Docker Compose
* PostgreSQL runs in a private container network and is not publicly accessible
* The live instance may not always be running to manage cloud costs

---

## Possible Future Improvements

* Authentication and role-based access
* Configurable risk rules
* Metrics and monitoring
* HTTPS with custom domain
* Automated testing

---

## Author

Fawwaz Ibrahim
