# Devtective — Backend (API)

Project & task management API.
Stack: Java 17 · Spring Boot · PostgreSQL · Flyway · Maven · Docker.
Port: 8080 · Health: GET /health

Quick start (Docker) — easiest

```bash
# clone
git clone https://github.com/libr4/devtective-java.git
cd devtective-java

# run db + api (uses docker-compose.yml)
docker compose up --build
# API: http://localhost:8080
```

Flyway runs automatically on startup to create/migrate the schema.

## Run locally (without Docker)

Prereqs: Java 17, Maven (./mvnw), PostgreSQL running locally.

Create a database (example):

```sql
CREATE DATABASE devtective;
```

2. Export datasource vars (adjust as needed):

```sql
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/devtective
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
# (optional) export SPRING_PROFILES_ACTIVE=dev
```

3. Start the app

```sql
# option A: run directly
./mvnw spring-boot:run

# option B: build a jar and run
./mvnw clean package -DskipTests
java -jar target/devtective-*.jar
```

### Useful

* Run tests: `./mvnw test`

* Base URL (local): `http://localhost:8080`

* Health check: `GET /health` returns 200 when ready
