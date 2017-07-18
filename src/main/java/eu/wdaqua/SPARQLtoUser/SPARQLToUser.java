package eu.wdaqua.SPARQLtoUser;

import org.apache.jena.base.Sys;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;


/**
 * @author youssef
 */
public class SPARQLToUser {
    private static final Logger logger = LoggerFactory.getLogger(SPARQLToUser.class);
    private String sparql;
    private String lang;
    private String kb;
    private String ep;
    private String interpretation;

    public static String newline = System.getProperty("line.separator");
    String result;
    int goCount = 0;
    List<String> variables;
    List<String> predicVar = new ArrayList<>();
    boolean contNotTriple = false;
    Label label = new Label();


    public SPARQLToUser() {
    } //empty constructor needed for spring

    public SPARQLToUser(String sparql, String lang, String kb, String ep) {
        this.sparql = sparql;
        this.lang = lang;
        this.kb = kb;
        this.ep= ep;
        this.interpretation = go(sparql, lang, kb, ep);

    }

    public String getSparql() {
        return sparql;
    }

    public String getLang() {
        return lang;
    }

    public String getKb() {
        return kb;
    }

    public String getInterpretation() {
        return interpretation;
    }

    public String go(String strq, String l, String k, String ep) {


        ArrayList <String> prefx=new ArrayList<>();
        prefx.add(null);
        prefx.add("");
        int dir=0;
        String aggregIntroduce = null;
        QueryParse p = new QueryParse();
        p.parse(strq);
        logger.info("getCanProcessed " + p.getCanBeProcessed());
        variables = p.getQuery().getResultVars();
        //treat VALUE part
        if (p.getValue() != null && p.getCanBeProcessed()) {
            StringUtils util = new StringUtils();
            String ur = p.getValue().getRows().get(0).get(p.getValue().getVars().get(0)).toString();
            ArrayList<String> lab = label.getLabel(replaceProp(ur), l, k, ep);
            result = lab.get(0);
            if (lab.size() == 2) {
                result += " (" + retressValue(lab.get(1)) + ")";
            }
        }

        //treat triple pattrens
        else if (p.getTriples() != null && p.getCanBeProcessed()) {
            ListIterator<TriplePath> triples = p.getTriples();
            ArrayList<Node> nodes = new ArrayList<Node>();
            if (p.getQuery().hasAggregators() || p.getQuery().hasOrderBy()) {

                if (p.getQuery().hasOrderBy()){
                    dir = p.getQuery().getOrderBy().get(0).getDirection(); // Direction c-a-d ASC / DESC
                }

                if (p.getQuery().hasLimit()){
                    int lim = (int) p.getQuery().getLimit(); // limit
                }

                if (dir == -1) {
                    aggregIntroduce = "the most (";
                    prefx.add("the most (");
                    System.out.println("THE MOST AAA");
                }
                else if (dir == 1) {
                    aggregIntroduce = "the least (";
                    prefx.add("the least (");


                }else {
                    aggregIntroduce = "how many (";
                    prefx.add("how many (");
                }
            }
            if (p.getQuery().hasAggregators() || p.getQuery().hasOrderBy()) {
                result = aggregIntroduce;
            }
            if (p.getQuery().isAskType()) {
                result = "Check (";
                prefx.add("Check (");
            }
            while (triples.hasNext()) {

                // ...and grab the subject
                TriplePath triple = triples.next();
                if (triple.isTriple()) {
                    if (result != null && !p.getQuery().isAskType() && !p.getQuery().hasAggregators() && !p.getQuery().hasOrderBy()) {
                        result += newline;
                    }
                    if (p.getQuery().isSelectType()) {
                        if (!prefx.contains(result) && writePredicate(triple, strq, l, k, variables, ep, p.getQuery())!="")
                            result += " / ";
                        System.out.println("This  is your search variables: "+ variables);

                        if (writePredicate(triple, strq, l, k, variables, ep, p.getQuery())!=null) {
                            result += writePredicate(triple, strq, l, k, variables, ep, p.getQuery());
                        }
                        if (triple.getSubject().isURI()) {
                            if (!prefx.contains(result) && writeSubject(triple, strq, l, k)!="") {
                                result += " / ";
                            }
                            result += writeSubject(triple, strq, l, k);
                        }
                        if (triple.getObject().isURI()) {
                            if (!prefx.contains(result) && writeObject(triple, strq, l, k)!="") {
                                result += " / ";
                            }
                            result += writeObject(triple, strq, l, k);
                        }
                    } else if (p.getQuery().isAskType()) {

                        if (triple.getSubject().isURI() && triple.getObject().isURI()) {

                            if (triple.getSubject().isURI()) {
                                result += writeSubject(triple, strq, l, k);
                            }
                            if ((triple.getPredicate().isURI()) || (triple.getPredicate().isVariable())) {
                                if (!prefx.contains(result) && writePredicate(triple, strq, l, k, variables, ep, p.getQuery())!="") {
                                    result += " / ";
                                }
                                result += writePredicate(triple, strq, l, k, variables, ep, p.getQuery());
                            }
                            if (!prefx.contains(result) && writeObject(triple, strq, l, k)!="") {
                                result += " / ";
                            }
                            if (triple.getObject().isURI()) {
                                result += writeObject(triple, strq, l, k);
                            }
                        } else {
                            if (triple.getSubject().isURI()) {
                                result += writeSubject(triple, strq, l, k);
                            }
                            if (triple.getObject().isURI()) {
                                if (!prefx.contains(result) && writeObject(triple, strq, l, k)!="") {
                                    result += " / ";
                                }
                                result += writeObject(triple, strq, l, k);
                            }

                            if ((triple.getPredicate().isURI()) || (triple.getPredicate().isVariable())) {

                                if (!prefx.contains(result) && writePredicate(triple, strq, l, k, variables, ep, p.getQuery())!="") {
                                    result += " / ";
                                }
                                result += writePredicate(triple, strq, l, k, variables, ep, p.getQuery());
                            }
                        }
                    }
                } else {
                    logger.info("it's not a triple");
                    result = null;
                    break;
                }
            }

            if (p.getQuery().isAskType() || p.getQuery().hasAggregators()|| p.getQuery().hasOrderBy()) {
                result += ")";
            }
        } else {
            result = "null";
        }

        // clean the result
        if (result != null) {
            result = result.replaceAll("/null", "");
            result = result.replaceAll("null", "");
            result = result.replaceAll("/null/", "/");
            result = result.replaceAll("null/", "");
            result = result.replaceAll("/ instance of /", "/");
            result = result.replaceAll("instance of /", "");
            result = result.replaceAll("Link from a Wikipage to ano..., ", "");
            result = result.replaceAll("Link from a Wikipage to ano... /", "");

        }

        return result;
    }

