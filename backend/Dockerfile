FROM gradle:8.10-jdk17-graal
LABEL maintainer="Pierce Le <hale0087@uni.sydney.edu.au>"

# Working directory inside the container
WORKDIR /app

# Copy everything to the container
COPY . .

# Accept build arguments from docker-compose.yml
ARG MYSQL_DATABASE
ARG MYSQL_USER
ARG MYSQL_PASSWORD
ARG MYSQL_ROOT_PASSWORD

# Set environment variables to be used at runtime
ENV MYSQL_DATABASE=${MYSQL_DATABASE}
ENV MYSQL_USER=${MYSQL_USER}
ENV MYSQL_PASSWORD=${MYSQL_PASSWORD}
ENV MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}

# Build the project using Gradle
RUN gradle build -x test

# Expose port 8080 for the application
EXPOSE 8080

# Run the application
CMD ["gradle", "run"]