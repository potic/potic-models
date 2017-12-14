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

warn "Currently running docker images"
docker ps -a

warn "Killing currently running docker image..."
docker kill potic-rank-coordinator; docker rm potic-rank-coordinator

warn "Pulling latest docker image..."
docker pull potic/potic-rank-coordinator:$TAG_TO_DEPLOY

warn "Starting docker image..."
docker run -dit --name potic-rank-coordinator -e LOG_PATH=/mnt/logs -v /mnt/logs:/mnt/logs potic/potic-rank-coordinator:$TAG_TO_DEPLOY

warn "Currently running docker images"
docker ps -a
