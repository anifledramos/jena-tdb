# Set up Apache Jena 

## Download apache-jena and unzip folder.
## Include /lib folder in the CLASSPATH.

    $ export JENA_HOME="/home/delfina/tfm/apache-jena/apache-jena-4.2.0"
    $ echo $JENA_HOME
        /home/delfina/tfm/apache-jena/apache-jena-4.2.0
    $ export CLASSPATH=$JENA_HOME/lib
    $ echo $CLASSPATH
        /home/delfina/tfm/apache-jena/apache-jena-4.2.0/lib

## Create directory for test files
    $ mkdir data

## TDB Execution scripts

    $ ./scripts/tdb-construction.sh

    $ ./scripts/tdb-spo.sh

## Test basic query without TDB

    $ bin/sparql --data=data/test.nt --query=data/q1.rq
    --------------------------------------------------------------------------------------------
    | subject                   | predicate                       | object                     |
    ============================================================================================
    | <http://example.org/uri3> | <http://example.org/predicate3> | <http://example.org/uri3>  |
    | <http://example.org/uri3> | <http://example.org/predicate3> | <http://example.org/uri4>  |
    | <http://example.org/uri3> | <http://example.org/predicate3> | <http://example.org/uri5>  |
    | <http://example.org/uri1> | <http://example.org/predicate2> | <http://example.org/uriA3> |
    | <http://example.org/uri1> | <http://example.org/predicate2> | <http://example.org/uri3>  |
    | <http://example.org/uri1> | <http://example.org/predicate1> | "literalC"                 |
    | <http://example.org/uri1> | <http://example.org/predicate1> | "literalB"                 |
    | <http://example.org/uri1> | <http://example.org/predicate1> | "literalA"                 |
    | <http://example.org/uri1> | <http://example.org/predicate1> | "literal1"                 |
    | <http://example.org/uri4> | <http://example.org/predicate4> | <http://example.org/uri3>  |
    | <http://example.org/uri2> | <http://example.org/predicate1> | "literal2"                 |
    | <http://example.org/uri5> | <http://example.org/predicate5> | <http://example.org/uri4>  |

## Creating TDB dataset

    $ bin/tdbloader --loc path/for/dataset path/to/dataset.nt
    $ bin/tdbloader --loc data/ data/test.nt
        13:59:57 INFO  loader          :: -- Start triples data phase
        13:59:57 INFO  loader          :: ** Load empty triples table
        13:59:57 INFO  loader          :: -- Start quads data phase
        13:59:57 INFO  loader          :: ** Load empty quads table
        13:59:57 INFO  loader          :: Load: data/test.nt -- 2021/11/11 13:59:57 CET
        13:59:57 INFO  loader          :: -- Finish triples data phase
        13:59:57 INFO  loader          :: ** Data: 12 triples loaded in 0,06 seconds [Rate: 206,90 per second]
        13:59:57 INFO  loader          :: -- Finish quads data phase
        13:59:57 INFO  loader          :: -- Start triples index phase
        13:59:57 INFO  loader          :: ** Index SPO->POS: 12 slots indexed in 0,00 seconds [Rate: 12.000,00 per second]
        13:59:57 INFO  loader          :: ** Index SPO->OSP: 12 slots indexed
        13:59:57 INFO  loader          :: -- Finish triples index phase
        13:59:57 INFO  loader          :: ** 12 triples indexed in 0,00 seconds [Rate: 2.400,00 per second]
        13:59:57 INFO  loader          :: -- Finish triples load
        13:59:57 INFO  loader          :: ** Completed: 12 triples loaded in 0,07 seconds [Rate: 173,91 per second]
        13:59:57 INFO  loader          :: -- Finish quads load

## Querying TDB

    $ bin/tdbquery --loc path/to/TDBdataset "SPARQL query"
    $ bin/tdbquery --loc data/ "SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate ?object } LIMIT 25"
    --------------------------------------------------------------------------------------------
    | subject                   | predicate                       | object                     |
    ============================================================================================
    | <http://example.org/uri1> | <http://example.org/predicate1> | "literal1"                 |
    | <http://example.org/uri1> | <http://example.org/predicate1> | "literalA"                 |
    | <http://example.org/uri1> | <http://example.org/predicate1> | "literalB"                 |
    | <http://example.org/uri1> | <http://example.org/predicate1> | "literalC"                 |
    | <http://example.org/uri1> | <http://example.org/predicate2> | <http://example.org/uri3>  |
    | <http://example.org/uri1> | <http://example.org/predicate2> | <http://example.org/uriA3> |
    | <http://example.org/uri3> | <http://example.org/predicate3> | <http://example.org/uri3>  |
    | <http://example.org/uri3> | <http://example.org/predicate3> | <http://example.org/uri4>  |
    | <http://example.org/uri3> | <http://example.org/predicate3> | <http://example.org/uri5>  |
    | <http://example.org/uri2> | <http://example.org/predicate1> | "literal2"                 |
    | <http://example.org/uri4> | <http://example.org/predicate4> | <http://example.org/uri3>  |
    | <http://example.org/uri5> | <http://example.org/predicate5> | <http://example.org/uri4>  |
    --------------------------------------------------------------------------------------------
