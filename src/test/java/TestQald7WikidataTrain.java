/**
 *
 * @author youssef
 */

import eu.wdaqua.SPARQLtoUser.SPARQLToUser;
import org.junit.Test;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import org.json.simple.parser.ParseException;



public class TestQald7WikidataTrain {
   @Test
   public void parse() throws IOException, ParseException {
       JSONParser parser = new JSONParser();

       Object obj = parser.parse(new FileReader("src/main/resources/wikidata-train-7.json"));
       JSONObject dataset = (JSONObject) obj;
       JSONArray questions = (JSONArray) dataset.get("questions");

       Iterator<JSONObject> iterator = questions.iterator();
     
        while (iterator.hasNext()) {
            JSONObject next=iterator.next();
            JSONArray languages = (JSONArray) next.get("question");
            Iterator<JSONObject> langs  = languages.iterator();
            while (langs.hasNext()){
                JSONObject lang = langs.next();
                String language = "en";
                String type = "string";
                String spar="sparql";
                String knowledge="wikidata.org";
                if (lang.get("language").equals(language)){
                    System.out.println("Question:"+lang.get("string").toString());
                    String expectedQuery="";
                    JSONObject query=(JSONObject)next.get("query");
                    if (query.containsKey("sparql")){
                        expectedQuery=query.get("sparql").toString();
//                        System.out.println(expectedQuery);
                        
                        SPARQLToUser s = new SPARQLToUser();
                        
                        String reponse = s.go(expectedQuery, language, knowledge);
                        System.out.println(reponse);
                  }
                }
            }
        }
   }
}


