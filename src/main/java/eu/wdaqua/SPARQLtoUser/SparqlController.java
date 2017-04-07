package eu.wdaqua.SPARQLtoUser;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SparqlController {

   @RequestMapping("/sparqlToUser")
    public SPARQLtoUser sparqlToUser(@RequestParam(defaultValue = "SELECT DISTINCT ?uri WHERE { <http://www.wikidata.org/entity/Q780394> <http://www.wikidata.org/prop/direct/P86> ?uri }") String sparql 
                          , @RequestParam(defaultValue = "en") String lang
                          , @RequestParam(defaultValue = "wikidata.org") String kb) {

      
    return new SPARQLtoUser( sparql,  lang, kb);        
                     
    }
}
