package eu.wdaqua.SPARQLtoUser;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.*;

import java.util.ListIterator;

/**
 * Created by Dennis on 16/05/17.
 */
//Parses the query and checks if there are elements we cannot deal with like UNIONS. If not the VALUES and the TriplePath is extracted
public class QueryParse {
    private boolean canBeProcessed = true;
    private ListIterator<TriplePath> triples = null;
    private ElementData value = null;
    private Query query = null;


    void parse(String strq) {
        try {
            query = QueryFactory.create(strq, Syntax.syntaxARQ);
        } catch (QueryParseException e) {
            System.out.println(e);
        }
        ElementWalker.walk(query.getQueryPattern(),
                // For each element...
                new ElementVisitorBase() {

                    // ...when it'interpretation a block of triples...
                    @Override
                    public void visit(ElementPathBlock el) {
                        // extract triples
                        triples = el.getPattern().iterator();
                    }

                    // Queries containing a VALUE
                    @Override
                    public void visit(ElementData el) {
                        value = el;
                    }

                    @Override
                    public void visit(ElementAssign el){
                        canBeProcessed = false;
                    }

                    @Override
                    public void	visit(ElementBind el){
                        canBeProcessed = false;
                    }

                    @Override
                    public void	visit(ElementDataset el){
                        canBeProcessed = false;
                    }

                    @Override
                    public void	visit(ElementExists el){
                        canBeProcessed = false;
                    }

                    @Override
                    public void	visit(ElementFilter el){
                        canBeProcessed = false;
                    }

                    @Override
                    public void	visit(ElementGroup el){
                        canBeProcessed = false;
                    }

                    @Override
                    public void	visit(ElementMinus el){
                        canBeProcessed = false;
                    }

                    @Override
                    public void	visit(ElementNamedGraph el){
                        canBeProcessed = false;
                    }

                    @Override
                    public void	visit(ElementNotExists el){
                        canBeProcessed = false;
                    }

                    @Override
                    public void	visit(ElementOptional el){
                        canBeProcessed = false;
                    }

                    @Override
                    public void	visit(ElementService el){
                        canBeProcessed = false;
                    }

                    @Override
                    public void	visit(ElementSubQuery el){
                        canBeProcessed = false;
                    }

                    @Override
                    public void	visit(ElementTriplesBlock el){
                        canBeProcessed = false;
                    }

                    @Override
                    public void	visit(ElementUnion el){
                        canBeProcessed = false;
                    }

                });
    }

    public boolean getCanBeProcessed(){
        return canBeProcessed;
    }

    public ListIterator<TriplePath> getTriples(){
        return triples;
    }

    public ElementData getValue(){
        return value;
    }

    public Query getQuery(){
        return query;
    }

}
