FROM openjdk:8

RUN mkdir -p /usr/src/potic-rank-coordinator && mkdir -p /opt

COPY build/distributions/* /usr/src/potic-rank-coordinator/

RUN unzip /usr/src/potic-rank-coordinator/potic-rank-coordinator-*.zip -d /opt/ && ln -s /opt/potic-rank-coordinator-* /opt/potic-rank-coordinator

WORKDIR /opt/potic-rank-coordinator

EXPOSE 8080
ENV ENVIRONMENT_NAME test
ENTRYPOINT [ "sh", "-c", "./bin/potic-rank-coordinator --spring.profiles.active=$ENVIRONMENT_NAME" ]
CMD []
