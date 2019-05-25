FROM 264606497040.dkr.ecr.us-east-1.amazonaws.com/ib-openjdk:8u181
LABEL Name=kotlindynamodb Version=master
ENV DEBIAN_FRONTEND=noninteractive

# TODO below here should run as a different user 'app'

ENV HOME=/home/app
ENV APP_HOME=$HOME/kotlindynamodb
# add path and config location for running sopstool with secrets encrypted outside of the jar
ENV PATH=.:$PATH

RUN mkdir -p $APP_HOME
WORKDIR $APP_HOME

ARG GIT_REV
ARG TAGS

# clean on build means there should only be one valid jar
# avaliable configuration in conf file: https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-script-customization-conf-file
COPY build/libs/testing.jar $APP_HOME/testing.jar


CMD ["sponsored-content-placement.jar"]
