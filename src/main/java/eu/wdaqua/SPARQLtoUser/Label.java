package eu.wdaqua.SPARQLtoUser;

import eu.wdaqua.SPARQLtoUser.knowledgebase.Dbpedia;
import eu.wdaqua.SPARQLtoUser.knowledgebase.KnowledgeBase;

import eu.wdaqua.SPARQLtoUser.knowledgebase.Wikidata;
import eu.wdaqua.SPARQLtoUser.knowledgebase.musicbrainz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by Youssef on 16/05/17.
 */
// we put both of the label and the description of the uri input in an array
public class Label {
    private static final Logger logger = LoggerFactory.getLogger(Label.class);

    public ArrayList<String> getLabel(String s, String l, String k, String endpoint) {
        ArrayList<String> lab;

        if (k.contains("wikidata")) {
            KnowledgeBase kbWikiLabel = new Wikidata(endpoint);
            lab= kbWikiLabel.getLabel(s, l, k, endpoint);
        } else if (k.contains("dbpedia")) {
            KnowledgeBase kbdbpeLabel = new Dbpedia(endpoint);
            lab=kbdbpeLabel.getLabel(s, l, k, endpoint);
        } else if (k.contains("musicbrainz")) {
            KnowledgeBase kbmusicbrainz = new musicbrainz(endpoint);
            lab=kbmusicbrainz.getLabel(s, l, k, endpoint);
        } else {
            logger.info("not dbpedia && not wikidata");
            lab=null;
        }
        return lab;
    }

    // in case of predicate variable, we take all possibles predicates for one query
    public ArrayList<String> getAlternatives (String res,String p, String l, String k, String endpoint){

        ArrayList<String> altern = new ArrayList<>();


        if (k.contains("wikidata")){

            KnowledgeBase kbWikiAltern = new Wikidata(endpoint);
            logger.info(" In GetAltern ===+++===+++===+++===+++===+++===+++====>> ");

            altern=kbWikiAltern.getAlternative(res, p, l, k, endpoint);


        }else if (k.contains("dbpedia")){
            KnowledgeBase kbdbpediaAltern = new Dbpedia(endpoint);
        altern=kbdbpediaAltern.getAlternative(res, p, l, k, endpoint);
            logger.info("In GetAltern ===+++===+++===+++===+++===+++===+++====>> ");

        }else if (k.contains("musicbrainz")){
        KnowledgeBase kbmusicbrainzAltern = new musicbrainz(endpoint);
        altern=kbmusicbrainzAltern.getAlternative(res, p, l, k, endpoint);
        logger.info("In GetAltern ===+++===+++===+++===+++===+++===+++====>> ");
        }

        else {
            logger.info("not dbpedia && not wikidata");
        }

        return altern;
    }

}
