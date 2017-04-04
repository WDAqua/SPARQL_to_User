

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author youssef
 */

import com.mycompany.sntl.SNTL;
import java.io.FileNotFoundException;
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
   public void parse() throws FileNotFoundException, IOException, ParseException {
          
          
      JSONParser parser = new JSONParser();
 
      
          Object obj = parser.parse(new FileReader("C:/Users/youssef/Documents/NetBeansProjects/sntl/src/main/resources/wikidata-train-7.json")); 
          JSONObject dataset = (JSONObject) obj;
      JSONArray questions = (JSONArray) dataset.get("questions"); 
        Iterator<JSONObject> iterator = questions.iterator();
     
         while (iterator.hasNext()) {
                      JSONObject next=iterator.next();
                              
            //if (count==21){
            //System.out.println("Number "+count);
            JSONArray languages = (JSONArray) next.get("question");
            Iterator<JSONObject> langs  = languages.iterator();
            while (langs.hasNext()){
               JSONObject lang = langs.next();
                                        String language = "en";
                                        String type = "string";
                                        String spar="sparql";
               if (lang.get("language").equals(language)){
                   System.out.println("Question:"+lang.get("string").toString());
                  String expectedQuery="";
                  JSONObject query=(JSONObject)next.get("query");
                  if (query.containsKey("sparql")){
                     expectedQuery=query.get("sparql").toString();
                      System.out.println(expectedQuery);
                      SNTL s= new SNTL();
                      String reponse = s.sntl(expectedQuery);
                     
                  }
            }
   }
         }}}


