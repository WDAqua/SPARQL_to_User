package eu.wdaqua.SPARQLtoUser;

import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SparqlController {

     //Set this to allow browser requests from other websites
    @ModelAttribute
    public void setVaryResponseHeader(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
    }
    @RequestMapping("/sparqltouser")
    public SPARQLtoUser sparqlToUser(@RequestParam(value="sparql", defaultValue = "SELECT ?x where { VALUES ?x { <http://www.wikidata.org/entity/Q76> } }") String sparql,
            @RequestParam(value="lang", defaultValue = "en") String lang,
            @RequestParam(value="kb", defaultValue = "wikidata.org") String kb) {
            return new SPARQLtoUser(sparql, lang, kb);

    }
}
