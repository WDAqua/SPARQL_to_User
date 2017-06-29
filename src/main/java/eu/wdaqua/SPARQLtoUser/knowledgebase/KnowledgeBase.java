package eu.wdaqua.SPARQLtoUser.knowledgebase;

import java.util.ArrayList;

/**
 * Created by dryous on 22/06/2017.
 */
public abstract class KnowledgeBase {
    String endpoint = null;

    public KnowledgeBase(){}

    public KnowledgeBase(String endpoint){
        this.endpoint = endpoint;
    }

    //what needs to be i,plemented?
    public abstract ArrayList<String> getLabel(String uri, String language, String kb, String ep);

    public abstract ArrayList<String> getAlternative(String res, String  predicate, String language, String kb, String ep);

}
