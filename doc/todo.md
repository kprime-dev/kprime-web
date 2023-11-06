# todo

*  separateArgsOptionals with envelops

       test_separate_optionals_with_envelope

## open:


* http://localhost:7777/noteedit.html?pr=kprime-case-confucius-mysql&tr=___&tf=readme.md
* http://localhost:7000/noteedit.html?pr=kprime-case-confucius-mysql&tr=___&tf=readme.md
* 
* query mapping with parameters (project=132)
* query from path join and where condition a.id=b.id 
* FactParserOrmTest
  * (labels conceptual vs physical view, mapping)
    * employee 
    * cockpit
* chartClassesHtml
* chartColouredClasses
* jsonLD with labels
* verbalization of samples
* schema API

+ unwrap DatabaseMappingUnwrapTest: to simplify queries
  + create a command to unwrap and execute query
  + cockpit example
+ URL to externalKeys browsing
  + fix URL of serving HOST http:/localhost:7000//

## kp emp fixes

* noteedit goals cell
    * fix goal status columns export to md
        - import a csv
        - add facts
            - get orm diag
            - get carm views
            - get json
        - add ontouml
            - get jsonld
            - get ontouml diag

* link goal to stories
    * fix button goal -> story
      ApiBuilder.get("/context/{projectName}/storygoaledit/{goalID}/{traceFileName}", storyController.getProjectStoryEditPage)
      ctx.redirect("/noteedit.html?pr=${projectName.value}&tr=${traceName.value}&goalid=${goalID}&tf=${storyName.value}.md")
    * fix link story -> goal

* cmd add-story (md)
    * create a record Story(gid, filepath) if record not exists
    * create a new file if not exists
        * adds permalink cell if cell not exists else if not equals error.
    * optional template create a new file if not exists else error.
        * put gid cell into MD file to track if needed resync

* cmd sync-stories
    * walk context file tree to check and update story records


-----
* cmd import-csv
    * from story cell
    * from csv file
    * from csv file to h2

-----
* cmd add-facts
  * complete ORM notation test 


add-fact Person has-key table0:SSN
add-fact Person has-key SSN

    no source table0:
    >from source mapping

add-fact Person has-one table0:Name
add-fact Person has Name exactly-one

add-fact Person has-zero-or-many table0:Phone
add-fact Person has Phone some

add-fact Department has-key table0:DepName as "Department Name"
add-fact Department has DepName
add-fact Department has-key DepName

add-fact Department has-one-unique table0:DepAddress as "Department Address"
add-fact Department has DepAddress exactly-one as:DepartmentAddress

    no unique
    >SQL alias AS field rename

add-fact Person works-in-one Department
add-fact Person works-in exactly-one Department

add-fact Employee is-a Person where Department is-not-null
add-fact Employee is-a Person

    no where
    >condition




* link to json page
* cmd get-json
    * compute mapping (recursive) to data

* cmd sql-show-carm
    * enhanced 6nd + oids

----
* cmd add-labels <element-name> <predicate> <cobject>
  * dereference <element-name> to <sbj-id>
* link to jsonld page
* cmd get-jsonld
  * compute mapping (recursive) to data
  * enrich with rdf labels

    
-----
* noteedit terms cell
    * links to OntoUML terms

* noteedit references cell
    * links to CARM
    * links to ORM
    * links to OntoUML
    * links to JsonLD


------
* new features
  * to noteedit add links to goals diag and goals.md (if exists).
  * to noteedit add links to terms diag and terms.md (if exists).
  * story permalink.
  * link to story log by gid.
  * start with readme.md if exists.

## open level 2:
    

* Log4JLoggerService()
* class diag cardinality
* gids
  * for stories? 
* settings IRIContext -> PrjContext
* upload rdf metadata
* notebook.html eviction
* upload sql
* import sql northwind as source
  * http://localhost:7000/forcetree/index.html
    https://github.com/FabioBentoLuiz/northwind/blob/master/northwind-backend/src/main/resources/schema.sql

