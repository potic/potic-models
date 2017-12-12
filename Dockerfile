FROM openjdk:8

RUN mkdir -p /usr/src/potic-rank-coordinator && mkdir -p /usr/app

COPY build/distributions/* /usr/src/potic-rank-coordinator/

RUN unzip /usr/src/potic-rank-coordinator/potic-rank-coordinator-*.zip -d /usr/app/ && ln -s /usr/app/potic-rank-coordinator-* /usr/app/potic-rank-coordinator

WORKDIR /usr/app/potic-rank-coordinator

EXPOSE 8080
ENV ENVIRONMENT_NAME test
ENTRYPOINT [ "sh", "-c", "./bin/potic-rank-coordinator --spring.profiles.active=$ENVIRONMENT_NAME" ]
CMD []
