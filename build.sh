#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────
# Render Build Script
# Builds the React frontend, copies it into the Spring Boot
# static resources, then packages the backend as a fat JAR.
# ─────────────────────────────────────────────────────────────
set -euo pipefail

echo "========================================"
echo "  1/3  Building React Frontend"
echo "========================================"
cd frontend
npm ci
npm run build
echo "✅  Frontend built successfully"

echo "========================================"
echo "  2/3  Copying frontend into backend"
echo "========================================"
# Spring Boot automatically serves anything in src/main/resources/static/
STATIC_DIR=../backend/src/main/resources/static
rm -rf "$STATIC_DIR"
mkdir -p "$STATIC_DIR"
cp -r dist/* "$STATIC_DIR"/
echo "✅  Frontend copied to $STATIC_DIR"

echo "========================================"
echo "  3/3  Building Spring Boot Backend"
echo "========================================"
cd ../backend
chmod +x mvnw 2>/dev/null || true
./mvnw clean package -DskipTests
echo "✅  Backend JAR built successfully"

echo "========================================"
echo "  Build Complete!"
echo "========================================"
ls -lh target/*.jar
