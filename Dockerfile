FROM maven:3.9-eclipse-temurin-26 AS build

WORKDIR /workspace

COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp dependency:go-offline

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp verify

FROM eclipse-temurin:26-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -S app && adduser -S -G app app

COPY --from=build --chown=app:app \
    /workspace/target/Financas_Computacionais-1.0-SNAPSHOT.jar ./app.jar

USER app

ENTRYPOINT ["java", "-cp", "/app/app.jar", "br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.MetodosNumericosController"]