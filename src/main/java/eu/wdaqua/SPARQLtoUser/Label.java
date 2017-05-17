package eu.wdaqua.SPARQLtoUser;

import org.apache.jena.query.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yousseff on 16/05/17.
 */
// we put both of the label and the description of the uri input in an array
public class Label {
    private static final Logger logger = LoggerFactory.getLogger(Label.class);

    public ArrayList<String> getLabel(String s, String l, String k) {
        logger.info("Heyoo I AM IN getLabel again !!!=====================>>");
        ArrayList<String> lab = new ArrayList<>();
        String res
                = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " PREFIX schema: <http://schema.org/> "
                + " SELECT DISTINCT ?o ?x WHERE { "
                + " <" + s + "> rdfs:label ?o . "
                + "  OPTIONAL { <" + s + ">  schema:description ?x FILTER( lang(?x)=\"" + l + "\" )} . "
                + "  FILTER( lang(?o)=\"" + l + "\" )"
                + "} limit 20 ";
        if (s.contains("wikidata")) {
            k = "https://query.wikidata.org/sparql";
            Query query1 = QueryFactory.create(res);
            QueryExecution qExe = QueryExecutionFactory.sparqlService(k, query1);
            ResultSet result = qExe.execSelect();
            while (result.hasNext()) {
                QuerySolution rsnext = result.next();
                lab.add(rsnext.getLiteral("o").getLexicalForm().toString());
                System.out.println(rsnext.getLiteral("o").getLexicalForm().toString());
                if (rsnext.getLiteral("x") != null) {
                    lab.add(rsnext.getLiteral("x").getLexicalForm().toString());
                }
            }
        } else if (s.contains("dbpedia")) {
            k = "http://dbpedia.org/sparql";
            Query query1 = QueryFactory.create(res);
            QueryExecution qExe = QueryExecutionFactory.sparqlService(k, query1);
            ResultSet result = qExe.execSelect();
            while (result.hasNext()) {
                QuerySolution rsnext = result.next();
                lab.add(rsnext.getLiteral("o").getLexicalForm().toString());
            }
        } else {
            lab.add("categorie");
            System.out.println("---------------not dbpedia && not wikidata");
        }
        return lab;
    }

    // in case of predicate variable, we take all possibles predicates for one query
    public ArrayList<ArrayList<String>> getAlternatives (String res, List<String> p, String l, String k){

        ArrayList<ArrayList<String>> altern = new ArrayList<>();

        if (res.contains("wikidata"))
            k = "https://query.wikidata.org/sparql";
        else if (res.contains("dbpedia"))
            k="http://dbpedia.org/sparql";

        System.out.println("ALTERNATIVE QUERY ... :"+res);
        for (int i=0; i<p.size(); i++) {
            ArrayList<String> petitAltern = new ArrayList<>();
            Query query1 = QueryFactory.create(res);
            QueryExecution qExe = QueryExecutionFactory.sparqlService(k, query1);
            ResultSet result;
            result = qExe.execSelect();
            while (result.hasNext()) {
                QuerySolution rsnext = result.next();//
                String s = p.get(i).replace("?","").toString();
                System.out.println("Ressources---"+i+"-- " + s);
                String nxt = rsnext.getResource(s).toString();
                petitAltern.add(nxt);
                System.out.println("petitAltern  "+ petitAltern);
                altern.add(petitAltern);
                petitAltern=new ArrayList<>();
            }


        }
        System.out.println("ALTERN "+altern);
        return altern;
    }

}
