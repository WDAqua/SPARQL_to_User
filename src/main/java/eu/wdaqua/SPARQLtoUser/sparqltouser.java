    package eu.wdaqua.SPARQLtoUser;

    import org.apache.jena.graph.Node;
    import org.apache.jena.query.*;
    import org.apache.jena.sparql.core.TriplePath;
    import org.apache.jena.sparql.expr.Expr;
    import org.apache.jena.sparql.syntax.*;
    import org.apache.jena.sparql.util.StringUtils;

    import java.lang.reflect.Array;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.ListIterator;


    /**
    *
    * @author youssef
    */
public class sparqltouser {

    private String sparql;
    private String lang;
    private String kb;
    private String s;
    String subject;
    String predicate;
    String object;
    String result;
    List<String> rech;
    List<String> predicVar = new ArrayList<>();
    boolean contNotTriple= false;
    int contVarPredic=0;
    ArrayList<String> strSubject = new ArrayList<>();
    ArrayList<String> strPredicate = new ArrayList<>();
    ArrayList<String> strObject = new ArrayList<>();

    public sparqltouser() {

    }
    public sparqltouser(String sparql, String lang, String kb) {
        this.sparql = sparql;
        this.lang = lang;
        this.kb = kb;
        this.s = go(sparql, lang, kb);
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
    public String getS() {
        return s;
    }
    public String go(String strq, String l, String k) {
        Query q;

        try {
            q = QueryFactory.create(strq, Syntax.syntaxARQ);
        } catch (QueryParseException e) {
            System.out.println(e);
            return null;
        }
        rech=q.getResultVars();
        System.out.println("ResultVars +++ "+rech);
        ElementWalker.walk(q.getQueryPattern(),
                // For each element...
                new ElementVisitorBase() {
                    // ...when it's a block of triples...
                    @Override
                    public void visit(ElementPathBlock el) {
                        // ...go through all the triples...

                        ListIterator<TriplePath> triples = el.getPattern().iterator();
                        ArrayList<Node> nodes = new ArrayList<Node>();
                        while (triples.hasNext()) {
                            // ...and grab the subject
                            TriplePath triple = triples.next();
                            if (triple.isTriple()) {
                                if(triple.getSubject().isURI())
                                subject = triple.getSubject().toString();
                                if(triple.getPredicate().isURI())
                                predicate = triple.getPredicate().toString();
                                if(triple.getObject().isURI())
                                object = triple.getObject().toString();

                                if (triple.getSubject().isURI()) {
                                    ArrayList<String> labS = getLabel(replaceProp(subject), l, k);
                                // we put the label in the position 0 of the output array getLabel
                                    String ss = labS.get(0);
                                    if (labS.size() == 2) {
                                        if (labS.get(1).length()<30) {
                                // Description (if exist) is in the position 1 of the output array getLabel
                                            ss += " (" + labS.get(1) + ")";
                                        }else {
                                            ss+= " ("+ labS.get(1).substring(0, 27)+"...)";
                                        }
                                    }
                                    strSubject.add(ss);
                                }else{
                                    strSubject.add("null");
                                }
                                if (triple.getObject().isURI()) {
                                    ArrayList<String> labO = getLabel(replaceProp(object), l, k);
                                    String so = labO.get(0);
                                    if (labO.size() == 2) {
                                        if (labO.get(1).length() < 30) {
                                            so += " (" + labO.get(1) + ")";
                                        } else {
                                            so += " (" + labO.get(1).substring(0, 27) + "...)";
                                        }
                                    }
                                    strObject.add(so);
                                }else{
                                    strObject.add("null");
                                }
                                if (triple.getPredicate().isURI()) {
                                    ArrayList<String> labP = getLabel(replaceProp(predicate), l, k);
                            // for the predicate we don't need description So we put just the label
                                    String sp = labP.get(0);
                                    strPredicate.add(sp);


                                }else if (triple.getPredicate().isVariable()){
                                //    we recupere the name of the variable and we  put it in an array
                                    predicVar.add(triple.getPredicate().toString());
                                    System.out.println("PredictVar ! "+predicVar);
                                    strPredicate.add("null");
                                    contVarPredic+=1;
                                }


                            } else {
                                System.out.println("it's not a triple");
                                contNotTriple=true;
                            }
                        }
                        for (int i=strPredicate.size()-1 ; i>=0 ; i--) {
                            // in case of it's a Select type, not aggregators yet and all the contents of the query are triples, and there is not a predicate variable
                            if (q.isSelectType() && !q.hasAggregators() && contNotTriple == false && contVarPredic == 0 ) {
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
                            else if (q.isSelectType() && !q.hasAggregators() && contNotTriple == false && contVarPredic == 1 && strPredicate.size() == 2) {
                                if (strPredicate.size() != 0 && strPredicate.get(i)!="null") {
                                    result += "/" + strPredicate.get(i) ;
                                }
                                if (strSubject.size() != 0 && strSubject.get(i)!="null") {
                                    result += "/" + strSubject.get(i);
                                }
                                if (strObject.size() != 0 && strObject.get(i)!="null") {
                                    result += "/" + strObject.get(i);
                                }
                                if (!result.contains("/~")){
                                result+="/~ ";
                                        ArrayList<ArrayList<String>> gAlt = getAlternatives(strq.replaceFirst(rech.get(0), predicVar.get(0).replace("?", "")), predicVar, l, k);
                                        System.out.println("GALT "+gAlt);
                                        for (int j = 0; j < gAlt.size(); j++) {
                                            String labJ = getLabel(replaceProp(gAlt.get(0).get(j)), l, k).get(0);
                                            //  if(labJ.get(0).length()<30)
                                            System.out.println("Label "+labJ);
                                            result += labJ + ",  ";
                                        }
                                        result += " ~";

                                }

                                // case of both of 2 predicates are variables
                            } else if (q.isSelectType() && !q.hasAggregators() && contNotTriple == false && contVarPredic == 2 && strPredicate.size() == 2) {
                                String strqAlt=strq.replaceFirst(rech.get(0), predicVar.get(0).replace("?","")+" ?"+predicVar.get(1).replace("?",""));
                                System.out.println("STRQALT: "+strqAlt);
                                result+="//";

                                    ArrayList<ArrayList<String>> gAlt = getAlternatives(strqAlt,predicVar, l, k);
                                ArrayList<String> labJ = new ArrayList<>();
                             //   for (int z=0; z < gAlt.size(); z++) {
                                    for (int j = 0; j < gAlt.get(i).size(); j++) {
                                        labJ = getLabel(replaceProp(gAlt.get(i).get(j)), l, k);
//                                        if(labJ.get(0).length()<30)
                                        result += labJ.get(0) + ", ";
                                    }
                               // }
                                if (strPredicate.size() != 0 && strPredicate.get(i)!="null") {
                                    result += "/" + strPredicate.get(i) ;
                                }
                                if (strSubject.size() != 0 && strSubject.get(i)!="null") {
                                    result += "/" + strSubject.get(i);
                                }
                                if (strObject.size() != 0 && strObject.get(i)!="null") {
                                    result += "/" + strObject.get(i);
                                }
                                result.replaceFirst("//", "");
                                result.replaceFirst("/", "");
                                result.replaceAll(", /", " /");


                                }

                            else{
                                result = "";
                            }
                        }

                        }

                    // Values Types
                    @Override
                    public void visit(ElementData el) {

                        StringUtils util = new StringUtils();
                        String ur = el.getRows().get(0).get(el.getVars().get(0)).toString();
                        ArrayList<String> lab=getLabel(replaceProp(ur), l, k);
                        result = lab.get(0);
                        if (lab.size() == 2) {
                            if (lab.get(1).length() > 30) {
                                result += " (" + lab.get(1).substring(0, 30) + "...)";
                            }else {
                                result += " (" + lab.get(1) + ")";

                            }

                        }

                    }
                });
        // clean the result
        if (result != null) {
            result.replaceAll(", ~"," ~");
            result = result.replaceAll("/null", "");
            result = result.replaceAll("/null/", "/");
            result = result.replaceAll("null/", "");
            result = result.replaceAll("/instance of/", "/");
            result = result.replaceAll("instance of/", "");
        }
        return result;
    }
    // we put both of the label and the description of the uri input in an array
    public ArrayList<String> getLabel(String s, String l, String k) {
        System.out.println("Heyoo I AM IN getLabel again !!!=====================>>");
        ArrayList<String> lab = new ArrayList<>();
        String la = "\"" + l + "\"";
        String res
                = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " PREFIX schema: <http://schema.org/> "
                + " SELECT DISTINCT ?o ?x WHERE { "
                + " <" + s + "> rdfs:label ?o . "
                + "  OPTIONAL { <" + s + ">  schema:description ?x FILTER( lang(?x)=" + la + " )} . "
                + "  FILTER( lang(?o)=" + la + " )"
                + "} limit 20 ";
        if (s.contains("wikidata")) {
            k = "https://query.wikidata.org/sparql";
            Query query1 = QueryFactory.create(res);
            QueryExecution qExe = QueryExecutionFactory.sparqlService(k, query1);
            ResultSet result;
            result = qExe.execSelect();
            ;
            while (result.hasNext()) {
                QuerySolution rsnext = result.next();
                lab.add(rsnext.getLiteral("o").getLexicalForm().toString());
                System.out.println(rsnext.getLiteral("o").getLexicalForm().toString());
                if (rsnext.getLiteral("x") != null) {
                    lab.add(rsnext.getLiteral("x").getLexicalForm().toString());
                }
            }
        } else if (s.contains("dbpedia")) {
            k = "http://dbpedia.org/sparql";
            Query query1 = QueryFactory.create(res);
            QueryExecution qExe = QueryExecutionFactory.sparqlService(k, query1);
            ResultSet result;
            result = qExe.execSelect();
            ;
            while (result.hasNext()) {
                QuerySolution rsnext2 = result.next();
                lab.add(rsnext2.getLiteral("o").getLexicalForm().toString());
            }
        } else {
            lab.add("categorie");
            System.out.println("---------------not dbpedia && not wikidata");
        }
        return lab;
    }
    // in case of predicate variable, we recupere all possibles predicates for one query
    public ArrayList<ArrayList<String>> getAlternatives (String res,List<String> p, String l,String k){

            ArrayList<ArrayList<String>> altern = new ArrayList<>();
            String la = "\"" + l + "\"";

        if (res.contains("wikidata"))
            k = "https://query.wikidata.org/sparql";
        else if (res.contains("dbpedia"))
            k="http://dbpedia.org/sparql";

        System.out.println("ALTERNATIVE QUERY ... :"+res);
        System.out.println("P SIZE ... :"+p.size());
//////////////////////////////////////////////////////////////////////////////
        for (int i=0; i<p.size(); i++) {
            ArrayList<String> petitAltern = new ArrayList<>();
            Query query1 = QueryFactory.create(res);
            QueryExecution qExe = QueryExecutionFactory.sparqlService(k, query1);
            ResultSet result;
            result = qExe.execSelect();
            while (result.hasNext()) {
                QuerySolution rsnext = result.next();//
                String s = p.get(i).replace("?","").toString();
                System.out.println("Ressources---"+i+"-- " + s);
                String nxt = rsnext.getResource(s).toString();
                petitAltern.add(nxt);
                System.out.println("petitAltern  "+ petitAltern);
            }
            altern.add(petitAltern);

        }
        System.out.println("ALTERN "+altern);
        return altern;
        }

    public String replaceProp (String str){

        return str.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity");
    }

}
