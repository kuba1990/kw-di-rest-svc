FROM registry.net/dbase/server-jre8:1.8.25

COPY ./build/libs/di-rest-svc-0.0.1.jar di-rest-svc-0.0.1.jar

CMD ["java", "-jar", "-Djava.security.egd=file:/dev/urandom", "di-rest-svc-0.0.1.jar"]
