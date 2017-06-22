package eu.wdaqua.SPARQLtoUser;

import org.apache.jena.query.*;

import java.util.ArrayList;

/**
 * Created by dryous on 22/06/2017.
 */
public abstract class Wikidata implements KnowledgeBase{

    public ArrayList<String> getLabel(String uri, String language, String kb) {

        ArrayList<String> lab = new ArrayList<>();
        String res
                = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " PREFIX schema: <http://schema.org/> "
                + " SELECT DISTINCT ?o ?x WHERE { "
                + " <" + uri + "> rdfs:label ?o . "
                + "  OPTIONAL { <" + uri + ">  schema:description ?x FILTER( lang(?x)=\"" + language + "\" )} . "
                + "  FILTER( lang(?o)=\"" + language + "\" )"
                + "} limit 20 ";

            kb = "https://query.wikidata.org/sparql";
            Query query1 = QueryFactory.create(res);
            QueryExecution qExe = QueryExecutionFactory.sparqlService(kb, query1);
            ResultSet result = qExe.execSelect();
            while (result.hasNext()) {
                QuerySolution rsnext = result.next();
                lab.add(rsnext.getLiteral("o").getLexicalForm().toString());
                System.out.println(rsnext.getLiteral("o").getLexicalForm().toString());
                if (rsnext.getLiteral("x") != null) {
                    lab.add(rsnext.getLiteral("x").getLexicalForm().toString());
                }
            }

        return lab;
        }

    public ArrayList<String> getAlternative(String sparqlPV,String  predicat, String language, String kb){
        ArrayList<String> altern = new ArrayList<>();
            kb = "https://query.wikidata.org/sparql";

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
