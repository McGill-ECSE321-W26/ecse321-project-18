FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /backend

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew bootJar -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /backend

COPY --from=build /backend/build/libs/fashionstore-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
# ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=dev"]
