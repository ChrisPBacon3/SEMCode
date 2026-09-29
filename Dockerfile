FROM amazoncorretto:27
COPY ./target/semApp.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "semApp.jar"]