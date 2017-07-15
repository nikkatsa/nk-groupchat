# Copy client side web UI content into an Nginx container. In order to run docker build
# the user needs to copy this Dockerfile in ${project.directory}/src/main/resources/web/ and
# run the command from that working directory, in order for the Docker build context to be 
# constructed correctly
FROM nginx:1.13.3

MAINTAINER Nikolaos Katsanos "nikolaos.katsanos@gmail.com"

COPY . /usr/share/nginx/html
