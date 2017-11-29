package eu.wdaqua.SPARQLtoUser.knowledgebase;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import java.util.ArrayList;

/**
 * Created by Dennis on 21/07/2017.
 */
public class Scigraph extends KnowledgeBase  {

    public Scigraph(String endpoint){

    }

    @Override
    public ArrayList<String> getLabel(String uri, String language, String kb, String ep) {
        ArrayList<String> labels = new ArrayList<>();
        if (uri.startsWith("\"")){
            labels.add(uri);
            return labels;
        }
        String res =
                "SELECT ?label  where { "
                + "OPTIONAL {<" + uri + "> <http://www.w3.org/2004/02/skos/core#altLabel> ?label . } "
                + "OPTIONAL {<" + uri + "> <http://scigraph.springernature.com/ontologies/core/publishedName> ?label } "
                + "OPTIONAL {<" + uri + "> <http://scigraph.springernature.com/ontologies/core/title> ?label } "
                + "OPTIONAL {<" + uri + "> <http://www.w3.org/2000/01/rdf-schema#label> ?label } "
                + "} ";
        System.out.println(res);
        Query query1 = QueryFactory.create(res);
        QueryExecution qExe = QueryExecutionFactory.sparqlService(ep, query1);
        ResultSet result = qExe.execSelect();
        while (result.hasNext()) {
            QuerySolution rsnext = result.next();
            if (rsnext.getLiteral("label")!=null) {
                System.out.println("NEXT-" + rsnext.toString());
                labels.add(rsnext.getLiteral("label").getLexicalForm().toString());
            }
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
        return altern;    }


}
