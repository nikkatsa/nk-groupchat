# Deploy groupchat server into a docker container. A user will have to copy this Dockerfile
# in ${project.directory}/target/appassembler/ and run the docker build command from there,
# as a working directory for the Docker build context to be constructed correctly
FROM openjdk:8u131-jre

MAINTAINER Nikolaos Katsanos "nikolaos.katsanos@gmail.com"

COPY . /usr/local/groupchat-server

WORKDIR /usr/local/groupchat-server/bin

CMD ["./WebsocketServer.sh"]
