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
    public SPARQLToUser sparqlToUser(@RequestParam(value="sparql", defaultValue = "ASK WHERE { ?x <http://www.wikidata.org/prop/direct/P6> <http://www.wikidata.org/entity/Q76> . }") String sparql,
                                     @RequestParam(value="lang", defaultValue = "en") String lang,
                                     @RequestParam(value="kb", defaultValue = "wikidata") String kb,
                                     @RequestParam(value="endpoint", defaultValue = "https://query.wikidata.org/sparql") String endpoint) {
            if (kb.contains("wikidata")){
                endpoint = "https://query.wikidata.org/sparql";
            }else if (kb.contains("dbpedia")){
                endpoint = "https://dbpedia.org/sparql";
            }else {
                logger.info("The endpoint is not existing");
            }
            logger.info("Request sparql: {}, lang: {}, kb: {}",sparql,lang,kb,endpoint);
            SPARQLToUser s = new SPARQLToUser(sparql, lang, kb, endpoint);
            logger.info("Result {}",s.getInterpretation());
            return s;

    }
}
