# 🏗️ MAS Builders Backend (vibe_backend)

A powerful **Spring Boot 3.x** backend powering the MAS Builders Real Estate Platform.  
Handles authentication, property listings, and admin operations — connected to a Next.js frontend and MongoDB database.

---

## 🚀 Tech Stack

- **Framework:** Spring Boot 3 (Java 17)  
- **Database:** MongoDB (Atlas / Local)  
- **Security:** Spring Security + JWT  
- **Build Tool:** Maven / Gradle  
- **Architecture:** Layered (Controller → Service → Repository → DTO)

---

## ⚙️ Setup & Installation

### 🧩 Prerequisites
- Java 17+
- MongoDB (local or Atlas)
- Maven or Gradle

---

### 🧱 Steps

```bash
git clone https://github.com/SmitAnn/vibe_backend.git
cd vibe_backend
mvn spring-boot:run


⚙️ Configuration

Update your MongoDB and JWT configuration inside:

src/main/resources/application.properties

spring.application.name=MAS_Builders_Backend

# ========================
# MONGODB CONFIG
# ========================
spring.data.mongodb.uri=mongodb://localhost:27017/masbuilders_db
spring.data.mongodb.database=masbuilders_db

server.port=8081

# ========================
# JWT CONFIG
# ========================
jwt.secret=ReplaceThisWithAStrongSecretKey_ChangeMe
jwt.expiration-ms=86400000   # Time in milliseconds (1 day)

# ========================
# LOGGING
# ========================
logging.level.org.springframework.web=DEBUG

# ========================
# FILE UPLOAD CONFIG
# ========================
file.upload-dir=uploads
file.upload-image-dir=uploads/images
file.upload-video-dir=uploads/videos

spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

🕒 Note: You can change the JWT expiration time (jwt.expiration-ms) according to your needs.


