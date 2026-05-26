#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="$SCRIPT_DIR/spring-petclinic-rest"

up() {
  echo "Starting dependencies..."
  docker compose -f "$SCRIPT_DIR/docker-compose.yml" up -d --wait

  echo "Building petclinic-rest..."
  cd "$APP_DIR"
  ./mvnw package -DskipTests -q

  JAR=$(find "$APP_DIR/target" -name "spring-petclinic-rest-*.jar" ! -name "*.original" | head -1)

  echo "Starting petclinic-rest app..."
  docker rm -f petclinic-app 2>/dev/null || true
  docker run -d \
    --name petclinic-app \
    --network petclinic-net \
    -e SPRING_PROFILES_ACTIVE=ManagedDocker,spring-data-jpa \
    -p 9966:9966 \
    -v "$JAR:/app/app.jar" \
    eclipse-temurin:21-jre-alpine \
    java -jar /app/app.jar

  echo "Petclinic REST is running at http://localhost:9966/petclinic"
}

down() {
  echo "Stopping petclinic-rest app..."
  docker rm -f petclinic-app 2>/dev/null || true

  echo "Stopping dependencies..."
  docker compose -f "$SCRIPT_DIR/docker-compose.yml" down -v

  echo "Removing network..."
  docker network rm petclinic-net 2>/dev/null || true
}

case "$1" in
  up)   up ;;
  down) down ;;
  *)    echo "Usage: $0 {up|down}"; exit 1 ;;
esac
