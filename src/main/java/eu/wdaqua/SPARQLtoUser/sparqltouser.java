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
    ArrayList <String> strSubject = new ArrayList<>();
    ArrayList <String> strPredicate = new ArrayList<>();
    ArrayList <String> strObject = new ArrayList<>();

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
                            String ss =getLabel(subject.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k).get(0);
                            if (getLabel(subject , l, k).size()== 2){
                                ss += " ("+ getLabel(subject.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k).get(1)+")";
                            }
                            strSubject.add(ss);
                            }
                            if(triple.getObject().isURI()) {

                            String so =getLabel(object.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k).get(0);
                            if (getLabel(object , l, k).size()== 2){
                                so += " ("+ getLabel(object.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k).get(1)+")";
                            }
                            strObject.add(so);
                            }
                            if(triple.getPredicate().isURI()) {

                            String sp =getLabel(predicate.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k).get(0);
                            if (getLabel(predicate , l, k).size()==2){
                                sp += " ("+ getLabel(predicate.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"), l, k).get(1)+")";
                            }
                            strPredicate.add(sp);
                            }
                            } else {
                        System.out.println("it's not a triple");
                        }

                        }
                        if (q.isSelectType() && !q.hasAggregators() && strPredicate.size()==1){

                            if (strPredicate.size() != 0) {
                                result = strPredicate.get(0) ;
                            }
                            if (strSubject.size() !=0){
                                result += "/"+strSubject.get(0);
                            }
                            if (strObject.size()!=0) {
                                result += "/" + strObject.get(0);
                            }
                        }else{
                            result="";
                        }
                    }
                    @Override
                    public void visit(ElementData el) {

                        StringUtils util = new StringUtils();
                        String ur=el.getRows().get(0).get(el.getVars().get(0)).toString();

                        System.out.println("-----NS------" + el.getRows().get(0).get(el.getVars().get(0)).getNameSpace());
                        System.out.println("-----LN------" + el.getRows().get(0).get(el.getVars().get(0)).getLocalName());

                        System.out.println("This is a VALUES QUERY..."+el.getRows().get(0).get(el.getVars().get(0)).toString());
                        result=getLabel(ur.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"),l,k).get(0);
                        if (getLabel(ur,l,k).size()==2){
                            result+=" ("+getLabel(ur.replaceAll("prop/direct", "entity").replaceAll("prop/qualifier", "entity"),l,k).get(1)+")";
                        }

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
            String res
            = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
            + " PREFIX schema: <http://schema.org/> "
            + " SELECT DISTINCT ?o ?x WHERE { "
            + " <" + s + "> rdfs:label ?o . "
            + "  OPTIONAL { <" + s + ">  schema:description ?x FILTER( lang(?x)="+la+" )} . "
            + "  FILTER( lang(?o)="+la+" )"
            + "} limit 20 ";
            System.out.println(res);
        if (s.contains("wikidata")){
            k="https://query.wikidata.org/sparql";
            Query query1 = QueryFactory.create(res);
            QueryExecution qExe = QueryExecutionFactory.sparqlService(k, query1);
            ResultSet result;
            result = qExe.execSelect();
        ;
        while (result.hasNext()) {
            QuerySolution rsnext = result.next();
            lab.add(rsnext.getLiteral("o").getLexicalForm().toString());
            System.out.println(rsnext.getLiteral("o").getLexicalForm().toString());
            if (rsnext.getLiteral("x")!=null){
            lab.add(rsnext.getLiteral("x").getLexicalForm().toString());
            }
        }
        }
        else if(s.contains("dbpedia")) {
            k="http://dbpedia.org/sparql";
            System.out.println(res);
            Query query1 = QueryFactory.create(res);
            QueryExecution qExe = QueryExecutionFactory.sparqlService(k, query1);
            ResultSet result;
            result = qExe.execSelect();
            ;
            while (result.hasNext()) {
            QuerySolution rsnext2 = result.next();
            System.out.println("Valeur 0 du Tableau"+rsnext2.getLiteral("o").getLexicalForm().toString());
            lab.add(rsnext2.getLiteral("o").getLexicalForm().toString());
           }
        }else{
            lab = null;
            System.out.println("---------------not dbpedia && not wikidata");
        }
    return lab;
    }
    }
