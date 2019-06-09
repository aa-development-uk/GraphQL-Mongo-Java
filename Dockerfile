###
# Build image
###
FROM java:8-alpine as build
WORKDIR /workspace/app

# Copy src files fpr building
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Make wrapper executable
RUN chmod +x mvnw

# Build app
RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

###
# Run image
###
FROM java:8-alpine
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build /workspace/app/target/graphql-*.jar /app/app.jar
ENTRYPOINT ["java", "-cp","app:app/lib/*", "-jar","/app/app.jar"]