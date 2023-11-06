# Usage

$ make run

browse http://localhost:7000/

---
## TABLES

Create a table:

    > create-table person:name,surname

Create a sql source from table:

    > sql-create person -source=<source.name> 

Create a sql source from changeset:

    > to-changeset
    > sql-exec-changeset

Create a sql source from sql command:

    > set-connection
    > create table person (name:varchar(64),surname:varchar(64)) 

Insert some values:

    > insert into person values ('nicola','pedot')
    > insert into person values ('gino','pedot')
    > insert into person values ('nicola','rossi')

List values ordered:

    > select surname,name from person order by surname


### tables from csv

* upload
* story

### tables from sql script

* upload
* story

## CHANGESET

* A representation of transactional list of DDL operations.


## DATA

### kprime query API json

http://localhost:7000/data/<project>/<trace>/<filename_db.xml>/<table>?
http://localhost:7000/data/h2sakila/root/base/ACTOR?first_name=%27MARY%27

    [
      {
        "actor_id": "66",
        "first_name": "MARY",
        "last_name": "TANDY",
        "last_update": "2006-02-15 04:34:33"
      },
      {
        "actor_id": "198",
        "first_name": "MARY",
        "last_name": "KEITEL",
        "last_update": "2006-02-15 04:34:33"
      }
    ]

### kprime query API jsonld

http://localhost:7000/project/h2sakila/ldata/root/base/ACTOR?first_name=%27MARY%27

    {
      "@context": {
        "ex": "http://example.org/vocab#"
      },
      "@graph": [
        {
          "@id": "tableurl",
          "@type": "tablename",
          "actor_id": "66",
          "first_name": "MARY",
          "last_name": "TANDY",
          "last_update": "2006-02-15 04:34:33"
        },
        {
          "@id": "tableurl",
          "@type": "tablename",
          "actor_id": "198",
          "first_name": "MARY",
          "last_name": "KEITEL",
          "last_update": "2006-02-15 04:34:33"
        }
      ]
    }


### kprime API rdf

https://commons.apache.org/proper/commons-rdf/userguide.html
https://www.w3.org/TR/vocab-data-cube/#slices
https://raw.githubusercontent.com/UKGovLD/publishing-statistical-data/master/specs/src/main/vocab/cube.rdf
https://github.com/opendatasicilia/COVID-19_Sicilia
https://ondata.github.io/guida-api-istat/


### kprime prov jsonld

https://github.com/lucmoreau/ProvToolbox/blob/master/modules-core/prov-jsonld/src/test/java/org/openprovenance/prov/core/PC1FullTest.java

---
## TRACE

### from facts to logical

    add-fact Person eat Food
    add-fact Person has Name
    add-fact Person has Surname
    add-fact Food has Name
    add-fact Sudent is-a Person

    facts
    xml

### pure logical

    add-database movies
    add-functional film:film_id --> replacement_cost, rental_duration, rental_rate
    add-functional address:address_id --> address2
    add-table person:ssn,name,dep_name,dep_add
    add-key person:name,surname


### from source to logical (jdbc:h2:file:~/data/people.db)

    new-h2-source people
    current-source people
    set-source people

    create TABLE Persons (PersonID int, LastName varchar(255), FirstName varchar(255),  Address varchar(255), City varchar(255));

    insert into Persons values(1,'Pedot','Nicola','C.so 4 Novembre','Mezzocorona');
    insert into Persons values(1,'Pedot','Giovanni','C.so 4 Novembre',null);
    
    select column_names FROM table_name WHERE column_name IS NOT NULL;
    
    alter table persons add age int;
    alter table persons add unique(personid);
    alter table persons alter age set not null;
    
    alter table persons add age int default 10;
    alter table persons drop column age;
    
    update persons set age = 17 where firstname = 'Nicola';
    select avg(age) from Persons;
    
    from-source people
    xml
    tables
    3nf


### select from mapping

    current-source transport

    add-mapping trips-routes-calendar from trips(route_id) inner routes and trips(service_id) left calendar

    select trips-routes-calendar where trips.route_id=179 and trips.direction_id='1' and calendar.monday='0'
    => select * from trips-routes-calendar where trips.route_id=179 and trips.direction_id='1' and calendar.monday='0'

    select trips-routes-calendar limit 2


    mappings
    Mappings:
     trips-routes-calendar


    sql-mappings
    ---------
    CREATE OR REPLACE VIEW public.trips-routes-calendar AS
    SELECT *
    FROM   trips
    inner JOIN routes
    ON trips.route_id = routes.route_id
    left JOIN calendar
    ON trips.service_id = calendar.service_id
    WHERE trips.route_id=179 and trips.direction_id='1' and calendar.monday='0'mappings LIMIT 10

