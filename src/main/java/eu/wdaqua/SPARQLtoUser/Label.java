package eu.wdaqua.SPARQLtoUser;

import org.apache.jena.query.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Youssef on 16/05/17.
 */
// we put both of the label and the description of the uri input in an array
public class Label {
    private static final Logger logger = LoggerFactory.getLogger(Label.class);

    public ArrayList<String> getLabel(String s, String l, String k) {
        ArrayList<String> lab = new ArrayList<>();
        if (k.contains("wikidata")) {
            KnowledgeBase kbWikiLabel = new Wikidata() {
                @Override
                public ArrayList<String> getLabel(String uri, String language, String kb) {
                    return super.getLabel(uri, language, kb);
                }
            };
            lab= kbWikiLabel.getLabel(s, l, k);

        } else if (k.contains("dbpedia")) {

            KnowledgeBase kbdbpeLabel = new Dbpedia() {
                @Override
                public ArrayList<String> getLabel(String uri, String language, String kb) {
                    return super.getLabel(uri, language, kb);
                }
            };
            lab=kbdbpeLabel.getLabel(s, l, k);
        } else {
            logger.info("not dbpedia && not wikidata");
            lab=null;
        }
        return lab;
    }

    // in case of predicate variable, we take all possibles predicates for one query
    public ArrayList<String> getAlternatives (String res,String p, String l, String k){

        ArrayList<String> altern = new ArrayList<>();
        if (k.contains("wikidata")){

            KnowledgeBase kbWikiAltern = new Wikidata() {
                @Override
                public ArrayList<String> getAlternative(String sparqlPV, String predicat, String language, String kb) {
                    return super.getAlternative(sparqlPV, predicat, language, kb);
                }
            };
            logger.info(" In GetAltern ===+++===+++===+++===+++===+++===+++====>> ");

            altern=kbWikiAltern.getAlternative(res, p, l, k);


        }else if (k.contains("dbpedia")){
            KnowledgeBase kbdbpediaAltern = new Dbpedia() {
                @Override
                public ArrayList<String> getAlternative(String sparqlPV, String predicat, String language, String kb) {
                    return super.getAlternative(sparqlPV, predicat, language, kb);
                }
            };
        altern=kbdbpediaAltern.getAlternative(res, p, l, k);
            logger.info("In GetAltern ===+++===+++===+++===+++===+++===+++====>> ");
        }else {
            logger.info("not dbpedia && not wikidata");
        }

        return altern;
    }

}
