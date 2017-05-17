package eu.wdaqua.SPARQLtoUser;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {
    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

     //Set this to allow browser requests from other websites
    @ModelAttribute
    public void setVaryResponseHeader(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
    }
    @RequestMapping("/sparqltouser")
    public SPARQLToUser sparqlToUser(@RequestParam(value="sparql", defaultValue = "SELECT DISTINCT ?x WHERE { ?x ?p1 <http://www.wikidata.org/entity/Q174782> . ?x ?p2 <http://www.wikidata.org/entity/Q495> . } limit 1000") String sparql,
                                     @RequestParam(value="lang", defaultValue = "en") String lang,
                                     @RequestParam(value="kb", defaultValue = "wikidata.org") String kb) {
            logger.info("Request sparql: {}, lang: {}, kb: {}",sparql,lang,kb);
            SPARQLToUser s = new SPARQLToUser(sparql, lang, kb);
            logger.info("Result {}",s.getInterpretation());
            return s;

    }
}