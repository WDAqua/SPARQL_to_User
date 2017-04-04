/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sntl;


import java.util.ArrayList;
import java.util.Iterator;
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
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.apache.jena.sparql.util.StringUtils;
import static org.apache.jena.sparql.vocabulary.TestManifest.result;
import static org.apache.jena.tdb.lib.NodeLib.nodes;
import static org.apache.jena.vocabulary.TestManifest.result;



/**
 *
 * @author youssef
 */
public class SNTL {
        String strq  ; 
     String resulte;
     String pays;
    public void sntl (){
        // q;
   
       
       strq= "SELECT DISTINCT ?x WHERE { "
               +"<http://www.wikidata.org/entity/Q91> <http://www.wikidata.org/prop/direct/P61> ?x . "
               +"} limit 1000";

       Query q = QueryFactory.create(strq, Syntax.syntaxARQ) ;

         ElementWalker.walk(q.getQueryPattern(),
                        // For each element...
                        new ElementVisitorBase() {
                            // ...when it's a block of triples...
                            @Override
                            public void visit(ElementPathBlock el) {
                                //System.out.println("Here");
                                // ...go through all the triples...
                                Iterator<TriplePath> triples = el.patternElts();
                                ArrayList<Node> nodes = new ArrayList<Node>();
                                while (triples.hasNext()) {
                                    // ...and grab the subject
                                    TriplePath triple = triples.next();
                                    nodes.add(triple.getSubject());
                                    nodes.add(triple.getPredicate());
                                    nodes.add(triple.getObject());
//System.out.println(triple.getSubject());
            resulte = triple.getPredicate().toString();
            pays= triple.getSubject().toString();
            System.out.println("****************************************************");
                                    System.out.println(resulte);
//                                System.out.println(pays);
                                }
                                int k=1;
                                StringUtils util=new StringUtils();
                                for (Node n : nodes){
                                    if (n.isVariable()){
//                                        feature.put("r"+k+"-var",1.0);
                                        k++;
                                    } else {
//                                        int i=m.getIndex(n.toString());
//                                        int sim=util.getLevenshteinDistance(m.getText(i).toLowerCase(),m.getLex(i).toLowerCase());
//                                        feature.put("r"+k+"-sim",(double)sim);
//                                        feature.put("r"+k+"-rel",(double)m.getRel(i));
                                        //feature.put("r"+k+"-type",m.getType(i));
                                        k++;
                                    }
                                }
                                 
                            }
                         
                            

                            
                            @Override
                            public void visit(ElementData el) {
                                StringUtils util=new StringUtils();
//                                feature.put("linking",1.0);
//                                int i=m.getIndex(el.getRows().get(0).get(el.getVars().get(0)).toString());
//                                int sim=util.getLevenshteinDistance(m.getText(i).toLowerCase(),m.getLex(i).toLowerCase());
//                                feature.put("r"+6+"-sim",(double)sim);
//                                feature.put("r"+6+"-rel",(double)m.getRel(i));
                                //feature.put("r"+7+"-type",m.getType(i));
                                 
                            }
                       });
       String strSubject=strTo(resulte);
       String strPredicat=strTo(pays);
       String  subject= getLabel(strSubject);
       String  predicat= getLabel(strPredicat);
       String resultate = predicat+"/"+subject ;
        System.out.println(resultate);
    }
    
    
    
    public String getLabel (String s){
   String lab = null;     
         String res = 
 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
+" SELECT DISTINCT ?o WHERE { "
+" "+s+" rdfs:label ?o ."
+"  FILTER( lang(?o)=\"en\")"
+"} limit 20 ";
    

       System.out.println(res);
         
Query query1 = QueryFactory.create(res);
QueryExecution qExe = QueryExecutionFactory.sparqlService("https://query.wikidata.org/sparql", query1 );
ResultSet result;
result = qExe.execSelect();
;
while (result.hasNext()){
 lab = result.next().getLiteral("o").getLexicalForm().toString();
//  System.out.println(lab);
}
            return lab;
   
    }
    
    public String strTo(String str2){
        return "<"+str2.replaceAll("/prop/direct", "/entity")+">";
    }
    
    
    
    
    public static void main(String[] args) {
       SNTL s= new SNTL();
       s.sntl();
    }
}
