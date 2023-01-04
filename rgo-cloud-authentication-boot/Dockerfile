FROM openjdk:11

ADD /target/rgo-cloud-authentication-boot-1.0-SNAPSHOT.jar auth-app.jar

ENTRYPOINT ["java", "-jar", "auth-app.jar"]