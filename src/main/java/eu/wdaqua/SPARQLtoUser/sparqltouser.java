    package eu.wdaqua.SPARQLtoUser;

    import org.apache.jena.graph.Node;
    import org.apache.jena.query.*;
    import org.apache.jena.sparql.core.TriplePath;
    import org.apache.jena.sparql.expr.Expr;
    import org.apache.jena.sparql.syntax.*;
    import org.apache.jena.sparql.util.StringUtils;

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
    boolean contNotTriple= false;
    boolean contVarPredic=false;
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
                                    ArrayList<String> labS = getLabel(subject.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k);

                                    String ss = labS.get(0);
                                    if (labS.size() == 2) {
                                        if (labS.get(1).length()<30) {

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
                                    ArrayList<String> labO = getLabel(object.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k);
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
                                    ArrayList<String> labP = getLabel(predicate.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k);

                                    String sp = labP.get(0);
                                    strPredicate.add(sp);

                                }else if (triple.getPredicate().isVariable()){
                                    strPredicate.add("null");
                                    contVarPredic=true;
                                }


                            } else {
                                System.out.println("it's not a triple");
                                contNotTriple=true;
                            }
                        }
                        for (int i=strPredicate.size()-1 ; i>=0 ; i--) {
                            if (q.isSelectType() && !q.hasAggregators() && contNotTriple == false && contVarPredic == false ) {
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
                            else if (q.isSelectType() && !q.hasAggregators() && contNotTriple == false && contVarPredic == true && strPredicate.size() != 1) {
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
                                ArrayList<String>gAlt = getAlternatives(subject ,predicate, l, k);
                                for (int j=0 ; j<gAlt.size(); j++) {
                                    ArrayList<String> labJ = getLabel(gAlt.get(j).replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"),l ,k);
                                    if(labJ.get(0).length()<30)
                                    result +=labJ.get(0)+ ",  ";
                                }
                                result +="... ~";
                                }

                            }else{
                                result = "";
                            }
                        }

                        }


                    @Override
                    public void visit(ElementData el) {

                        StringUtils util = new StringUtils();
                        String ur = el.getRows().get(0).get(el.getVars().get(0)).toString();
                        ArrayList<String> lab=getLabel(ur.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k);
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

        if (result != null) {
            result = result.replaceAll("/null", "");
            result = result.replaceAll("/null/", "/");
            result = result.replaceAll("null/", "");
            result = result.replaceAll("/instance of/", "/");
            result = result.replaceAll("instance of/", "");
        }
        return result;
    }
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
            lab .add("null");
            System.out.println("---------------not dbpedia && not wikidata");
        }
        return lab;
    }
    ArrayList<String> getAlternatives (String s, String p, String l,String k){
        ArrayList<String> altern = new ArrayList<>();
        String la = "\"" + l + "\"";
        String res
                = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "PREFIX wdt: <http://www.wikidata.org/prop/direct/> "
                + " SELECT DISTINCT ?x WHERE { "
                + " <" + s + "> ?x ?o . "
                + " ?o <" + p + ">  ?y . "
                + "} limit 20 ";
        k = "https://query.wikidata.org/sparql";
        Query query1 = QueryFactory.create(res);
        QueryExecution qExe = QueryExecutionFactory.sparqlService(k, query1);
        ResultSet result;
        result = qExe.execSelect();
        ;
        while (result.hasNext()) {
            QuerySolution rsnext = result.next();
            altern.add(rsnext.getResource("x").toString());
        }
        return altern;
    }

}