    public String replaceProp(String str) {

        return str.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity");
    }

    public String retress(String str) {
        if (str.length() > 30) {
            str = str.substring(0, 27) + "...";
        }
        return str;
    }
    public String retressValue(String str) {
        if (str.length() > 77) {
            str = str.substring(0, 77) + "...";
        }
        return str;
    }

    public String writePredicate(TriplePath triple, String strq, String l, String k, List<String> vars, String ep, org.apache.jena.query.Query q) {

        String res = "";
        if (triple.getPredicate().isURI()) {
            ArrayList<String> labP = label.getLabel(replaceProp(triple.getPredicate().toString()), l, k, ep);
            // for the predicate we don't need description So we put just the label
            if (labP.size() != 0) {
                String sp = labP.get(0);
                res += sp;
            } else {
                logger.info("there is no label for this uri !!" + replaceProp(triple.getPredicate().toString()));
            }

        } else if (triple.getPredicate().isVariable()) {
            ArrayList<String> gAlt = null;
            //        System.out.println("query get alternative"+ strq.replaceFirst(vars.get(0), triple.getPredicate().toString().replace("?", "")));
            String s = "SELECT DISTINCT ";
            s+= triple.getPredicate().toString();
            s+= " WHERE ";
            s+=q.getQueryPattern().toString();
            s+= " limit 1000 ";
            gAlt = label.getAlternatives(s, triple.getPredicate().toString(), l, k, ep);

            if (gAlt.size() != 0) {
                for (int i = 0; i < gAlt.size(); i++) {
                    ArrayList<String> getlabi = label.getLabel(replaceProp(gAlt.get(i)), l, k, ep);
                    if (getlabi!= null) {
                        if (label.getLabel(replaceProp(gAlt.get(i)), l, k, ep).size() != 0) {
                            res += retress(label.getLabel(replaceProp(gAlt.get(i)), l, k, ep).get(0));
                        }
                        if (i < gAlt.size() - 1) {
                            res += ", ";
                        }
                    }
                }
            }

        } else {
            res = "null";
        }

        return res;
    }

    public String writeSubject(TriplePath triple, String strq, String l, String k) {
        String res="";
        String ss="";
        ArrayList<String> labS = label.getLabel(replaceProp(triple.getSubject().toString()), l, k, ep);
        // we put the label in the position 0 of the output array getLabel
        if (labS.size() != 0) {
            ss = labS.get(0);
        }
        if (labS.size() == 2) {
            ss += " (" + retress(labS.get(1)) + ") ";
        }
        if (ss != null) {
            res += ss;
        }
        System.out.println("WRITE SUBJECT"+res);
        return res;
    }

    public String writeObject(TriplePath triple, String strq, String l, String k) {
        String res = "";
        String so = "";
        ArrayList<String> labO = label.getLabel(replaceProp(triple.getObject().toString()), l, k, ep);
        if (labO.size()!=0) {
            so = labO.get(0);
        }
        if (labO.size() == 2) {
            so += " (" + retress(labO.get(1)) + ") ";
        }
        res += so;

        return res;
    }
}
