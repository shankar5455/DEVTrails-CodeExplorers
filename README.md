# EarnSafe — AI-Powered Parametric Insurance for Gig Workers

EarnSafe is a production-grade full-stack platform that provides **AI-powered parametric insurance** for gig workers in India. It automatically detects real-world weather disruptions using live data, calculates risk using machine learning, triggers claims, detects fraud intelligently, and processes payouts — all without manual intervention.

## Architecture

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│   React (Vite)  │────▶│  Spring Boot API  │────▶│  Python FastAPI  │
│   Port: 5173    │     │   Port: 8080      │     │   Port: 8000     │
│                 │     │                   │     │                  │
│  • User Dashboard│    │  • JWT Auth       │     │  • Risk Model    │
│  • Admin Panel   │    │  • Policy Engine  │     │    (RandomForest)│
│  • Charts        │    │  • Claim Engine   │     │  • Fraud Model   │
│                 │     │  • Weather Fetch  │     │    (IsolationForest)│
└─────────────────┘     │  • Scheduler      │     └─────────────────┘
                        │  • Payout (Razorpay)│
                        └────────┬───────────┘
                                 │
                        ┌────────▼───────────┐
                        │      MySQL DB       │
                        │   earnsafe_db       │
                        └────────────────────┘
                                 │
                        ┌────────▼───────────┐
                        │  OpenWeather API    │
                        │  (Real-time data)   │
                        └────────────────────┘
```

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Frontend | React 19 + Vite, Recharts, Lucide Icons |
| Backend | Spring Boot 3.2, Java 17, Spring Security |
| AI/ML Service | Python FastAPI, scikit-learn |
| Database | MySQL 8.0 |
| Authentication | JWT (JSON Web Tokens) |
| Weather Data | OpenWeather API |
| Payments | Razorpay (test mode) |

## Features

### Core Features
- **JWT Authentication** with role-based access (USER, ADMIN)
- **AI Risk Engine** — ML-based risk scoring using RandomForestRegressor
- **Dynamic Pricing** — Weekly premiums calculated from AI model output
- **Parametric Triggers** — Automatic claim creation from real-time weather events
- **AI Fraud Detection** — Isolation Forest anomaly detection (location mismatch, claim frequency, weather inconsistency)
- **Automated Claims** — No manual trigger; scheduled weather checks every 30 minutes
- **Razorpay Payouts** — Automatic payout processing after claim approval

### Dashboards
- **User Dashboard**: Active policy, weekly premium, claims history, earnings protected, risk score
- **Admin Dashboard**: Total users, claims analytics, fraud detection stats, premium/payout metrics

## Prerequisites

- **Java 17+** and **Maven 3.8+**
- **Node.js 18+** and **npm 9+**
- **Python 3.10+** and **pip**
- **MySQL 8.0+**

## Setup Instructions

### 1. Database Setup

```bash
mysql -u root -p
CREATE DATABASE earnsafe_db;
```

### 2. Backend (Spring Boot)

```bash
cd backend

# Update configuration (optional)
# Edit src/main/resources/application.properties with your:
# - MySQL credentials
# - OpenWeather API key (get free key at https://openweathermap.org/api)
# - Razorpay test keys (get at https://razorpay.com)

# Build and run
mvn clean install -DskipTests
mvn spring-boot:run
```

The backend starts at `http://localhost:8080`

### 3. AI Service (Python FastAPI)

```bash
cd ai-service

# Create virtual environment (optional)
python -m venv venv
source venv/bin/activate  # Linux/Mac
# venv\Scripts\activate   # Windows

# Install dependencies
pip install -r requirements.txt

# Run the service
uvicorn main:app --host 0.0.0.0 --port 8000
```

The AI service starts at `http://localhost:8000`

### 4. Frontend (React)

```bash
cd frontend

# Install dependencies
npm install

# Run development server
npm run dev
```

The frontend starts at `http://localhost:5173`

## Default Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@earnsafe.com | admin123 |
| User | rahul@example.com | password123 |
| User | priya@example.com | password123 |
| User | amit@example.com | password123 |

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT |

### Policies
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/policies/purchase` | Purchase weekly policy |
| GET | `/api/policies/my` | Get user's policies |
| GET | `/api/policies/active` | Get active policy |

### Claims
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/claims/my` | Get user's claims |

### Dashboard
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard/user` | User dashboard data |

### Weather
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/weather?lat=&lon=` | Fetch weather data |

### Admin (ADMIN role required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/dashboard` | Admin analytics |
| GET | `/api/admin/claims` | All claims |
| GET | `/api/admin/policies` | All policies |
| POST | `/api/admin/trigger-check` | Manual weather check |

### AI Service
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Health check |
| POST | `/predict/risk` | Risk score prediction |
| POST | `/predict/fraud` | Fraud detection |

## Database Schema

### Tables
- **users** — User accounts with location data
- **policies** — Weekly insurance policies with AI-calculated premiums
- **claims** — Automatically triggered claims with fraud analysis
- **weather_data** — Historical weather data log
- **payouts** — Razorpay payout transaction records

## Scheduled Jobs

| Job | Frequency | Description |
|-----|-----------|-------------|
| Weather Check | Every 30 minutes | Fetches weather for all active policy holders |
| Parametric Trigger | Every 30 minutes | Detects disruptions and creates claims |
| Policy Expiry | Every 30 minutes | Marks expired policies |

## Disruption Thresholds

| Event | Threshold | Payout % |
|-------|-----------|----------|
| Heavy Rain | > 15 mm/h | 30% of coverage |
| Extreme Heat | > 42°C | 20% of coverage |
| Storm | > 20 m/s wind | 50% of coverage |
| Flood | > 30 mm/h rain | 60% of coverage |

## AI Models

### Risk Prediction (RandomForestRegressor)
- **Features**: temperature, humidity, rainfall, wind_speed, latitude, longitude, historical_claims, AQI
- **Output**: risk_score (0-100), suggested_premium, risk_level

### Fraud Detection (IsolationForest)
- **Features**: claim_amount, location coordinates, weather data, claim_frequency, total_claims, location_distance
- **Detects**: Location mismatch, abnormal claim frequency, weather inconsistency