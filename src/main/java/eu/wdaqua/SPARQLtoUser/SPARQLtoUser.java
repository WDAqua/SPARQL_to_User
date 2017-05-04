package eu.wdaqua.SPARQLtoUser;

import ch.qos.logback.core.util.AggregationType;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Predicate;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.SortCondition;
import org.apache.jena.query.Syntax;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpLib;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.apache.jena.sparql.util.StringUtils;



/**
 *
 * @author youssef
 */
public class SPARQLtoUser {

    
    private  String sparql;
    private  String lang;
    private  String kb;
    private  String s;
  
    String subject;
    String predicate;
    String object;
    String result;
    boolean isOneTrip;
    boolean isConform;

    public SPARQLtoUser() {
        
    }
    
    

    public SPARQLtoUser(String sparql, String lang, String kb){
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

/******************************************************************************************/

        if (!q.hasAggregators() && q.isSelectType()){
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

                                    subject=triple.getSubject().toString();
                                    predicate=triple.getPredicate().toString();
                                    object = triple.getObject().toString();
                                    String strSubject;
                                    String strPredicate;
                                    String strObject;
                                    strSubject = getLabel(subject.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k);
                                    strObject = getLabel(object.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k);
                                    strPredicate = getLabel(predicate.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k);
                                    result= strSubject+"/"+strPredicate+"/"+strObject;
                                                                   } else {
                                    result="";
                                    System.out.println("it's not a triple");
                                }
                            }
                        }
                        @Override
                        public void visit(ElementData el) {
                            StringUtils util = new StringUtils();

                        }
                    });

        }
       else{

    /***********************************************************************************/



      if (!q.hasAggregators()){
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
                      
                                subject=triple.getSubject().toString();
                                predicate=triple.getPredicate().toString();
                                object = triple.getObject().toString();
                                String strSubject;
                                String strPredicate;
                                String strObject;
                            strSubject = getLabel(subject.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k);
                            strObject = getLabel(object.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k);
                            strPredicate = getLabel(predicate.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k);
                                if (q.hasOrderBy()){
                                    boolean isOrdDesc = q.getOrderBy().get(0).toString().contains("DESC");
                                    if (isOrdDesc){
                                    if (result ==null){
                                    result = "the most //"+strPredicate+"/"+strObject+"/"+strSubject;
                                    }else{
                                    result =result+"/"+strPredicate+"/"+strObject+"/"+strSubject;
                                    }
                                    }
                                }
                                 if (!q.hasOrderBy()){
                                    if (triple.getPredicate().isURI()){         
                                    result = result+"/"+strPredicate;
                                    }
                                    if (triple.getSubject().isURI()){
                                    result =result+"/"+ strSubject;
                                    }
                                    if (triple.getObject().isURI()){
                                    result = result+"/"+strObject;
                                    }
                                
                                    
                                 }
                        } else {
                            result="";
                            System.out.println("it's not a triple");
                        }
                    }
                }
                @Override
                public void visit(ElementData el) {
                    StringUtils util = new StringUtils();

                }
            });
        
   if (q.isAskType()){
        result = "check("+result+")";
      } 
     }
      if (q.hasAggregators()){
       
       ElementWalker.walk(q.getQueryPattern(),
                // For each element...
                new ElementVisitorBase() {
                // ...when it's a block of triples...
                @Override
                public void visit(ElementPathBlock el) {
                    // ...go through all the triples...

                    ListIterator<TriplePath> triples = el.getPattern().iterator();
                    ArrayList<Node> nodes = new ArrayList<Node>();
                    int i=1;
                    while (triples.hasNext()) {
                        // ...and grab the subject
                        TriplePath triple = triples.next();
                        if (triple.isTriple()) {
                            System.out.println("it's triple !!!"+i);
                                subject=triple.getSubject().toString();
                                predicate=triple.getPredicate().toString();
                                object = triple.getObject().toString();
                                String strSubject;
                                String strPredicate;
                                String strObject;
                            strSubject = getLabel(subject.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k);
                            strObject = getLabel(object.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k);
                            strPredicate = getLabel(predicate.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k);
                                boolean  subjCount= q.getAggregators().get(0).toString().contains(triple.getSubject().toString());
                                boolean  predictCount= q.getAggregators().get(0).toString().contains(triple.getPredicate().toString());
                                boolean  objCount= q.getAggregators().get(0).toString().contains(triple.getObject().toString());
                                boolean  isSubjectQuest = triple.getSubject().toString().contains("?uri");
                                boolean  isObjectQuest = triple.getObject().toString().contains("?uri");
                                long limit= q.getLimit();
                                String strngz = q.getAggregators().get(0).getVar().toString();
                        if (q.hasOrderBy()){
                                boolean isOrdDesc = q.getOrderBy().get(0).toString().contains("DESC");
                                boolean isOrdAsc = q.getOrderBy().get(0).toString().contains("ASC");
                              
                            if (isOrdDesc){
                                if (result ==null){
                                result = "the most //"+strPredicate+"/"+strObject+"/"+strSubject;
                                }else{
                                result =result+"/"+strPredicate+"/"+strObject+"/"+strSubject;
                            }
                            }else if (isOrdAsc) {
                                 if (result ==null){
                                result = "the least //"+strPredicate+"/"+strObject+"/"+strSubject;
                                }else{
                                result =result+"/"+strPredicate+"/"+strObject+"/"+strSubject;
                            }
                            }
                          i++;    
                        } 
                        if (!q.hasOrderBy()) {
                                if (result ==null){
                                result = "how many //"+strObject+"/"+strPredicate+"/"+strSubject;
                                 }else{
                                result = result+"/"+strObject+"/"+strPredicate+"/"+strSubject; 
                                }
                        }
                        if (q.hasHaving()){
                                List<Expr> b = q.getHavingExprs();
                                Expr br=b.get(0).getFunction();
                                
                        }
                        }else{
                            result="";
                                System.out.println("it'not a triple !!!");
                        }
                    }
                }    

                @Override
                public void visit(ElementData el) {
                        StringUtils util = new StringUtils();
//                        feature.put("linking",1.0);
//                        int i=m.getIndex(el.getRows().get(0).get(el.getVars().get(0)).toString());
//                        int sim=util.getLevenshteinDistance(m.getText(i).toLowerCase(),m.getLex(i).toLowerCase());
//                        feature.put("r"+6+"-sim",(double)sim);
//                        feature.put("r"+6+"-rel",(double)m.getRel(i));
//                        feature.put("r"+7+"-type",m.getType(i));

                }
            });
      
    }
    result=null;
        }
      if (result!=null){
            result = result.replaceAll("/null", "");
            result = result.replaceAll("/null/", "/");
            result = result.replaceAll("null/", "");
            result = result.replaceAll("/instance of/", "/");
            result = result.replaceAll("instance of/", "/");
          System.out.println("SELECT ?x WHERE { VALUES ?x { <http://www.wikidata.org/entity/Q14169302> }}");

      }
         return result;
    }
    public String getLabel(String s, String l, String k) {
        String lab = null;
        String la="\""+l+"\"";
        //k="http://dbpedia.org/sparql";
        //k="https://query.wikidata.org/sparql";
        String res
                = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + " SELECT DISTINCT ?o WHERE { "
                + " <" + s + "> rdfs:label ?o ."
                + "  FILTER( lang(?o)="+la+" )"
                + "} limit 20 ";

        //       System.out.println(res);
        if (k.contains("wikidata")){
         k="https://query.wikidata.org/sparql";
        Query query1 = QueryFactory.create(res);
        QueryExecution qExe = QueryExecutionFactory.sparqlService(k, query1);
        ResultSet result;
        result = qExe.execSelect();
        ;
        while (result.hasNext()) {
            lab = result.next().getLiteral("o").getLexicalForm().toString();

        }
        }
        if(k.contains("dbpedia"))
        {
            k="http://dbpedia.org/sparql";
            Query query1 = QueryFactory.create(res);
            QueryExecution qExe = QueryExecutionFactory.sparqlService(k, query1);
            ResultSet result;
            result = qExe.execSelect();
            ;
            while (result.hasNext()) {
                lab = result.next().getLiteral("o").getLexicalForm().toString();

            }
        }if (!k.contains("wikidata") && !k.contains("dbpedia")) {
            lab = "";
            System.out.println("---------------not dbpedia && not wikidata");
        }
            return lab;
    }
    public boolean isThereOk(Query query) {

        boolean res;
        boolean isSel = query.isSelectType();
        boolean hasAggre = !(query.hasAggregators());

        ElementWalker.walk(query.getQueryPattern(),
                // For each element...
                new ElementVisitorBase() {
            // ...when it's a block of triples...
            @Override
            public void visit(ElementPathBlock el) {
                // ...go through all the triples...
                ListIterator<TriplePath> triples = el.getPattern().iterator();
                ArrayList<Node> nodes = new ArrayList<Node>();
                int c = 0;
                while (triples.hasNext()) {
                    // ...and grab the subject
                    TriplePath triple = triples.next();
                    if (triple.isTriple()) {

                        if (((triple.getSubject().isVariable()) && (triple.getPredicate().isURI()) && (triple.getObject().isURI())) || ((triple.getSubject().isURI()) && (triple.getPredicate().isURI()) && (triple.getObject().isVariable()))) {
                            isConform = true;

                        } else {
                            isConform = false;
                          }

                        c++;
                    } 
                  
                }
                if (c == 1) {
                    isOneTrip = true;
                } else {
                    isOneTrip = false;
                }
            }

            @Override
            public void visit(ElementData el) {
                StringUtils util = new StringUtils();

            }
        });

        return (isSel) && (hasAggre);// && (isConform)&& (isOneTrip);
    }

}
