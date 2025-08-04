  ## elasticsearchapp 
This project demonstrates the powerful integration of Elasticsearch with Java for real-world data indexing and searching use cases.

## Technologies Used
Java 17: The core programming language.

Spring Boot 3.5.x: Framework for building robust, stand-alone, production-grade Spring applications.

Spring Data Elasticsearch: Spring's module for interacting with Elasticsearch.

Elasticsearch 8.x (via Docker): The distributed search and analytics engine.

Gradle: Build automation tool for dependency management and project building.

JUnit 5: The next generation of JUnit for writing and running tests.

Jackson: High-performance JSON processor for Java.

## Prerequisites
Before you get started, ensure you have the following installed:

Java 17+

Docker & Docker Compose: For running Elasticsearch locally.

Git: For cloning the repository.

Gradle (or use the provided ./gradlew wrapper)

## Elasticsearch Setup 
This section guides you through setting up Elasticsearch and loading sample data.

➤ docker-compose.yml

To start your Elasticsearch instance:

Navigate to the project root directory.

Run the command:

docker-compose up -d

(The -d flag runs it in detached mode)

Verify Elasticsearch is running by accessing its API:

curl http://localhost:9200

You should see a JSON response with Elasticsearch cluster information.

 ## Sample Data

The project includes pre-defined sample course data.

File Location: src/main/resources/sample-courses.json

 ## Elasticsearch Configuration

The application connects to your local Elasticsearch instance using the following properties in src/main/resources/application.properties:

spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.connection-timeout=5s

  ## Bulk-Index Sample Data
The CourseDataLoader.java class handles the initial data population.

Loader class: CourseDataLoader.java

Process: On application startup, this class reads sample-courses.json and pushes all course objects into the courses index in Elasticsearch.

Confirmation: A console message will confirm successful indexing:

Sample data indexed into Elasticsearch.

 ##  Search Service
The core of this application is a robust search service designed to query the courses index with various parameters.

It supports:

Full-text search (q): Searches keywords in both title and description fields using a multi_match query.

Range filters:

minAge, maxAge

minPrice, maxPrice

Exact filters:

category

type

Date filter: nextSessionDate (to show only courses on or after a given date).

Sorting logic:

Default: Ascending by nextSessionDate (soonest upcoming first).

sort=priceAsc: Sorts by price (low to high).

sort=priceDesc: Sorts by price (high to low).

Pagination: Supports page and size parameters for result set navigation.

 ##  REST API Endpoints
The application exposes the following RESTful endpoints:

GET /api/: Provides basic root information about the API.

GET /api/search: The main endpoint for performing course searches.

GET /api/health: A simple health check endpoint.

404 Handler: Catches and handles requests to unknown endpoints gracefully.

 ##  Testing & Verification
You can manually test the API endpoints using curl or any API client (like Postman/Insomnia).

Manual API Tests (via curl):

Basic Search (All courses):

curl "http://localhost:8080/api/search"

Filter by Category:

curl "http://localhost:8080/api/search?category=Science"

Filter by Price Range:

curl "http://localhost:8080/api/search?minPrice=30&maxPrice=60"

Filter by Age Range:

curl "http://localhost:8080/api/search?minAge=6&maxAge=9"

Sort by Price (Descending):

curl "http://localhost:8080/api/search?sort=priceDesc"

Full-text search with pagination:

curl "http://localhost:8080/api/search?q=math&page=0&size=3"

Date filter (courses on or after a specific date):

curl "http://localhost:8080/api/search?nextSessionDate=2025-08-01T00:00:00Z"
  ## Integration Testing
This project includes a suite of integration tests to ensure the search service and data indexing are working as expected.

Location: src/test/java/

Framework: The tests are written using JUnit 5 with Spring Boot Test.

Purpose: These tests validate the REST API endpoints and ensure the search functionality works correctly with various query parameters, filters, and sorting options.

How to Run Tests

From the project root directory, you can run all tests using the Gradle wrapper:

./gradlew test

## Part 4: Assignment B (Bonus) - Autocomplete Suggestions & Fuzzy Search

This section covers the implementation of enhanced search capabilities beyond basic filtering and sorting.

### 4.1. Autocomplete (Completion Suggester)

The application provides real-time autocomplete suggestions for course titles as users type. This is achieved using Elasticsearch's Completion Suggester.


During the application startup, when `sample-courses.json` is bulk-indexed, each course document now includes a `title` and a corresponding `suggest` sub-field that the Completion Suggester uses.

#### 4.1.2. Autocomplete Endpoint

A dedicated REST endpoint is exposed to provide autocomplete suggestions.

**Endpoint:** `GET /api/search/suggest?q={partialTitle}`

**Description:** This endpoint queries Elasticsearch using the `completion suggester` API. It returns up to 10 matching course titles that start with the provided `partialTitle`. The response is a JSON array of suggested course titles.

**Example Usage (using `curl`):**

* **Input:** Search for titles starting with "mat"
    ```bash
    curl "http://localhost:8080/api/search/suggest?q=mat"
    ```
* **Expected Output:**
    ```json
    ["Math Explorers", "Math Champions", "Math Puzzle Club"]
    ```
   ]

* **Input:** Search for titles starting with "eco"
    ```bash
    curl "http://localhost:8080/api/search/suggest?q=eco"
    ```
* **Expected Output:**
    ```json
    ["Eco Kids"]
    ```
   ]

* **Input:** Search for titles starting with "ani"
    ```bash
    curl "http://localhost:8080/api/search/suggest?q=ani"
    ```
* **Expected Output:**
    ```json
    ["Animal Kingdom"]
    ```
   ]

These suggestions are sourced from the `titleSuggest` field (which corresponds to the `suggest` sub-field in Elasticsearch) and utilize the Elasticsearch Completion Suggester.

#### 4.2.2. Documentation Examples

Here are examples demonstrating how a fuzzy query with a typo still returns the correct document(s):

* **Input:** Search for "mathh" (typo for "math")
    ```bash
    curl "http://localhost:8080/api/search?q=mathh"
    ```
* **Expected Output:** Courses matching: "Math Explorers", "Math Champions", "Math Puzzle Club", etc.
    (The response will include the full JSON structure of matching courses, similar to Assignment A's search results, but with these titles appearing due to fuzziness).

* **Input:** Search for "histroy" (typo for "history")
    ```bash
    curl "[http://8080/api/search?q=histroy](http://8080/api/search?q=histroy)"
    ```
* **Expected Output:** Courses matching: "History Detectives", "Ancient Egypt Adventure", etc.

* **Input:** Search for "muscal" (typo for "musical")
    ```bash
    curl "[http://8080/api/search?q=muscal](http://8080/api/search?q=muscal)"
    ```
* **Expected Output:** Courses matching: "Musical Minds", "Music Makers", etc.

---

