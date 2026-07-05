# ============================================================
# Multi-stage Dockerfile for the Fertilizer Recommendation Agent
# Stage 1: Build React frontend
# Stage 2: Build Spring Boot backend (with frontend baked in)
# Stage 3: Slim runtime image
# ============================================================

# ---- Stage 1: Build Frontend ----
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# ---- Stage 2: Build Backend ----
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app/backend

# Copy pom.xml and download dependencies first (Docker cache layer)
COPY backend/pom.xml ./
RUN mvn dependency:go-offline -B

# Copy frontend build into Spring Boot static resources
COPY --from=frontend-build /app/frontend/dist/ ./src/main/resources/static/

# Copy backend source
COPY backend/src/ ./src/

# Build the fat JAR
RUN mvn clean package -DskipTests -B

# ---- Stage 3: Runtime ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the fat JAR from the build stage
COPY --from=backend-build /app/backend/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=render"]
