# With this file we create a Docker image that contains the application
FROM gradle:7-jdk17 AS build
# We create a directory for the application and copy the build.gradle file
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

# We create a new image with the application
FROM openjdk:17-jdk-slim-buster
EXPOSE 8080:8080
EXPOSE 8083:8082
# Directory to store the application
RUN mkdir /app
# Copy the certificate to the container (if it is necessary)
RUN mkdir /cert
COPY --from=build /home/gradle/src/cert/* /cert/
# Copy the jar file to the container
COPY --from=build /home/gradle/src/build/libs/ktor-reactive-rest-hyperskill-all.jar /app/ktor-reactive-rest-hyperskill.jar
# Run the application
ENTRYPOINT ["java","-jar","/app/ktor-reactive-rest-hyperskill.jar"]