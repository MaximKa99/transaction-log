# Transaction Log Application

A Spring Boot application that manages mathematical formulas with real-time calculation capabilities using WebSocket subscriptions.

## Features

- **Formula Management**: Create, read, update, and delete mathematical formulas via REST API
- **Real-time Calculations**: Subscribe to formulas and receive calculation results in real-time via WebSocket

## Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Gradle (optional, wrapper included)

## Architecture

The application consists of:
- **REST API**: HTTP endpoints for formula CRUD operations
- **WebSocket Server**: Real-time communication for formula subscriptions
- **Subscription Manager**: Manages WebSocket subscriptions and calculation processing
- **PostgreSQL Database**: Stores formulas with expressions and variables

## Getting Started

### Using Docker Compose (Recommended)

1. **Start the application and database**:
   ```bash
   docker-compose up --build
   ```

   This will:
   - Start a PostgreSQL database
   - Build and start the Spring Boot application
   - Expose the application on port `8080`
   - Expose debug port `50005` for remote debugging

2. **Verify the application is running**:
   ```bash
   curl http://localhost:8080/formula/1
   ```

### Manual Setup

1. **Start PostgreSQL**:
   ```bash
   docker run -d \
     --name postgres \
     -e POSTGRES_PASSWORD=postgres \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_DB=postgres \
     -p 5432:5432 \
     postgres
   ```

2. **Update application configuration** (if needed):
   Edit `src/main/resources/application.yml` to match your PostgreSQL connection details.

3. **Build and run the application**:
   ```bash
   ./gradlew build
   ./gradlew bootRun
   ```

## API Endpoints

### Formula Management

#### Create a Formula
```http
POST /formula
Content-Type: application/json

{
  "expression": "x * 2 + 5",
  "variables": ["x"]
}
```

**Response:**
```json
{
  "id": 1,
  "expression": "x * 2 + 5",
  "variables": ["x"]
}
```

#### Get a Formula
```http
GET /formula/{id}
```

**Response:**
```json
{
  "id": 1,
  "expression": "x * 2 + 5",
  "variables": ["x"]
}
```

#### Update a Formula
```http
PUT /formula
Content-Type: application/json

{
  "id": 1,
  "expression": "x * 3 + 10",
  "variables": ["x"]
}
```

**Response:**
```json
{
  "id": 1,
  "expression": "x * 3 + 10",
  "variables": ["x"]
}
```

#### Delete a Formula
```http
DELETE /formula/{id}
```

**Response:**
```json
{
  "id": 1,
  "expression": "x * 2 + 5",
  "variables": ["x"]
}
```

## WebSocket API

### Subscribe to Formula Calculations

Connect to the WebSocket endpoint and send a subscription request:

**Endpoint:** `ws://localhost:8080/subscribe`

**Message Format:**
```json
{
  "formulaId": 1
}
```

**Response:**
The server will send calculation results every second in the following format:
```json
{
  "result": 7.0
}
```

The calculation uses an incremental sequence of numbers, and all variables in the formula are set to the same value.

## Example Usage

### Using cURL

1. **Create a formula**:
   ```bash
   curl -X POST http://localhost:8080/formula \
     -H "Content-Type: application/json" \
     -d '{"expression": "x^2 + 2*x + 1", "variables": ["x"]}'
   ```

2. **Get the formula**:
   ```bash
   curl http://localhost:8080/formula/1
   ```

3. **Update the formula**:
   ```bash
   curl -X PUT http://localhost:8080/formula \
     -H "Content-Type: application/json" \
     -d '{"id": 1, "expression": "x^3 + x", "variables": ["x"]}'
   ```

### Using Postman for WebSocket Testing

We recommend using **Postman** to test WebSocket connections. Postman provides an intuitive interface for WebSocket testing.

1. **Open Postman** and create a new WebSocket request
2. **Connect** to: `ws://localhost:8080/subscribe`
3. **Send a subscription message**:
   ```json
   {
     "formulaId": 1
   }
   ```
4. **Receive real-time results**: Postman will display calculation results as they arrive from the server

## Formula Expression Syntax

The application uses [exp4j](https://www.objecthunter.net/exp4j/) for expression evaluation. Supported features include:

- **Basic operations**: `+`, `-`, `*`, `/`, `^` (power)
- **Functions**: `sin`, `cos`, `tan`, `log`, `sqrt`, etc.
- **Variables**: Any variable names defined in the `variables` set
- **Constants**: `pi`, `e`

**Examples:**
- `x * 2 + 5`
- `sin(x) + cos(x)`
- `x^2 + 2*x + 1`
- `log(x) * sqrt(x)`

## Configuration

### Application Properties

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/postgres
    username: postgres
    password: postgres
```

### Docker Compose

The `docker-compose.yaml` file configures:
- Application service on ports `8080` (HTTP) and `50005` (debug)
- PostgreSQL service on port `5432`

## Development

### Building the Project

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

### Remote Debugging

The application exposes a debug port on `50005`. Connect your IDE debugger to:
- Host: `localhost`
- Port: `50005`

## Project Structure

```
src/main/java/com/epam/
├── Main.java                    # Application entry point
├── Calculator.java              # Formula calculation logic
├── controller/
│   └── FormulaController.java   # REST API endpoints
├── service/
│   ├── FormulaService.java      # Formula business logic
│   └── SubscriptionManager.java # WebSocket subscription management
├── entity/
│   └── Formula.java             # Formula entity
├── config/
│   └── WebSocketConfig.java     # WebSocket configuration
├── handler/
│   └── SubscribeHandler.java    # WebSocket subscribe handler
└── view/
    ├── FormulaCreation.java     # DTO for formula creation
    ├── FormulaUpdate.java       # DTO for formula update
    ├── SubscribeRequest.java    # DTO for subscription request
    └── CalculationResult.java   # DTO for calculation results
```