* queryJsonEntity. referencedTables
        computeSelectString
            database.mapping(askedTable) or else 
                    build a select from table columns and where conditions.

* fix term page editor, to display terms : mappings, database, rdf labels, mappings SQL. 
* fix jsonld export with rdf labels.

* import-json : swagger-odh.json

* refact SchemaCmdParser to do only parsing of ConstraintTokens and leave building to Schema

* FactParserTest

* chartColouredTables : 
    * chartIsaLinks with stereotypes and category: cover,partition,exclusive
    * chartMappingLinks with stereotypes and cardinalities
  
* ${context.env.prjContextIRI.value} to set from user label.

* import ontouml alpinebits
  * TraceCmdImportOntouml from context
  * TraceCmdReadOntouml

## open level 2:

* KP Check, given a notebook cell execution cheks if the contents matches with the expected value in the result cell.
* TraceCmdSqlSelect from sources (via sql parse as Query UnSQLizeSelectUseCase().fromsql(mappingName, sqlView))
* Sql6nf
* DataServiceAdapter queryJsonEntity: header, referenced TO COMPLETE
* case ontouml alpinebits
  * case_employee.md
    * rewrite query,insert,diff based on mappings
    * http://localhost:7000/project/employee/chart/classes
    *  source: table0 	employee:data/table0.csv 	CSV
  >  current-source -newSource=table0
  >  SELECT ssn,name FROM table0 WHERE depname is not null
          al momento il file viene cercato nel contesto attivo e non dal prefisso
      le query NON comando , ma con rotte tipo JSON non usano correttamente la source
     va esteso il comportamento alle rotte non comando.

import
sources:
* from csv
  TraceCmdSqlCreateTableFromCsv -> H2
  TraceCmdSqlCreateTableFromCsvNote -> H2
* from json
  JacksonFlattenerTest -> MutableMap
* from jdbc
  MetaSchemaJdbcAdapterTest.test_mysql_meta -> Database
* from ddl sql
  DataService.createTableFromFileSql -> H2

vocabularies:
* kp from ttl ontouml
  RdfServiceAdapterTest
  .test_read_alpinebits_local_resource -> println
* from jsonld
  JsonldTest.test_read_person_jsonld -> RDFDataSet
* from orm
  FactParserTest -> Database



* localhost:7000/project/employee/chart/classes
    for ontouml coloured classes diagram

* doc/archi/link-goal-story-term.md
* use vocabularies in context for labels
* use source in context for queries
* identities log
* swagger
  ApiBuilder.get("/context/:contextName/swagger", viewController.getContextSwaggerPage)
  ApiBuilder.get("/context/:projectName/dictionary/:traceName/:traceFileName/jsonapi", termController.getTermsJsonApi)
  JsonApiServiceAdapter
  SwaggerService
* GraphQL 

## from confucious

* adds goal shape : task, completed
* add goal export md goal links to stories.
* add table chart primary keys.
* add table chart color style  cssClass "nodeId1" styleClass;

## to fix:

* kp employee goals page to fix:
    - start, end, due dates to edit.
* kp employee terms page to fix:
    - load data from source csv.
    - buttons: json, jsonld, prov, table, chart:
        - non appaiono sulle righe giuste.
        - link non funzionanti.
    - selettori di riga per label, non selezionano.
    - autocompletamento
        - aggiungere info da source: tabelle, colonne
        - filtrare solo sui vocabolari selezionati
    - bottoni autocompletamento
        - completano la line con un substring sbagliato:  ERSON.DIP_INDIRIZZO
    - turtle
        - export to fix
    - swagger
        - datatype to fix, è fisso a varchar(64)
        - version to fix è fissa a 1.0.17
        - authorize
* kp page shortcuts
    - G goals
    - U users
    - E events
    - S stories
    - T terms
