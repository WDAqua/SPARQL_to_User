package eu.wdaqua.SPARQLtoUser.knowledgebase;

import org.apache.jena.query.*;

import java.util.ArrayList;

/**
 * Created by Dennis on 21/07/2017.
 */
public class Dblp extends KnowledgeBase  {

    public Dblp(String endpoint){

    }

    @Override
    public ArrayList<String> getLabel(String uri, String language, String kb, String ep) {
        ArrayList<String> labels = new ArrayList<>();
        String res =
                "SELECT ?label  where { " +
                "  OPTIONAL{ " +
                //"<" + value + "> rdfs:label ?label . FILTER (lang(?label)=\""+ lang +"\" || lang(?label)=\"en\" || lang(?label)=\"de\" || lang(?label)=\"fr\" || lang(?label)=\"it\")" +
                "    <" + uri + "> foaf:name ?label . " +
           //     "  FILTER( lang(?label)=\"" + language + "\" )" +
                "} " +
                "  FILTER( lang(?label)=\"" + language + "\" || lang(?label)=\"\")" +
                "} ";

        Query query1 = QueryFactory.create(res);
        QueryExecution qExe = QueryExecutionFactory.sparqlService(ep, query1);
        ResultSet result = qExe.execSelect();
        while (result.hasNext()) {
            QuerySolution rsnext = result.next();
            labels.add(rsnext.getLiteral("label").getLexicalForm().toString());
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
