FROM java:8-jre-alpine

ENV JAVA_OPTS=""

ADD target/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]