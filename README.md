# 🎵 Music & People Discovery Service

> A simple, clean Java Spring Boot REST API built with Maven designed for beginners learning Java.
> Connect people with similar music taste, discover new music, and explore community favorites.

---

## 📑 Table of Contents
1. [Core Features & Overview](#core-features--overview)
2. [Architecture Explained for Beginners](#architecture-explained-for-beginners)
3. [Prerequisites & Running the App](#prerequisites--running-the-app)
4. [Interactive Swagger UI & H2 Database Console](#interactive-swagger-ui--h2-database-console)
5. [Complete API Reference](#complete-api-reference)
   - [Music Endpoints](#music-endpoints)
   - [User Endpoints](#user-endpoints)
   - [Discovery Endpoints](#discovery-endpoints)
6. [Automated Testing](#automated-testing)
7. [Project Structure](#project-structure)

---

## 🌟 Core Features & Overview

- **Music Catalog Management**: Full CRUD operations for songs with artist, genre, and link.
- **User Library Management**: Create users and manage personal collections of favorite songs.
- **People Discovery**: Discovers users with overlapping music taste using Jaccard taste similarity algorithms.
- **Music Discovery**: Recommends songs loved by people with similar taste that you haven't heard yet.
- **Zero-Setup Database**: Runs on an embedded in-memory H2 database with automatic schema generation.
- **Pre-Loaded Sample Data**: Automatically seeds popular songs and users on startup so you can test right away!

---

## 🏗️ Architecture Explained for Beginners

Spring Boot applications follow a layered architecture to keep code organized and maintainable:

```
┌────────────────────────────────────────────────────────┐
│                   HTTP Client / Browser                │
└───────────────────────────┬────────────────────────────┘
                            │ (JSON over HTTP)
                            ▼
┌────────────────────────────────────────────────────────┐
│                Controller Layer (@RestController)       │
│  - MusicController, UserController                     │
│  - Handles incoming HTTP requests and returns JSON     │
└───────────────────────────┬────────────────────────────┘
                            ▼
┌────────────────────────────────────────────────────────┐
│                 Service Layer (@Service)               │
│  - MusicService, UserService, DiscoveryService         │
│  - Contains core business logic & taste calculation    │
└───────────────────────────┬────────────────────────────┘
                            ▼
┌────────────────────────────────────────────────────────┐
│               Repository Layer (@Repository)           │
│  - MusicRepository, UserRepository                     │
│  - Spring Data JPA handles SQL queries automatically   │
└───────────────────────────┬────────────────────────────┘
                            ▼
┌────────────────────────────────────────────────────────┐
│              Database Layer (H2 In-Memory DB)          │
└────────────────────────────────────────────────────────┘
```

### Key Spring Annotations Used:
- `@SpringBootApplication`: The bootstrap annotation that starts the Spring Boot container.
- `@RestController`: Marks classes that handle HTTP requests and serialize return values to JSON.
- `@GetMapping`, `@PostMapping`, `@PatchMapping`, `@DeleteMapping`: Map specific HTTP verbs to methods.
- `@PathVariable`: Extracts path parameters from URLs (e.g., `/music/{music-id}`).
- `@RequestBody`: Deserializes the incoming JSON payload into a Java object.
- `@JsonProperty`: Maps JSON keys like `"artist-name"` to Java camelCase variables like `artistName`.
- `@Entity` & `@Table`: Map Java classes to database tables.
- `@RestControllerAdvice`: Intercepts exceptions globally and returns clean JSON error responses.

---

## 🚀 Prerequisites & Running the App

### Requirements:
- **Java 17+** (Java 21 tested and recommended)
- **Maven 3.8+** (or use the included `./mvnw` wrapper)

### Run the Service:
```bash
# Using the Maven wrapper:
./mvnw spring-boot:run

# Or with system Maven:
mvn spring-boot:run
```
The server will start at: **`http://localhost:8080`**

### Run the Interactive Demo:
In a separate terminal window:
```bash
chmod +x curl-test.sh
./curl-test.sh
```

---

## 🌐 Interactive Swagger UI & H2 Database Console

### 1. Swagger UI (API Playground)
Open your browser and navigate to:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

You can test all endpoints directly from your browser without typing curl commands!

### 2. H2 Database Web Console
To visually inspect tables (`MUSICS`, `USERS`, `USER_MUSIC`):
👉 **[http://localhost:8080/h2-console](http://localhost:8080/h2-console)**

- **JDBC URL**: `jdbc:h2:mem:musicdb`
- **User Name**: `sa`
- **Password**: *(leave blank)*
- Click **Connect**

---

## 📡 Complete API Reference

### 🎵 Music Endpoints

#### 1. Get entire music catalog
- **Method**: `GET`
- **URL**: `/music`
- **Optional Query**: `/music?genre=Electronic`
- **Response**: `200 OK`
```json
[
  {
    "id": 1,
    "artist-name": "Daft Punk",
    "genre-name": "Electronic",
    "link": "https://open.spotify.com/track/4Cee00Dyq308ZgfSd9337b"
  }
]
```

#### 2. Get a song by ID
- **Method**: `GET`
- **URL**: `/music/{music-id}`
- **Response**: `200 OK` or `404 Not Found`
```json
{
  "id": 1,
  "artist-name": "Daft Punk",
  "genre-name": "Electronic",
  "link": "https://open.spotify.com/track/4Cee00Dyq308ZgfSd9337b"
}
```

#### 3. Add a new song
- **Method**: `POST`
- **URL**: `/music`
- **Body**:
```json
{
  "artist-name": "Pink Floyd",
  "genre-name": "Progressive Rock",
  "link": "https://open.spotify.com/track/3TO7bbrV2Vp0wO8iG3bpqA"
}
```
- **Response**: `201 Created`

#### 4. Partially update a song
- **Method**: `PATCH`
- **URL**: `/music/{music-id}`
- **Body**:
```json
{
  "genre-name": "Classic Rock"
}
```
- **Response**: `200 OK`

#### 5. Delete a song
- **Method**: `DELETE`
- **URL**: `/music/{music-id}`
- **Response**: `204 No Content`

---

### 👤 User Endpoints

#### 1. Get entire user catalog
- **Method**: `GET`
- **URL**: `/user`
- **Response**: `200 OK`
```json
[
  {
    "id": 1,
    "name": "Alice",
    "music": [1, 3, 5]
  },
  {
    "id": 2,
    "name": "Bob",
    "music": [1, 4, 5]
  }
]
```

#### 2. Get a single user
- **Method**: `GET`
- **URL**: `/user/{user-id}`
- **Response**: `200 OK`
```json
{
  "id": 1,
  "name": "Alice",
  "music": [1, 3, 5]
}
```

#### 3. Create a new user
- **Method**: `POST`
- **URL**: `/user`
- **Body**:
```json
{
  "name": "Frank"
}
```
- **Response**: `201 Created`

#### 4. Partially update user (update name and/or add music)
- **Method**: `PATCH`
- **URL**: `/user/{user-id}`
- **Body**:
```json
{
  "name": "Alice Cooper",
  "music": "2"
}
```
*(Accepts `"music": "2"`, `"music": 2`, or array `[2, 3]`)*
- **Response**: `200 OK`

#### 5. Delete a user
- **Method**: `DELETE`
- **URL**: `/user/{user-id}`
- **Response**: `204 No Content`

#### 6. Remove a song from user's music list
- **Method**: `DELETE`
- **URL**: `/user/{user-id}/music/{music-id}`
- **Response**: `204 No Content`

---

### 🔍 Discovery Endpoints

#### 1. Discover people with similar music taste
- **Method**: `GET`
- **URL**: `/user/{user-id}/similar-users`
- **Response**: `200 OK`
```json
[
  {
    "user-id": 2,
    "name": "Bob",
    "shared-tracks-count": 2,
    "similarity-percentage": 50.0,
    "shared-music": [
      {
        "id": 1,
        "artist-name": "Daft Punk",
        "genre-name": "Electronic",
        "link": "https://open.spotify.com/track/4Cee00Dyq308ZgfSd9337b"
      }
    ]
  }
]
```

#### 2. Discover music recommendations
- **Method**: `GET`
- **URL**: `/user/{user-id}/recommendations`
- **Response**: `200 OK`
```json
[
  {
    "music": {
      "id": 4,
      "artist-name": "Led Zeppelin",
      "genre-name": "Rock",
      "link": "https://open.spotify.com/track/5CQ30WqJwcep0pYcV4AMNc"
    },
    "recommendation-score": 2,
    "recommended-by": ["Bob", "Dana"]
  }
]
```

#### 3. Discover fans of a song
- **Method**: `GET`
- **URL**: `/music/{music-id}/fans`
- **Response**: `200 OK`

---

## 🧪 Automated Testing

Run the full automated JUnit 5 test suite:
```bash
./mvnw test
```

Tests cover:
- Controller integration with MockMvc
- JSON path assertions
- Input validation and exception handling
- Edge cases (404 Not Found, 400 Bad Request)

---

## 📁 Project Structure

```
music-service/
├── pom.xml                                      # Maven project configuration
├── curl-test.sh                                 # Runnable CLI test script
├── src/
│   ├── main/
│   │   ├── java/com/musicservice/
│   │   │   ├── MusicServiceApplication.java     # Main application runner
│   │   │   ├── model/
│   │   │   │   ├── Music.java                   # JPA entity for songs
│   │   │   │   └── User.java                    # JPA entity for users
│   │   │   ├── dto/
│   │   │   │   ├── MusicRequest.java            # Request DTO for song creation/updates
│   │   │   │   ├── UserRequest.java             # Request DTO for user creation/updates
│   │   │   │   ├── TasteMatch.java              # DTO for people discovery
│   │   │   │   └── TasteRecommendation.java     # DTO for music discovery
│   │   │   ├── repository/
│   │   │   │   ├── MusicRepository.java         # Spring Data JPA repository
│   │   │   │   └── UserRepository.java          # Spring Data JPA repository
│   │   │   ├── service/
│   │   │   │   ├── MusicService.java            # Business logic for music
│   │   │   │   ├── UserService.java             # Business logic for users
│   │   │   │   └── DiscoveryService.java        # Taste & recommendation algorithms
│   │   │   ├── controller/
│   │   │   │   ├── MusicController.java         # REST endpoints for /music
│   │   │   │   └── UserController.java          # REST endpoints for /user
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java  # @RestControllerAdvice for JSON errors
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── BadRequestException.java
│   │   │   └── config/
│   │   │       └── DataInitializer.java         # Initial sample data seeder
│   │   └── resources/
│   │       └── application.properties           # Spring Boot configuration
│   └── test/
│       └── java/com/musicservice/controller/
│           ├── MusicControllerTest.java         # Tests for Music endpoints
│           └── UserControllerTest.java          # Tests for User & Discovery endpoints
```
