# Base image Alpine
FROM khipu/openjdk17-alpine

# Set working dir
WORKDIR /auth-api

# Cài ffmpeg trên Alpine
RUN apk add --no-cache ffmpeg

# Copy jar
COPY target/preschool-api-0.0.1-SNAPSHOT.jar target.jar

# Run
CMD ["java", "-jar", "target.jar"]
