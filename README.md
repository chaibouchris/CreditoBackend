# CreditoBackend

CreditoBackend is a backend service built with Spring Boot. The system provides a flexible, scalable, and API-based mechanism for integrating with external "black box" algorithms such as credit scoring models.

## Key Features

- **External Service Orchestration:** Calls to external services with advanced error handling and retry mechanisms.
- **Caching Mechanism:** Utilizes Redis for temporary data storage to enhance performance.
- **Custom Error Handling:** Intelligent error management including exceptional cases.
- **Exponential Backoff Support:** Manages wait times between retry attempts.

## Technologies Used

- **Backend:** Java 23, Spring Boot, Spring Data JPA, Spring Cache, Redis, Resilience4j
- **Testing:** JUnit 5, Mockito, MockWebServer

## About the SCORING Service

In this project, an external SCORING service is used solely for testing the system's capabilities. These calls help create tests that demonstrate the system's behavior in various scenarios. It's important to note that this service is not related to any real SCORING mechanism but effectively showcases the system's robustness and error-handling capabilities.

## Installation Instructions

### Prerequisites

- Java JDK 23
- Maven
- Redis (optional - the system supports an in-memory cache fallback if Redis is unavailable)

### Setup and Running the Application

1. **Clone the repository:**
   Make sure you are in the directory where you want to clone the project.
   ```bash
   git clone https://github.com/chaibouchris/CreditoBackend.git
   ```

2. **Build the project:**  
   Ensure you are in the project root directory where the `mvnw` file is located.
   - **On Linux or macOS:**
     ```bash
     ./mvnw clean install
     ```
   - **On Windows (PowerShell or CMD):**
     ```bash
     mvnw clean install
     ```
   - **On Windows (Git Bash):**
     ```bash
     ./mvnw clean install
     ```

   > **Note:** If you encounter the following error:
   > ```
   > Cannot find path '.mvn/wrapper/maven-wrapper.properties' because it does not exist.
   > ```
   > Navigate to the project root directory and run:
   > ```bash
   > mvn -N io.takari:maven:wrapper
   > ```
   > This command will regenerate the missing Maven Wrapper files.

3. **Run the server:**  
   Ensure you are still in the project directory:
   - **On Linux or macOS:**
     ```bash
     ./mvnw spring-boot:run
     ```
   - **On Windows (PowerShell or CMD):**
     ```bash
     mvnw spring-boot:run
     ```
   - **On Windows (Git Bash):**
     ```bash
     ./mvnw spring-boot:run
     ```

   The server will be available at: [http://localhost:8080](http://localhost:8080)

### Running Redis with Docker (Optional)

If you prefer running Redis using Docker:

```bash
docker run --name credito-redis -p 6379:6379 -d redis
```

> **Note:** Running Redis is optional. The system includes a smart fallback mechanism that switches to an in-memory cache if Redis is unavailable.

### Configuration

Key configuration files:
- `application.properties` - General application settings.
- `application-test.properties` - Configuration for the testing environment.

## Running the Application and Testing API Calls

1. Ensure the server is running on `localhost:8080`.

2. **Example request to the SCORING service:**

   **Using cURL:**
   ```bash
   curl -X POST http://localhost:8080/api/service/scoring \
     -H "Content-Type: application/json" \
     -d '{"customerId": "12345"}'
   ```

   **Using Postman:**
   - Open Postman.
   - Click **New Request** and select **POST**.
   - Enter the URL: `http://localhost:8080/api/service/scoring`
   - Go to the **Headers** tab:
     - Key: `Content-Type`, Value: `application/json`
   - Go to the **Body** tab, select **raw** and choose **JSON** from the dropdown.
   - Add the following JSON:
     ```json
     {
       "customerId": "12345"
     }
     ```
   - Click **Send**.

3. **Example request to the ANALYZER service:**

   **Using cURL:**
   ```bash
   curl -X POST http://localhost:8080/api/service/analyzer \
     -H "Content-Type: application/json" \
     -d '{"data": "sample"}'
   ```

   **Using Postman:**
   - Open Postman.
   - Click **New Request** and select **POST**.
   - Enter the URL: `http://localhost:8080/api/service/analyzer`
   - Go to the **Headers** tab:
     - Key: `Content-Type`, Value: `application/json`
   - Go to the **Body** tab, select **raw** and choose **JSON** from the dropdown.
   - Add the following JSON:
     ```json
     {
       "data": "sample"
     }
     ```
   - Click **Send**.

4. **Viewing API Responses:**

   Example of a successful response:
   ```json
   {
     "customerId": "12345",
     "creditScore": 750
   }
   ```

   Example of an error response:
   ```json
   {
     "error": "Service scoring failed after 3 attempts"
   }
   ```

## Running Tests

To run tests, make sure you are in the root directory of the project where the `mvnw` file is located.

### **On Linux or macOS:**
```bash
./mvnw test
```

### **On Windows (PowerShell or CMD):**
```bash
mvnw test
```

> **Note:** If you are using Git Bash on Windows, you can use the same command as in Linux/macOS:
```bash
./mvnw test
```

Ensure you are in the project folder before running these commands:
```bash
cd /path/to/CreditoBackend
```
