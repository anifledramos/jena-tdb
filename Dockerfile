FROM gcc:8.5.0

RUN mkdir data

#Install dependencies
RUN apt-get update \
    && apt-get install -y \
    git \
    make\
    maven \
    nano \
    openjdk-11-jdk 

ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

#Download sources
RUN cd /opt && git clone https://github.com/anifledramos/jena-tdb.git
RUN apt-get update

ENV JENA_HOME /opt/jena-tdb
ENV CLASSPATH $JENA_HOME/lib