### story chart 

Recognized prefixes:

    # title
    + event EventA
    + actor ActorA
    + readmodel ModelA
    + command CommandA
    + policy PolicyA
    + external SignalA
    + source PersonDB
    + go 8 when timeout goto Then
    [title](url)
    [title](url.md)

### terms and vocabularies 

    current-database trace1 opensource_db.xml

    add-term liberties category1 relation1 type1 http://www.schema.org/ It is just a description

    add-vocabulary prefix2 ns2 http://ibm.com/ it is my desc

## jupyter notebooks

$ pip install notebook

$ jupyter notebook

import requests,json
from IPython.core.display import display, HTML

def dictionary(tracedir:str,tracename:str,term:str)->str:
    r = requests.get("http://localhost:7000/dictionary/"+tracedir+"/"+tracename+".xml/"+term)
    display(HTML(r.text))
    
dictionary('demo1','demo1_1_tracedb','table3')

def kp(cmd:str)->str:
    r = requests.post("http://localhost:7000/parse",data={'command':cmd})
    print (r.text.replace('\\n', '\n').replace("\\t", '\t'))
    
kp('all')   

## Lucene

[lucene manual](https://lucene.apache.org/core/9_2_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html)

    >find-text roadmap NOT kotlin
    >find-text kotlin roadmap
    >find-text kotlin +roadmap
    >find-text kotlin AND roadmap
    >find-text "kotlin roadmap"

To search for documents that must contain "jakarta" and may contain "lucene" use the query:

    +jakarta lucene

### Lucene supports fielded data.

When performing a search you can either specify a field, or use the default field. 
The field names and default field is implementation specific.

    title:"The Right Way" AND text:go
    title:"The Right Way" AND go


### Lucene supports Wildcard

    te?t
    tes*

You cannot use a * or ? symbol as the first character of a search. 

### Regular expression

    /[mb]oat/

### Fuzzy searches based on Damerau-Levenshtein Distance. 

To do a fuzzy search use the tilde, "~", symbol at the end of a Single word Term.

    roam~

An additional (optional) parameter can specify the maximum number of edits allowed. The value is between 0 and 2, For example:
The default that is used if the parameter is not given is 2 edit distances.

    roam~1

### Proximity Searches

To do a proximity search use the tilde, "~", symbol at the end of a Phrase. 

For example to search for a "apache" and "jakarta" within 10 words of each other in a document use the search:

    "jakarta apache"~10

### Range Searches

Match documents whose field(s) values are between the lower and upper bound specified by the Range Query. 
Range Queries can be inclusive [] or exclusive {} of the upper and lower bounds. 
Sorting is done lexicographically.

    mod_date:[20020101 TO 20030101]
    title:{Aida TO Carmen}

    >find-text Java filename:[20180821.md TO 20180823.md]

### Booster

    jakarta^4 apache

### Grouping

    (jakarta OR apache) AND website

### Field Grouping

    title:(+return +"pink panther")

### Escaping 

Special Characters

    + - && || ! ( ) { } [ ] ^ " ~ * ? : \ / 

To escape these character use the \ before the character. 

    \(1\+1\)\:2

## RDF

to list all labels in current context:

    >labels

to add one label:

    >add-label rdf:src rdf:pred rdf:obj

to find one label: 

    >find-label rdf:src rdf:pred rdf:obj

to find one label with unknown predicate and object:

    >find-label :xxx _ _

to remove one label:

    >rem-label rdf:src rdf:pred rdf:obj

to select labels:

    >sparql
    >PREFIX  foaf: <http://xmlns.com/foaf/0.1/>
    >SELECT ?friend ?name
    >WHERE { ?friend foaf:name ?name }

to construct labels:

    >sparql
    >PREFIX  foaf: <http://xmlns.com/foaf/0.1/>
    >CONSTRUCT
    >WHERE { ?friend rdf:pred rdf:obj . }

to add one label to a term:

    >add-term :agent a person of the company
    >add-label :agent :address '4_november'
    >add-label :agent :city 'Los_Angeles'

## http client

Given an endpoint:

    https://tourism.api.opendatahub.bz.it/swagger/index.html#/Common/get_v1_SkiArea

>http get http://tourism.api.opendatahub.bz.it/v1/SkiArea?removenullvalues=false

>http get http://tourism.api.opendatahub.bz.it/v1/SkiArea?removenullvalues=false&Language=de



## Mappings

    >add-mapping map1 select * from project; -source=confu

    >mapping -name=map1
    >mapping -name=Actor

    >rem-mapping map1