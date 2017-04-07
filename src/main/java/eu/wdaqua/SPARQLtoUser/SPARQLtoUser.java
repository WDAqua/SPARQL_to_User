package eu.wdaqua.SPARQLtoUser;

import java.util.ArrayList;
import java.util.ListIterator;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.apache.jena.sparql.util.StringUtils;



/**
 *
 * @author youssef
 */
public class SPARQLtoUser {

    String resulte;
    String predicate;
    boolean isOneTrip;
    boolean isConform;
    

    public String go(String strq) {
        Query q;
        try {
            q = QueryFactory.create(strq, Syntax.syntaxARQ);
        } catch (QueryParseException e){
            System.out.println(e);
            return null;
        }
      
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
                        }

                    }

                }

                @Override
                public void visit(ElementData el) {
                    StringUtils util = new StringUtils();

                }
            });
            String strSubject = getLabel(resulte.replaceAll("/prop/direct", "/entity"));
            String strPredicate = getLabel(predicate.replaceAll("/prop/direct", "/entity"));
            String result = strSubject + "/" + strPredicate;
            return result;
        } else {
            return null;
        }
    }

    public String getLabel(String s) {
        String lab = null;
        String res
                = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + " SELECT DISTINCT ?o WHERE { "
                + " <" + s + "> rdfs:label ?o ."
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
        return  str2 ;
    }
  
    public boolean isThereOk(Query query) {

        boolean res;
        boolean isSel = query.isSelectType();
        boolean hasAggre = !(query.hasAggregators());

        ElementWalker.walk(query.getQueryPattern(),
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
                            isConform = true;

                        } else {
                            isConform = false;
                          }

                        c++;
                    } 
                  
                }
                if (c == 1) {
                    isOneTrip = true;
                } else {
                    isOneTrip = false;
                }
            }

            @Override
            public void visit(ElementData el) {
                StringUtils util = new StringUtils();

            }
        });

        return (isSel) && (hasAggre) && (isConform) && (isOneTrip);
    }


}
