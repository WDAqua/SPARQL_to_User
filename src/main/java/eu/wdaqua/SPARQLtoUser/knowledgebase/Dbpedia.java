package eu.wdaqua.SPARQLtoUser.knowledgebase;


import eu.wdaqua.SPARQLtoUser.knowledgebase.KnowledgeBase;
import org.apache.jena.query.*;

import java.util.ArrayList;

/**
 * Created by dryous on 22/06/2017.
 */
public class Dbpedia extends KnowledgeBase {

    public Dbpedia(String endpoint) {

    }

    @Override
    public ArrayList<String> getLabel(String uri, String language, String kb, String ep) {
        ArrayList<String> labels = new ArrayList<>();
        String res
                = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " PREFIX schema: <http://schema.org/> "
                + " SELECT DISTINCT ?o ?x WHERE { "
                + " <" + uri + "> rdfs:label ?o . "
                + "  OPTIONAL { <" + uri + ">  schema:description ?x FILTER( lang(?x)=\"" + language + "\" )} . "
                + "  FILTER( lang(?o)=\"" + language + "\" )"
                + "} limit 20 ";

        Query query1 = QueryFactory.create(res);
        QueryExecution qExe = QueryExecutionFactory.sparqlService(ep, query1);
        ResultSet result = qExe.execSelect();
        while (result.hasNext()) {
            QuerySolution rsnext = result.next();
            labels.add(rsnext.getLiteral("o").getLexicalForm().toString());
        }

        return labels;
    }

    @Override
    public ArrayList<String> getAlternative(String res, String predicate, String language, String kb, String ep) {


        ArrayList<String> altern = new ArrayList<>();
        Query query1 = QueryFactory.create(res);
        QueryExecution qExe = QueryExecutionFactory.sparqlService(ep, query1);
        ResultSet result;
        result = qExe.execSelect();
        while (result.hasNext()) {
            QuerySolution rsnext = result.next();//
            String s = predicate.replace("?","");
            String nxt = rsnext.getResource(s).toString();
            altern.add(nxt);
        }
        return altern;

    }


}
