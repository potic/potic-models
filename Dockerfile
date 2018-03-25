FROM openjdk:8

RUN mkdir -p /usr/src/potic-models && mkdir -p /opt

COPY build/distributions/* /usr/src/potic-models/

RUN unzip /usr/src/potic-models/potic-models-*.zip -d /opt/ && ln -s /opt/potic-models-* /opt/potic-models

WORKDIR /opt/potic-models

EXPOSE 8080
ENV ENVIRONMENT_NAME test
ENTRYPOINT [ "sh", "-c", "./bin/potic-models --spring.profiles.active=$ENVIRONMENT_NAME" ]
CMD []
