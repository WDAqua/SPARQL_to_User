package eu.wdaqua.SPARQLtoUser;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SparqlController {

   @RequestMapping("/sparqlToUser")
    public SPARQLtoUser sparqlToUser(@RequestParam(defaultValue = "SELECT DISTINCT ?uri WHERE { ?uri <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q4830453>  . ?uri <http://www.wikidata.org/prop/direct/P17> <http://www.wikidata.org/entity/Q668> . ?uri <http://www.wikidata.org/prop/direct/P1128> ?employees . } ORDER BY DESC(?employees) LIMIT 1") String sparql 
                          , @RequestParam(defaultValue = "en") String lang
                          , @RequestParam(defaultValue = "wikidata.org") String kb) {
   
    return new SPARQLtoUser( sparql,  lang, kb);        
                     
    }
}
