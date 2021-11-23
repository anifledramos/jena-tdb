/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.udc.lbd.jena;

import org.apache.jena.query.Dataset ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

import org.apache.jena.sparql.util.QueryUtils;
import org.apache.jena.tdb.TDBFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/** Example of creating a TDB-backed model.
 *  The preferred way is to create a dataset then get the mode required from the dataset.
 *  The dataset can be used for SPARQL query and update
 *  but the Model (or Graph) can also be used.
 *  
 *  All the Jena APIs work on the model.
 *   
 *  Calling TDBFactory is the only place TDB-specific code is needed.
 */

public class JenaServer {

    private String input;
    private int port = 15362;

    private Dataset ds;
    private Model model;

    private PrintStream logger;

    public JenaServer(String input) {
        this.input = input;
    }

    protected int runQuery(String queryString) {
        int nres = 0;
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, ds);

        ResultSet results = qexec.execSelect();

        while (results.hasNext()) {
            QuerySolution soln = results.next();
            nres ++;
        }
        return nres;
    }

    private int runQueries(List<String> queries) {
        System.out.println("Executing queries: " + queries.size());
        int nres = 0;
        for (int i = 0; i < queries.size(); i++) {
            nres += runQuery(queries.get(i));
        }
        return nres;
    }

    /**
     * Read from a line, where each component is separated by space.
     *
     * @param line
     */
    private String parseQuery(String line) {

        int split, posa, posb;

        // SET SUBJECT
        posa = 0;
        posb = split = line.indexOf(' ', posa);

        String sub = line.substring(posa, posb);
        if (sub.equals("?")) sub = "?s";

        // SET PREDICATE
        posa = split+1;
        posb = split = line.indexOf(' ', posa);

        String pred = line.substring(posa, posb);
        if (pred.equals("?")) pred = "?p";

        // SET OBJECT
        posa = split+1;
        posb = line.length();

        if(line.charAt(posb-1)=='.') posb--;	// Remove trailing <space> <dot> from NTRIPLES.
        if(line.charAt(posb-1)==' ') posb--;

        String obj = line.substring(posa, posb);
        if (obj.equals("?")) obj = "?o";

        return "SELECT * WHERE { " + sub + " " + pred + " " + obj + " }";
    }

    public void execute() throws IOException {
        // Direct way: Make a TDB-back Jena model in the named directory.
        String directory = "MyDatabases/DB1" ;
        ds = TDBFactory.createDataset(directory) ;
        model = ds.getDefaultModel() ;



        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            System.out.println("Server starting on port " + port);
            serverSocket = new ServerSocket(port, 1); //Minimalistic server
            boolean keepAlive = true;
            PrintStream out = null;
            while (keepAlive) {
                socket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintStream(socket.getOutputStream());
                try {
                    String line = reader.readLine();
                    System.out.println("Got: " + line);
                    if ("DIE".equals(line)) {
                        out.println("BYE");
                        keepAlive = false;
                        System.exit(0);
                    } else if ("CHECK".equals(line)) {
                        out.println("OK");
                    } else if (line.startsWith("LOG")) {
                        logger = new PrintStream(line.substring(4));
                    } else if (line.startsWith("RUN")) {
                        String filename = line.substring(4);
                        BufferedReader in = new BufferedReader(new FileReader(filename));
                        int nqueries = Integer.parseInt(in.readLine());
                        List<String> queries = new ArrayList<String>(nqueries);
                        for (int i = 0; i < nqueries; i++) {
                            queries.add(parseQuery(in.readLine()));
                        }
                        in.close();
                        Long startTime = System.currentTimeMillis();
                        int nres = runQueries(queries);
                        Long endTime = System.currentTimeMillis();

                        System.out.println("Got " + nqueries + ". Results: " + nres + ", time: " + (endTime - startTime));
                        out.println("Results: " + nres + ", time: " + (endTime - startTime));
                        if (logger != null) {
                            logger.close();
                            logger = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) out.close();
                        if (socket != null) socket.close();
                    } catch (Exception e) {
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ds != null) ds.close();
                if (serverSocket != null) serverSocket.close();
            } catch (Exception e) {
            }
        }

    }

    public static void main(String[] args) throws Throwable {

        JenaServer jenaServer = new JenaServer(args[0]);

        jenaServer.execute();
    }
}