* FIX meta on datasource to catch JDBC errors, not give an empty db BUT print the error.

* FIX json API terms, and project infos.

* FIX add story to goal from button.

goes to old version noteedit.html

not adds line to file:

+ goal <goalID>

* FIX link from goal chart to read only is broken:

http://localhost:7000/noteview.html?pr=kprime-case-cockpit&tr=___&tf=Framework,%20methodology%20and%20tool%20to%20support%20collaborative%20process%20modeling.md


* FIX link from notebook editor to goal is broken

http://localhost:7000/project/kprime-case-cockpit/todo/Methodology%20and%20tool%20for%20real-time%20measurement%20of%20the%20progress%20on-%20site


* FIX from public project page
  http://localhost:7000/project/

  you can not browse to projects with "-" in their name.

* FIX internal links from stories
 
  * fix: http://localhost:7000/noteview.html?pr=kprime-case-confucius-mysql&tr=___home___nicola___Workspace___kprime-cases-bz___kprime-case-confucius-mysql&tf=readme.md
    good:     http://localhost:7000/noteview.html?pr=kprime-case-confucius-mysql&tr=___&tf=readme.md
    good: http://localhost:7000/noteview.html?pr=kprime-case-confucius-mysql&tr=queries&tf=activity-report.md

          ChartCaseService

          val todos = todoService.all(prjContextLocation)
          val tasks = todoService.tasks(prjContextLocation)

           if (prjContext!=null && !prjContext.isNoProject()) {
              markdownDiag += "click id${todo.id} \"/noteview.html?pr=${prjContext.name}&tr=${task.traceName}&tf=${task.storyFileName}\" \"Goto to task story.\"" + lineSeparator
            } else {
              markdownDiag += "click id${todo.id} \"/slide/${task.traceName}/${task.storyFileName}\" \"Goto to task story.\"" + lineSeparator

* fix: link goal-story-term
* fix: vuca (using cmdlog)
* fix: zero warnings
* fix: zero test log
* fix: add source driver name -> driver class

* fix: use context datasources insted of instance datasources.
  use instance drivers.

* fix: SourceService : use context Sources not only instance Sources.

* fix: TraceController
  result.message = result.message.replace('<','_')
  result.message = result.message.replace('>','_')
  result.message = result.message.replace("\n","<BR>")

    is required for multiline command in tpp4 notebook
    is dangerous for single line command in terms

* fix: http://localhost:7000/context/diary/tracesave/stories/abc.md
  if (traceDir == '') traceDir = "stories";
  http://localhost:7000/context/diary/tracesave/stories___2022___11/20221002.md

* fix: find labels search really a label in context
* fix: story notebook trace browser.
* fix: story delete from storyedit.
* fix: admin management.
* fix: check ALL command argument validity.

## to add:


* ADD new project has not gid to show on client side

gid cd2b08ff-ae41-44ca-80ae-c42844a52bb0

* ADD edit "part of" project
  manually enter parent gid

* ADD project active switch to edit


* ADD project chart semantic links

* ADD semantic labels:
    - challenges
    - opportuninties
    - novelty

* ADD make readme.md the default context story

* ADD terms,story,goal refact, remove.

* add: to dictionary, ttl properties e.g. https://github.com/bp4mc2/archimate2rdf/blob/master/src/main/resources/ontology/archimate.ttl
* add: when enter page story editor, open /readme.md if present. 
* add: goal actors.
* add: goal resources.
* add: story labels.
* add: goal labels.
* add: rename remove :term in .md and .chart . 
* add: rename remove :goalN in .md and .chart .
* add: chart RDF.
* add: json API.
* add: generate .md from context, goal, dictionary.
* add: generate readme.md template.
* add: last modified.
* add: gids + commandlog.
* add: home stats.
* add: SettingService add userName to settings x user (with cache).
* add: convert to on browser with js:
    * add: convert local link for stories [](.md) in contextualized links
    * add: convert local link for files []() in contextualized links in new window
    * add: convert local link for images ![]() in contextualized images
      ![](/context/:context/image/:traceName/:traceFileName)
      StoryService.readNotes
      http://localhost:7000/project/kprime-case-confucius/file/resources___prova/Confucius.jpg
* add: add-find-rem labels using gid names.
* add: import terms from ttl TurtleTest alpinebits.
* add: commandlog pagination and search.
* add: expert hexagon pure method in auto loading module (no webcall).

## to refact:


* refact: SettingService moving method to Services.
* refact: command argument validity checks (like TraceCmdAddActor).
* refact: controller only command calls, no direct service call. (see controller-command-service.md)
* refact: service call hierarchy. from commands and API  (like ActorController).
* refact: service error return catch (like ActorService).



## features

* terms: export ttl
* terms: import ttl
* terms: export json
* terms: import json
* command log with undo
* jsonld
* jsonprov
* consistent renaming
* transformer inherited constraints
* ARM identities
* [FAIR principles](principles.md)
* [normalizations](normalization.md)
* I18N allow managing context with multi-language labels.
* https://github.com/RMLio/rmlmapper-java
* https://github.com/centic9/jgit-cookbook

## commands

### dictionary:

* dictionary terms with labels
    * to fix: string returned for labels has too many ',' clashes with same char used in triple string representation.
    * to fix: label filter doesn't function.

### table:

* add '+ <table>|<term>(key|id):attributes'
* add '+ <table>|<term>.<attribute> <ctx:datatype> <ctx:vocabulartype> <resolution> <min> <max> <pattern> <cardmin> <cardmax>'
= add '+ <table>|<term> has <attribute> <ctx:datatype> <ctx:vocabulartype> <resolution> <min> <max> <pattern> <cardmin> <cardmax>'

### goal:

* rename -> rename story
* edit story -> add story ID

  add-goal title [actor|priority|part-of|label]

### actor:

* admin with pass random generated on apprun if pass is empty
  * admin is the only can create change actors
  rename an actor -> change actor on log

### label:

    add-label [goal|story|schema] label
    find-label label

### doc:

    add-doc <story|journal> <doc-name>
    copy .md file from doc folder to current trace renaming to doc-name.md
    
    del-doc <doc-name>
    deletes from current trace doc-name.md

### dictionary:

    add-note [database,concept,attr,rel] free-text
    add-description [database,concept,attr,rel] free-text
    add-url [database,concept,attr,rel] free-text

### term:

* synonyms, multi-language

* finds a term inside paragraphs of md docs in current trace

    find-term
    file: line nr: print line containng term

* calculate graph of terms inside the same parameters and most important terms in paragraph.
    
    find-term-graph
    
output use frequency of a term
    calc-term-stat


### project:

    publish versioning (active is latest running version)

* a why map http://localhost:7000/project/asa/cases
* a story http://localhost:7000/project/asa/slide/root/Story_story_room_reservation.md#1
* a story line http://localhost:7000/project/asa/chartactivities/root/Story_story_room_reservation.md
* a sequence http://localhost:7000/project/asa/doc/root/seq-room-reservation.chart
* a term list http://localhost:7000/project/asa/dictionary/root/reservation3_db.xml
* a term base: http://localhost:7000/chart/classes/root/reservation3_db.xml

project publish (active trace)
= copy trace  + todos    + terms folders
    route /published/<prj>/<timestamp>/*
    to published_folder/project/timestamp/
    published
        date
        author
        version
        uuid
        title
        description

## done

20230908
+ cli: post file 
+ TraceCmdRemLabelInstance
+ TraceCmdAddLabelInstance
+ TraceCmdRemNamespace

20230909
+ chartColouredClassesHtml to projectLocation stereotypes.
+ fix project context edit, realigned jackson wrapper.
