#!/usr/bin/env sh

##############################################################################
##
##  Stop and kill currently running docker image, pull newest version and
##  run it.
##
##############################################################################

warn ( ) {
    echo "$*"
}

warn "Pulling latest docker image..."
docker pull potic/potic-models:$TAG_TO_DEPLOY

warn "Currently running docker images"
docker ps -a

warn "Killing currently running docker image..."
docker kill potic-models; docker rm potic-models

warn "Starting docker image..."
docker run -dit --name potic-models --restart on-failure --link potic-articles --link potic-ranker --link potic-mongodb -e LOG_PATH=/mnt/logs -v /mnt/logs:/mnt/logs -e MONGO_PASSWORD=$MONGO_PASSWORD -e LOGZIO_TOKEN=$LOGZIO_TOKEN -p 40408:8080 potic/potic-models:$TAG_TO_DEPLOY

warn "Wait 30sec to check status"
sleep 30

warn "Currently running docker images"
docker ps -a
