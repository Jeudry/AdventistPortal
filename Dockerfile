# One image per service, from one Dockerfile: the services differ only in which module is
# built, so a file each would be four copies of the same thing drifting apart.
#
# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk AS build
ARG SERVICE
WORKDIR /src

COPY gradle gradle
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY build-logic build-logic
COPY shared shared
COPY contracts contracts
COPY core core
COPY features features
COPY services services

# Cache mounts, so a rebuild after a code change does not re-resolve the dependency graph
# or recompile what has not moved.
#
# `docker compose build` runs the services in parallel, so the shared dependency cache is
# locked and the per-project state gets an id of its own — two Gradle builds writing the
# same .gradle directory at once corrupt it.
RUN --mount=type=cache,target=/root/.gradle,sharing=locked \
    --mount=type=cache,target=/src/.gradle,id=gradle-project-${SERVICE} \
    ./gradlew --no-daemon ":services:${SERVICE}:bootJar" \
    && cp "services/${SERVICE}/build/libs/"*.jar /src/service.jar

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Not root: a service that is compromised should not also own the filesystem it runs on.
RUN useradd --system --uid 10001 --create-home service
USER service

COPY --from=build /src/service.jar service.jar

ENTRYPOINT ["java", "-jar", "/app/service.jar"]
