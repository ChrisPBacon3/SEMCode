FROM amazoncorretto:27
COPY ./target/semApp-0.1.0.6.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "semApp-0.1.0.6.jar"]