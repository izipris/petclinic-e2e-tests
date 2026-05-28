# petclinic-e2e-tests

End-to-end tests for [spring-petclinic-rest](https://github.com/spring-petclinic/spring-petclinic-rest), written as Spring Boot + JUnit 5 tests
that drive the API through HTTP.
This is the reference repository behind the article [How I write end-to-end tests](https://idanzipris.com/writing/how-i-write-e2e-tests.html).

## Layout

- `src/main/java/.../drivers/` — thin REST clients (one per resource) built on `BaseDriver` (`RestClient` wrapper).
- `src/main/java/.../model/` — request/response DTOs (Lombok `@Data` + `@Builder`).
- `src/test/java/.../*Test.java` — one suite per driver, exercising create/get/update/delete + resource-specific flows.
- `pet-clinic-rest.sh` — `up`/`down` lifecycle for the dependencies (Postgres via Docker Compose) and the petclinic-rest app container.
- `docker-compose.yml` — Postgres dependency on the shared `petclinic-net` network.
- `Jenkinsfile` — declarative pipeline: checkout petclinic-rest, bring services up, run tests, tear down.

## Running locally

```bash
git clone <petclinic-rest-url> spring-petclinic-rest
./pet-clinic-rest.sh up      # builds the app, starts Postgres + app container on :9966
./mvnw test
./pet-clinic-rest.sh down
```

The default `petclinic.base-url` is `http://localhost:9966/petclinic` (see `src/main/resources/application.properties`).

## Running in CI

The `Jenkinsfile` activates the `ManagedDocker` Spring profile, which overrides the base URL to `http://docker:9966/petclinic` (the DinD
hostname) — see `src/main/resources/application-ManagedDocker.properties`.

```bash
./mvnw test -Dspring.profiles.active=ManagedDocker
```

## Requirements

- Java 21
- Docker (Compose v2)
- A clone of `spring-petclinic-rest` next to this repo as `spring-petclinic-rest/`
