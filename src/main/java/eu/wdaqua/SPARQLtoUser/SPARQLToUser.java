package eu.wdaqua.SPARQLtoUser;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 *
 * @author youssef
 */
public class SPARQLToUser {
    private static final Logger logger = LoggerFactory.getLogger(SPARQLToUser.class);

    private String sparql;
    private String lang;
    private String kb;
    private String interpretation;
    String subject;
    String predicate;
    String object;
    String result;
    List<String> variables;
    List<String> predicVar = new ArrayList<>();
    boolean contNotTriple= false;
    int contVarPredic=0;
    ArrayList<String> strSubject = new ArrayList<>();
    ArrayList<String> strPredicate = new ArrayList<>();
    ArrayList<String> strObject = new ArrayList<>();

    Label label = new Label();

    public SPARQLToUser() {} //empty constructor needed for spring

    public SPARQLToUser(String sparql, String lang, String kb) {
        this.sparql = sparql;
        this.lang = lang;
        this.kb = kb;
        this.interpretation = go(sparql, lang, kb);
    }

    public String getSparql() {
        return sparql;
    }
    public String getLang() {
        return lang;
    }
    public String getKb() { return kb; }

    public String getInterpretation() {
        return interpretation;
    }

    public String go(String strq, String l, String k) {
        QueryParse p = new QueryParse();
        p.parse(strq);

        //treat VALUE part
        if (p.getValue()!= null && p.getCanBeProcessed()==true) {
            StringUtils util = new StringUtils();
            String ur = p.getValue().getRows().get(0).get(p.getValue().getVars().get(0)).toString();
            ArrayList<String> lab = label.getLabel(replaceProp(ur), l, k);
            result = lab.get(0);
            if (lab.size() == 2) {
                if (lab.get(1).length() > 30) {
                    result += " (" + lab.get(1).substring(0, 30) + "...)";
                } else {
                    result += " (" + lab.get(1) + ")";

                }

            }
        }

        //treat triple pattrens
        if (p.getTriples()!= null && p.getCanBeProcessed()==true) {
            ListIterator<TriplePath> triples = p.getTriples();
            ArrayList<Node> nodes = new ArrayList<Node>();
            while (triples.hasNext()) {
                // ...and grab the subject
                TriplePath triple = triples.next();
                if (triple.isTriple()) {
                    if (triple.getSubject().isURI())
                        subject = triple.getSubject().toString();
                    if (triple.getPredicate().isURI())
                        predicate = triple.getPredicate().toString();
                    if (triple.getObject().isURI())
                        object = triple.getObject().toString();

                    if (triple.getSubject().isURI()) {
                        ArrayList<String> labS = label.getLabel(replaceProp(subject), l, k);
                        // we put the label in the position 0 of the output array getLabel
                        String ss = labS.get(0);
                        if (labS.size() == 2) {
                            if (labS.get(1).length() < 30) {
                                // Description (if exist) is in the position 1 of the output array getLabel
                                ss += " (" + labS.get(1) + ")";
                            } else {
                                ss += " (" + labS.get(1).substring(0, 27) + "...)";
                            }
                        }
                        strSubject.add(ss);
                    } else {
                        strSubject.add("null");
                    }
                    if (triple.getObject().isURI()) {
                        ArrayList<String> labO = label.getLabel(replaceProp(object), l, k);
                        String so = labO.get(0);
                        if (labO.size() == 2) {
                            if (labO.get(1).length() < 30) {
                                so += " (" + labO.get(1) + ")";
                            } else {
                                so += " (" + labO.get(1).substring(0, 27) + "...)";
                            }
                        }
                        strObject.add(so);
                    } else {
                        strObject.add("null");
                    }
                    if (triple.getPredicate().isURI()) {
                        ArrayList<String> labP = label.getLabel(replaceProp(predicate), l, k);
                        // for the predicate we don't need description So we put just the label
                        String sp = labP.get(0);
                        strPredicate.add(sp);


                    } else if (triple.getPredicate().isVariable()) {
                        //    we recupere the name of the variable and we  put it in an array
                        predicVar.add(triple.getPredicate().toString());
                        System.out.println("PredictVar ! " + predicVar);
                        strPredicate.add("null");
                        contVarPredic += 1;
                    }


                } else {
                    System.out.println("it'interpretation not a triple");
                    contNotTriple = true;
                }
            }
            for (int i = strPredicate.size() - 1; i >= 0; i--) {
                // in case of it'interpretation a Select type, not aggregators yet and all the contents of the query are triples, and there is not a predicate variable
                if (p.getQuery().isSelectType() && !p.getQuery().hasAggregators() && contNotTriple == false && contVarPredic == 0) {
                    if (strPredicate.size() != 0) {
                        result += "/" + strPredicate.get(i);
                    }
                    if (strSubject.size() != 0) {
                        result += "/" + strSubject.get(i);
                    }
                    if (strObject.size() != 0) {
                        result += "/" + strObject.get(i);
                    }
                }
                // case of 2 triples and one predicate is variable
                else if (p.getQuery().isSelectType() && !p.getQuery().hasAggregators() && contNotTriple == false && contVarPredic == 1 && strPredicate.size() == 2) {
                    if (strPredicate.size() != 0 && strPredicate.get(i) != "null") {
                        result += "/" + strPredicate.get(i);
                    }
                    if (strSubject.size() != 0 && strSubject.get(i) != "null") {
                        result += "/" + strSubject.get(i);
                    }
                    if (strObject.size() != 0 && strObject.get(i) != "null") {
                        result += "/" + strObject.get(i);
                    }
                    if (!result.contains("/~")) {
                        result += "/~ ";
                        if (strSubject.get(0) != "null") {
                            ArrayList<ArrayList<String>> gAlt = label.getAlternatives(strq.replaceFirst(variables.get(0), predicVar.get(0).replace("?", "")), predicVar, l, k);
                            for (int j = 0; j < gAlt.size(); j++) {
                                ArrayList<String> labJ = label.getLabel(replaceProp(gAlt.get(0).get(j)), l, k);
                                //                             if(labJ.get(0).length()<30)
                                result += labJ.get(i) + ",  ";
                            }
                            result += " ~";

                        } else {
                            ArrayList<ArrayList<String>> gAlt = label.getAlternatives(strq.replaceFirst(variables.get(0), predicVar.get(0).replace("?", "")), predicVar, l, k);
                            for (int j = 0; j < gAlt.size(); j++) {
                                ArrayList<String> labJ = label.getLabel(replaceProp(gAlt.get(i).get(j)), l, k);
                                //                                  if(labJ.get(0).length()<30)
                                result += labJ.get(0) + ", ";
                            }
                            result += " ~";

                        }
                    }

                    // case of both of 2 predicates are variables
                } else if (p.getQuery().isSelectType() && !p.getQuery().hasAggregators() && contNotTriple == false && contVarPredic == 2 && strPredicate.size() == 2) {
                    String strqAlt = strq.replaceFirst(variables.get(0), predicVar.get(0).replace("?", "") + " ?" + predicVar.get(1).replace("?", ""));
                    System.out.println("STRQALT: " + strqAlt);
                    result += "//";

                    ArrayList<ArrayList<String>> gAlt = label.getAlternatives(strqAlt, predicVar, l, k);
                    ArrayList<String> labJ = new ArrayList<>();
                    //   for (int z=0; z < gAlt.size(); z++) {
                    for (int j = 0; j < gAlt.get(i).size(); j++) {
                        labJ = label.getLabel(replaceProp(gAlt.get(i).get(j)), l, k);
                        //                                        if(labJ.get(0).length()<30)
                        result += labJ.get(0) + ", ";
                    }
                    // }
                    if (strPredicate.size() != 0 && strPredicate.get(i) != "null") {
                        result += "/" + strPredicate.get(i);
                    }
                    if (strSubject.size() != 0 && strSubject.get(i) != "null") {
                        result += "/" + strSubject.get(i);
                    }
                    if (strObject.size() != 0 && strObject.get(i) != "null") {
                        result += "/" + strObject.get(i);
                    }
                    result.replaceFirst("//", "");
                    result.replaceFirst("/", "");
                    result.replaceAll(", /", " /");
                } else {
                    result = "";
                }
            }

            // clean the result
            if (result != null) {
                result.replaceAll(", ~", " ~");
                result = result.replaceAll("/null", "");
                result = result.replaceAll("/null/", "/");
                result = result.replaceAll("null/", "");
                result = result.replaceAll("/instance of/", "/");
                result = result.replaceAll("instance of/", "");
            }
        }
        return result;
    }

    public String replaceProp (String str){

        return str.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity");
    }

}
