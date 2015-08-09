FROM ubuntu:14.04
MAINTAINER Kim Stebel <kim.stebel@gmail.com>
EXPOSE 8080
RUN apt-get update && apt-get install -y \
    git \
    aspell \
    aspell-en \
    openjdk-7-jdk
RUN echo 2015-08-09 08:39 >/version
COPY target/scala-2.11/scaspell.jar /
CMD ["java", "-jar", "/scaspell.jar"]

