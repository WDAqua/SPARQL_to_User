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
    public SPARQLToUser sparqlToUser(@RequestParam(value="sparql", defaultValue = "SELECT DISTINCT ?x WHERE { <http://dbpedia.org/resource/Eritrea> <http://dbpedia.org/ontology/leader> ?x } limit 1000") String sparql,
                                     @RequestParam(value="lang", defaultValue = "en") String lang,
                                     @RequestParam(value="kb", defaultValue = "dbpedia") String kb,
                                     @RequestParam(value="endpoint", defaultValue = "https://dbpedia.org/sparq") String endpoint) {
            if (kb.contains("wikidata")){
                endpoint = "https://query.wikidata.org/sparql";
            } else if (kb.contains("dbpedia")){
                endpoint = "https://dbpedia.org/sparql";
            } else if (kb.contains("musicbrainz")){
                endpoint = "http://wdaqua.univ-st-etienne.fr/hdt-endpoint/musicbrainz/sparql";
            }  else if (kb.contains("dblp")){
                endpoint = "http://wdaqua.univ-st-etienne.fr/hdt-endpoint/dblp/sparql";
            } else {
                logger.info("The endpoint is not wikidata nor dbpedia neither musicbrainz");
            }
        logger.info("ENDPOINT: ",endpoint);

        logger.info("Request sparql: {}, lang: {}, kb: {}",sparql,lang,kb,endpoint);
            SPARQLToUser s = new SPARQLToUser(sparql, lang, kb, endpoint);
            logger.info("Result {}",s.getInterpretation());
            return s;

    }
}
