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
    public SPARQLtoUser sparqlToUser(@RequestParam(value="sparql", defaultValue = "SELECT DISTINCT ?x WHERE { ?x <http://www.wikidata.org/prop/direct/P26> <http://www.wikidata.org/entity/Q76> . } limit 1000") String sparql,
            @RequestParam(value="lang", defaultValue = "en") String lang,
            @RequestParam(value="kb", defaultValue = "wikidata.org") String kb) {
            System.out.println("Request "+sparql);
            return new SPARQLtoUser(sparql, lang, kb);

    }
}