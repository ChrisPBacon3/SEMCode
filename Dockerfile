FROM amazoncorretto:27
COPY ./target/semApp-0.1.0.3.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "semApp-0.1.0.3.jar"]