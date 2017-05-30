package eu.wdaqua.SPARQLtoUser;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;


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
    public static String newline = System.getProperty("line.separator");
    String result;
    int goCount=0;
    List<String> variables;
    List<String> predicVar = new ArrayList<>();
    boolean contNotTriple= false;
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

        logger.info("I am in the GO function for the ");
        QueryParse p = new QueryParse();
        p.parse(strq);
        System.out.println("getCanProcessed "+ p.getCanBeProcessed());
        variables=p.getQuery().getResultVars();
        //treat VALUE part
        if (p.getValue() != null && p.getCanBeProcessed()) {
            StringUtils util = new StringUtils();
            String ur = p.getValue().getRows().get(0).get(p.getValue().getVars().get(0)).toString();
            ArrayList<String> lab = label.getLabel(replaceProp(ur), l, k);
            result = lab.get(0);
            if (lab.size() == 2) {
                    result += " (" + retress(lab.get(1)) + ")";
            }
        }
        //treat triple pattrens
        else if (p.getTriples() != null && p.getCanBeProcessed()) {
            ListIterator<TriplePath> triples = p.getTriples();
            ArrayList<Node> nodes = new ArrayList<Node>();
            while (triples.hasNext()) {
                // ...and grab the subject
                TriplePath triple = triples.next();
                if (triple.isTriple()) {
                if (result!=null){
                result += newline;
                }
                    if (triple.getPredicate().isURI()) {
                        ArrayList<String> labP = label.getLabel(replaceProp(triple.getPredicate().toString()), l, k);
                        // for the predicate we don't need description So we put just the label
                        String sp = labP.get(0);
                        result+= sp;
                    } else if (triple.getPredicate().isVariable()) {
//                        predicVar.add(triple.getPredicate().toString());

                        ArrayList<String> gAlt = label.getAlternatives(strq.replaceFirst(variables.get(0), triple.getPredicate().toString().replace("?", "")), triple.getPredicate().toString(), l, k);

                        if (gAlt.size()!=0){
                            for (int i = 0; i < gAlt.size(); i++) {
                               ArrayList<String> getlabi=label.getLabel(replaceProp(gAlt.get(i)), l, k);
                               if (getlabi.toString()!="[]") {
                                   System.out.println("eh oui  il est différent de null !!!");

                                   if ((i==0)){
                                }
                                if (label.getLabel(replaceProp(gAlt.get(i)), l, k).size()!=0){
                                    result += retress(label.getLabel(replaceProp(gAlt.get(i)), l, k).get(0));
                                }
                                if (i<gAlt.size()-1){
                                    result+= ", ";
                               }
                            }
                            }
                        }
                    }


                    if (triple.getSubject().isURI()) {
                        ArrayList<String> labS = label.getLabel(replaceProp(triple.getSubject().toString()), l, k);
                        // we put the label in the position 0 of the output array getLabel
                        String ss = "/" + labS.get(0);
                        if (labS.size() == 2) {
                                ss += " (" + retress(labS.get(1)) + ") ";
                            }
                        if (ss!=null) {
                            result += ss;
                        }
                    }

                    if (triple.getObject().isURI()) {
                        ArrayList<String> labO = label.getLabel(replaceProp(triple.getObject().toString()), l, k);
                        String so = labO.get(0);
                        if (labO.size() == 2) {
                            so += " (" + retress(labO.get(1)) + ") ";

                        }
                        if (so!=null){
                        result += "/" + so;
                        }
                    }

                    } else {
                    logger.info("it's not a triple");
                    result = null;
                    break;
                }
            }


        }else {
            result = "";
        }

        // clean the result
        if (result != null) {
            result = result.replaceAll(", ]", " ]");
            result = result.replaceAll("/null", "");
            result = result.replaceAll("null", "");
            result = result.replaceAll("/null/", "/");
            result = result.replaceAll("null/", "");
            result = result.replaceAll("/instance of/", "/");
      //      result = result.replaceAll("instance of/", "");
        }


        return result;
    }

    public String replaceProp (String str){

        return str.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity");
    }
    public String retress (String str){
        if (str.length() > 30) {
        str = str.substring(0, 27) + "...";
        }

        return str;
    }

}
