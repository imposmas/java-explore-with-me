# Explore With Me - Main & Stats Services

## Overview
Explore With Me is a two-module application consisting of:
- **ewm-main-service** — manages events, categories, users, compilations, participation requests.
- **ewm-stats-service** — collects and provides view statistics for events.

Both services run separately and each uses its own PostgreSQL database.

## Modules

### 1. ewm-main-service
Handles:
- Public event search
- User event management
- Participation request flow
- Admin event moderation
- Categories
- Compilations

### 2. ewm-stats-service
Handles:
- Saving endpoint hits
- Aggregating view statistics
- Querying view counts for events

## Swagger Documentation

### Main Service Swagger
[ewm-main-service-spec.json](./ewm-main-service-spec.json)

### Stats Service Swagger
[ewm-stat-service-spec.json](./ewm-stat-service-spec.json)

---

## Database Schema

### Main Service DB (`ewm`)

- **users**: user accounts  
- **categories**: event categories  
- **locations**: coordinates for events  
- **events**: main event entity  
- **compilations**: event collections  
- **compilation_events**: many-to-many link  
- **participation_requests**: event participation requests  

### Stats Service DB (`stats`)

- **endpoint_hits**: raw hits  
- **stats_view**: aggregated views  

![Main Service DB structure](db-main-service.png)

---

## Running with Docker Compose

The system uses two PostgreSQL containers and two service containers.

To start all:

```bash
docker-compose up --build
```

Services:
- Main service → http://localhost:8080
- Stats service → http://localhost:9090
- Postgres main → localhost:6542  
- Postgres stats → localhost:6541

---

## Repository Structure

```
ewm-main-service/
ewm-stats/
docker-compose.yml
README.md
```

---

## Technologies
- Java 21
- Spring Boot 3
- JPA/Hibernate
- PostgreSQL 15
- Docker & Docker Compose
- Lombok
- RestTemplate client communication
- Multi-module Maven project

---
