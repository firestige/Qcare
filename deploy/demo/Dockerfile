FROM alpine/java:21-jdk

COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas
COPY ./DemoApp-0.0.1.jar /app/

RUN apk add --no-cache curl

EXPOSE 8080

ENV JAVA_OPTS="-Xms512m -Xmx512m -XX:+UseG1GC"
ENV ARTHAS_HOME="/opt/arthas"

WORKDIR /app

CMD ["java", "-jar", "/app/DemoApp-0.0.1.jar"]
