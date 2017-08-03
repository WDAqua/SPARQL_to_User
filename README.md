## SPARQLtoUser

### Intro
SPARQLtoUser is a web service that translates a SPARQL query into a representation that is thought for end users. For example the SPARQL query over wikidata:

    SELECT DISTINCT ?x   
    WHERE {   
      <http://www.wikidata.org/entity/Q20034> <http://www.wikidata.org/prop/direct/P527> ?x .  
    } limit 1000  

is transformed into:

    has part / lasagne (flat rectangle-shaped pasta...) 

Or the query 

    SELECT DISTINCT ?x  
    WHERE {  
      ?x ?p1 <http://www.wikidata.org/entity/Q38222> .  
      ?x ?p2 <http://www.wikidata.org/entity/Q11424> .  
    } limit 1000 

is transormed into:

    characters, executive producer, screenwriter, director, producer / George Lucas (American film producer) 
    instance of / film (sequence of images that giv...) 

i.e. the properties are expanded.


## To start
Tu run the package do:

    mvn clean package
    java -jar target/SparqlToUser-0.1.jar

The service will then be available under localhost:1920/sparqltouser. Or check out our online available webservice at:
https://wdaqua-sparqltouser.univ-st-etienne.fr/sparqltouser

## API
GET or POST with parameters:
- sparql : the SPARQL query you want to translate
- lang : the language you would like to translate
- kb: the kb for which the SPARQL is written (dbpedia, wikidata, musicbrainz and dblp are supported)

example: 

    curl --data-urlencode "sparql=SELECT DISTINCT ?x WHERE {   <http://www.wikidata.org/entity/Q20034> <http://www.wikidata.org/prop/direct/P527> ?x . } limit 1000" --data-urlencode "lang=en" --data-urlencode "kb=wikidata" https://wdaqua-sparqltouser.univ-st-etienne.fr/sparqltouser

returns:

    {"sparql":"SELECT DISTINCT ?x WHERE {   <http://www.wikidata.org/entity/Q20034> <http://www.wikidata.org/prop/direct/P527> ?x . } limit 1000","lang":"en","kb":"wikidata","interpretation":"has part / lasagne (flat rectangle-shaped pasta...) "}

## Dig into the code
The code is entirely written in Java. We use mainly the Jena Apache Library (https://jena.apache.org/documentation/query/) and Spring (https://spring.io/guides/gs/rest-service/)

---
Made with ♥ by Youssef Dridi and Dennis Diefenbach (www.wdaqua.eu)

