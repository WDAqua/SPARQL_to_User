/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sntl;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import static org.apache.jena.query.ResultSetFactory.result;
import static org.apache.jena.query.ResultSetFactory.result;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.lang.arq.ParseException;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.apache.jena.sparql.util.StringUtils;
import static org.apache.jena.sparql.vocabulary.TestManifest.result;
import static org.apache.jena.tdb.lib.NodeLib.nodes;
import static org.apache.jena.vocabulary.TestManifest.result;
import org.apache.maven.plugins.annotations.Execute;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;



/**
 *
 * @author youssef
 */
public class SNTL {
   
    String strq;
    String resulte;
    String predicate;
    boolean isOneTrip;
    boolean isConform;
    

    public String sntl(String strq) {

        Query q = QueryFactory.create(strq, Syntax.syntaxARQ);
      
        if (isThereOk(q)) {
            ElementWalker.walk(q.getQueryPattern(),
                    // For each element...
                    new ElementVisitorBase() {
                // ...when it's a block of triples...
                @Override
                public void visit(ElementPathBlock el) {
                    // ...go through all the triples...

                    ListIterator<TriplePath> triples = el.getPattern().iterator();
                    ArrayList<Node> nodes = new ArrayList<Node>();

                    while (triples.hasNext()) {
                        // ...and grab the subject
                        TriplePath triple = triples.next();
                        if (triple.isTriple()) {
                       
                                resulte=triple.getSubject().toString();
                                predicate=triple.getPredicate().toString();

                        } else {
                            triple.getPath();
                            System.out.println("it's not a triple");
                        }

                    }

                }

                @Override
                public void visit(ElementData el) {
                    StringUtils util = new StringUtils();

                }
            });
            String strSubject = strTo(resulte);
            String strPredicat = strTo(predicate);
            String subject = getLabel(strSubject);
            String predicat = getLabel(strPredicat);
            String resultate = predicat + "/" + subject;

            return resultate;
        } else {
            System.out.println("The query is not OK ");
            return null;
        }
    }

    public String getLabel(String s) {
        String lab = null;
        String res
                = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + " SELECT DISTINCT ?o WHERE { "
                + " " + s + " rdfs:label ?o ."
                + "  FILTER( lang(?o)=\"en\")"
                + "} limit 20 ";

        //       System.out.println(res);
        Query query1 = QueryFactory.create(res);
        QueryExecution qExe = QueryExecutionFactory.sparqlService("https://query.wikidata.org/sparql", query1);
        ResultSet result;
        result = qExe.execSelect();
        ;
        while (result.hasNext()) {
            lab = result.next().getLiteral("o").getLexicalForm().toString();
            //  System.out.println(lab);
        }
        return lab;

    }

    public String strTo(String str2) {
        return "<" + str2.replaceAll("/prop/direct", "/entity") + ">";
    }
  
    public boolean isThereOk(Query qu) {

        boolean res;
        boolean isSel = qu.isSelectType();
        boolean hasAggre = !(qu.hasAggregators());

        if (qu.isSelectType()) {
            System.out.println("SELECT OK !!");
        } else {
            System.out.println("SELECT NO !! ");
        }
        if (!(qu.hasAggregators())) {
            System.out.println("AGGREGATORS OK !!");
        } else {
            System.out.println("AGGREGATORS NO !! ");
        }

        ElementWalker.walk(qu.getQueryPattern(),
                // For each element...
                new ElementVisitorBase() {
            // ...when it's a block of triples...
            @Override
            public void visit(ElementPathBlock el) {
                // ...go through all the triples...
                ListIterator<TriplePath> triples = el.getPattern().iterator();
                ArrayList<Node> nodes = new ArrayList<Node>();
                int c = 0;
                while (triples.hasNext()) {
                    // ...and grab the subject
                    TriplePath triple = triples.next();
                    if (triple.isTriple()) {

                        if ((triple.getSubject().isURI()) && (triple.getPredicate().isURI() && (triple.getObject().isVariable()))) {
                            System.out.println("CONFORM OK !!");
                            isConform = true;

                        } else {
                            System.out.println("CONFORM NO !!");
                        }

                        c++;
                    } else {
                        System.out.println("it's not a triple");
                    }
                    if (c == 1) {
                        System.out.println("COMPTE 1 OK !!");
                        isOneTrip = true;
                    } else {
                        System.out.println("COMPTE 1 NO !!");
                        isOneTrip = false;

                    }
                }

            }

            @Override
            public void visit(ElementData el) {
                StringUtils util = new StringUtils();

            }
        });

        return (isSel) && (hasAggre) && (isConform) && (isOneTrip);
    }


    public static void main(String[] args) {
       SNTL s= new SNTL();

    }
}
