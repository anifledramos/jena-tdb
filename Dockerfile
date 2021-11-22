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
RUN cd /opt && git clone https://github.com/anifledramos/tfm-rdf.git
RUN apt-get update

ENV JENA_HOME /opt/jena-tdb
ENV CLASSPATH $JENA_HOME/lib

RUN cd /opt/tfm-rdf/jnitriples && mkdir obj 

RUN cd /opt/tfm-rdf/jnitriples && make clean all

RUN cd /opt/tfm-rdf/jnidictionary && make jni

RUN cd /opt/tfm-rdf/hdt-jni && mvn -DskipTests install

RUN ./opt/jena-tdb/bin/tdbloader --loc /data/test-tdb /opt/jena-tdb/data/test.nt

RUN ./opt/jena-tdb/bin/tdbquery --loc /data/test-tdb --time "SELECT ?s ?p ?o WHERE { ?s ?p ?o }"

RUN cd /opt/tfm-rdf/hdt-jni/hdt-java-cli/ && ./bin/rdf2hdt.sh -options "dictionary.type=<http://purl.org/HDT/hdt#dictionaryFour>;triples.format=<http://purl.org/HDT/hdt#triplesBitmap>;" ../../data/test.nt ../../data/test.hdt

RUN cd /opt/tfm-rdf/hdt-jni/hdt-jena && ./bin/hdtsparql.sh ../../data/test.hdt "SELECT ?s ?p ?o WHERE { ?s ?p ?o . }"
