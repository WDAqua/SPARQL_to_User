package eu.wdaqua.SPARQLtoUser;


import org.apache.jena.query.*;

import java.util.ArrayList;

/**
 * Created by dryous on 22/06/2017.
 */
public abstract class Dbpedia implements KnowledgeBase{

    public ArrayList<String> getLabel(String uri, String language, String kb) {

        ArrayList<String> labels = new ArrayList<>();
        String res
                = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " PREFIX schema: <http://schema.org/> "
                + " SELECT DISTINCT ?o ?x WHERE { "
                + " <" + uri + "> rdfs:label ?o . "
                + "  OPTIONAL { <" + uri + ">  schema:description ?x FILTER( lang(?x)=\"" + language + "\" )} . "
                + "  FILTER( lang(?o)=\"" + language + "\" )"
                + "} limit 20 ";

            kb = "http://dbpedia.org/sparql";
            Query query1 = QueryFactory.create(res);
            QueryExecution qExe = QueryExecutionFactory.sparqlService(kb, query1);
            ResultSet result = qExe.execSelect();
            while (result.hasNext()) {
                QuerySolution rsnext = result.next();
                labels.add(rsnext.getLiteral("o").getLexicalForm().toString());
            }

        return labels;
    }
    public ArrayList<String> getAlternative(String sparqlPV, String predicat, String language, String kb){


        ArrayList<String> altern = new ArrayList<>();

            kb="http://dbpedia.org/sparql";

        Query query1 = QueryFactory.create(sparqlPV);
        QueryExecution qExe = QueryExecutionFactory.sparqlService(kb, query1);
        ResultSet result;
        result = qExe.execSelect();
        while (result.hasNext()) {
            QuerySolution rsnext = result.next();//
            String s = predicat.replace("?","");
            String nxt = rsnext.getResource(s).toString();
            altern.add(nxt);
        }
        return altern;
    }

}
