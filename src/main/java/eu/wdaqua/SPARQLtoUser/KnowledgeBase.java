package eu.wdaqua.SPARQLtoUser;

import java.util.ArrayList;

/**
 * Created by dryous on 22/06/2017.
 */
public interface KnowledgeBase {

    public ArrayList<String> getLabel(String uri, String language, String kb);
    public ArrayList<String> getAlternative(String res,String  predicate, String language, String kb);

}
