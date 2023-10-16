FROM amazoncorretto:11
COPY target/*.jar app.jar
COPY src/main/resourses/application.properties application.properties
ENTRYPOINT ["java","-jar","/app.jar","--spring.config.location=application.properties"]