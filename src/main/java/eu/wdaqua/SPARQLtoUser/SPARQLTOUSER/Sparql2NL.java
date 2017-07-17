package eu.wdaqua.SPARQLtoUser.SPARQL2NL;

/**
 * Created by dryous on 12/07/2017.
 */



import org.aksw.sparql2nl.naturallanguagegeneration.SimpleNLGwithPostprocessing;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.dllearner.kb.sparql.SparqlEndpoint;
import simplenlg.lexicon.Lexicon;

public class Sparql2NL {

     public String Sparql2NL(String query){
        String result=null;
       /*  SparqlEndpoint ep = SparqlEndpoint.getEndpointLinkedMDB();
         Lexicon lexicon = Lexicon.getDefaultLexicon();
         SimpleNLGwithPostprocessing snlg = new SimpleNLGwithPostprocessing(ep);

         System.out.println("\n----------------------------------------------------------------");
         Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
         result = snlg.getNLR(sparqlQuery); */
return result;

     }


}
