# Payments API

Spring Boot 4.1.0, Java 21 and Maven. Payment operations are stubbed with an in-memory store.

## Prerequisites

- JDK 21 with `JAVA_HOME` configured for local builds.
- Git to clone the repository.
- Docker with Linux containers enabled for container builds and runs.
- Internet access for the first build to download Maven and dependencies.

The Maven wrapper is included; a global Maven installation is optional. Docker
builds use their own JDK and Maven, so no local Java installation is required for
the Docker-only workflow.

## Get the code

```powershell
git clone https://github.com/akhan-msft/payments-api.git
Set-Location payments-api
```

## Build and run locally

From the project root in PowerShell:

```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

Or use an installed Maven CLI: `mvn clean verify` and `mvn spring-boot:run`.

On macOS/Linux, use `sh mvnw clean verify` and `sh mvnw spring-boot:run`.
The build runs the tests and produces an executable JAR. You can also run it with:

```powershell
java -jar target\payments-api-0.0.1-SNAPSHOT.jar
```

The application listens on port **8080**. Stop it with Ctrl+C. To use another port
with the JAR, append `--server.port=8081`.

## Check the endpoints

In another PowerShell terminal:

```powershell
Invoke-RestMethod http://localhost:8080/test
Invoke-RestMethod http://localhost:8080/actuator/health
Invoke-RestMethod http://localhost:8080/actuator/info
```

| Endpoint | Response |
| --- | --- |
| `GET /test` | `{"version":"0.0.1-SNAPSHOT"}` |
| `GET /actuator/health` | `{"status":"UP"}` |
| `GET /actuator/info` | Build metadata, including the project version |

The version comes from `pom.xml` via the Maven `build-info` goal, not a hardcoded
controller value. Run through Maven or build the JAR first so this metadata exists.

## Payments API (stub)

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/payments` | Create a payment (`201 Created`, status `PENDING`) |
| `GET` | `/api/payments` | List payments |
| `POST` | `/api/payments/{id}/cancel` | Cancel a payment (`404` if unknown) |

Payment fields: `id`, `customerId`, `productId`, `amount`, `currency`, `method`
(`CARD`, `BANK_TRANSFER`, `WALLET`), `status` (`PENDING`, `COMPLETED`, `CANCELLED`)
and `createdAt`. Invalid create requests return `400`.

```powershell
$body = '{"customerId":"cust-1","productId":"sku-42","amount":49.99,"currency":"USD","method":"CARD"}'
$payment = Invoke-RestMethod -Method Post http://localhost:8080/api/payments -ContentType application/json -Body $body
Invoke-RestMethod http://localhost:8080/api/payments
Invoke-RestMethod -Method Post "http://localhost:8080/api/payments/$($payment.id)/cancel"
```

Data is held in memory and is lost when the application restarts.

## Build and run with Docker

```powershell
docker build -t payments-api:0.0.1 .
docker run --rm --name payments-api -p 127.0.0.1:8080:8080 payments-api:0.0.1
```

The multi-stage build compiles and tests the application using the Maven wrapper
and a Java 21 JDK. The runtime image uses a Java 21 JRE and a non-root user.

Visit the same endpoints on `localhost:8080`. Stop the container with Ctrl+C, or
run `docker stop payments-api` in another terminal. `--rm` removes the container
after it stops.

Stop the local application before running Docker on port 8080, or map a different
host port with `-p 127.0.0.1:8081:8080` and use `http://localhost:8081`.

## Scope

This is a local-development scaffold, not a production payment service. The
payment endpoints are stubs: no real payment processing, persistence,
authentication, or authorization is implemented.
Only the Actuator `health` and `info` endpoints are exposed. Do not use this
scaffold to accept real payment data.
