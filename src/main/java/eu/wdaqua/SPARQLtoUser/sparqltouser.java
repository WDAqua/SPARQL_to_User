package eu.wdaqua.SPARQLtoUser;

import org.apache.jena.graph.Node;
import org.apache.jena.query.*;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.apache.jena.sparql.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 *
 * @author youssef
 */
public class sparqltouser {


    private  String sparql;
    private  String lang;
    private  String kb;
    private  String s;

    String subject;
    String predicate;
    String object;
    String result;
    String strSubject = null;
    String strPredicate = null;
    String strObject = null;

    public sparqltouser() {

    }



    public sparqltouser(String sparql, String lang, String kb){
        this.sparql=sparql;
        this.lang=lang;
        this.kb=kb;
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
        } catch (QueryParseException e){
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
                                        subject = triple.getSubject().toString();
                                        predicate = triple.getPredicate().toString();
                                        object = triple.getObject().toString();
                                        System.out.println("S-"+subject);
                                        System.out.println("P-"+predicate);
                                        System.out.println("O-"+object);
                                        if(triple.getSubject().isURI()) {
                                            strSubject= getLabel(subject.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k).get(0);
                                        }
                                        if(triple.getObject().isURI()) {
                                            strObject=getLabel(object.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k).get(0);
                                        }
                                        if(triple.getPredicate().isURI()) {
                                            strPredicate = getLabel(predicate.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k).get(0);
                                        }
                                } else {
                                        System.out.println("it's not a triple");
                                }

                            }
                            if (q.isSelectType() && !q.hasAggregators() && strPredicate!=""){

                                if (strPredicate!="") {
                                    result = strPredicate;
                                }
                                if (strSubject!=""){
                                    result += "/"+strSubject;
                                }
                                if (strObject!="") {
                                    result += "/" + strObject;
                                }
                            }else{
                                result="";
                            }
                        }
                        @Override
                        public void visit(ElementData el) {

                            StringUtils util = new StringUtils();
                            String ur=el.getRows().get(0).get(el.getVars().get(0)).toString();
                            System.out.println("This is a VALUES QUERY..."+el.getRows().get(0).get(el.getVars().get(0)).toString());
                            result=getLabel(ur.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"),l,k).get(0);
                            result+="( "+getLabel(ur.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"),l,k).get(1)+" )";

//                          feature.put("linking",1.0);
//                          int i=m.getIndex(el.getRows().get(0).get(el.getVars().get(0)).toString());
//                          int sim=util.getLevenshteinDistance(m.getText(i).toLowerCase(),m.getLex(i).toLowerCase());
//                          feature.put("r"+6+"-sim",(double)sim);
//                          feature.put("r"+6+"-rel",(double)m.getRel(i));
//                          feature.put("r"+7+"-type",m.getType(i));


                        }
                    });

      if (result!=null){
            result = result.replaceAll("/null", "");
            result = result.replaceAll("/null/", "/");
            result = result.replaceAll("null/", "");
            result = result.replaceAll("/instance of/", "/");
            result = result.replaceAll("instance of/", "/");
      }
         return result;
    }
    public ArrayList <String> getLabel(String s, String l, String k) {
        ArrayList <String> lab= new ArrayList<>();
        String la="\""+l+"\"";
        //k="http://dbpedia.org/sparql";
        //k="https://query.wikidata.org/sparql";
        String res
                    = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                        " PREFIX schema: <http://schema.org/> "
                + " SELECT DISTINCT ?o ?x WHERE { "
                + " <" + s + "> rdfs:label ?o . "
                + " <" + s + "> schema:description ?x . "
                + "  FILTER( lang(?o)="+la+" && lang(?x)="+la+" )"
                + "} limit 20 ";

        System.out.println(res);
        if (k.contains("wikidata")){
         k="https://query.wikidata.org/sparql";
                Query query1 = QueryFactory.create(res);
                QueryExecution qExe = QueryExecutionFactory.sparqlService(k, query1);
                ResultSet result;
                result = qExe.execSelect();


            ;
        while (result.hasNext()) {

            QuerySolution rsnext = result.next();
            System.out.println(rsnext.getLiteral("o").getLexicalForm().toString());
            lab.add(rsnext.getLiteral("o").getLexicalForm().toString());
            lab.add(rsnext.getLiteral("x").getLexicalForm().toString());

        }
        }
        else if(k.contains("dbpedia"))
        {
            k="http://dbpedia.org/sparql";
            Query query1 = QueryFactory.create(res);
            QueryExecution qExe = QueryExecutionFactory.sparqlService(k, query1);
            ResultSet result;
            result = qExe.execSelect();
            ;
            while (result.hasNext()) {
//            lab.add( result.next().getLiteral("o").getLexicalForm().toString());
//            lab.add( result.next().getLiteral("x").getLexicalForm().toString());
            }
        }else{
            lab = null;
            System.out.println("---------------not dbpedia && not wikidata");
        }
            return lab;
    }


}
