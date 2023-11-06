# Databases

A Database is an abstract relational representation of a container of data.

* A Database is a logical level artifact.
* There is just one Database per Context.
* A Database has one Schema.
* A Schema has at least one Table.
* A Database may have a physical connection via [datasources] and [mappings].

## Database Commands

    > database
    > tables
    > mappings
    > sources


## Changing a Database

There are two ways:
  - direct manipulation commands
  - transactionally via a changeset 

### Create a changeSet

We start creating a table to store personal data as names and surnames.  

    add-cs-table person:name,surname

Implicit defaults:

* current context, alternative:

    add-cs-table -context:myContext person:name,surname

* update current changeSet, no changeSet name, alternative:

    add-cs-table -changeset:myChangeSet person:name,surname

* autoincrement id, alternative id name:

    add-cs-table person[idx generated]:name,surname

* no index, alternative:

    add-cs-table person:(name,surname)

* not nullable, alternative:

    add-cs-table nullable person:name,surname
    
* not unique, alternative: 

    add-cs-table unique person:name,surname

* id key, alternative:

    add-cs-table unique person:[name,surname]

* no schema or catalog, alternative:

    add-cs-table -schema:mySchema person:name,surname
    add-cs-table -catalog:myCatalog person:name,surname
    
* no source, alternative:

    add-cs-table -source:mySource person:name,surname

### Add a new source

    add-source mySource 

Implicit defaults:

* current context
* trademark H2 database
* mySource file name
* user 'su'
* no pass

### Project changeSet to source 

    execute-cs

Implicit defaults:

* current context
* current changeSet
* apply to current termbase
* SQL changeSet translation
* name is the column name, datatype is varchar
* surname is the column name, datatype is varchar
* person is the database table name 


### Insert a row

    insert person:Gino, Dal Sasso

Implicit defaults: 

* current context
* current source


---

## From Existing

We start from existing sources

### List sources

    sources

### Add a sources (if not already present)

For this example we will add an H2 db persisting in context data/ folder.

    add-source h2 mySource

### Set the source to use

    set-source  mySource

### Create a changeSet

    add-cs-column person:age int
    
Implicit defaults:

* current context
* current changeset
* apply to current termbase
* nullable column    

### Apply changeSet to source

    execute-cs

### Query rows

    select person:Gino

Implicit defaults:

* populate termbase with meta 

### Complete rows

to complete filling nulls in a single row:

    complete person:Gino
    age? 33

or to fill all null for all rows once a time:

    complete person
    Gino.age? 33

or to fill all null in all rows in automatic: 

    complete person age 33

## select from browser json view

http://localhost:7000/data/root/base/ACTOR?ACTOR_ID=4
http://localhost:7000/data/root/base/FILM?TITLE LIKE 'B%25'
http://localhost:7000/data/root/base/FILM?TITLE%20LIKE%20%27B%25%27
http://localhost:7000/data/root/base/FILM?TITLE%20LIKE%20%27S%25%27&LENGTH=%2780%27



## Mappings

List current mappings:

    >mappings

Get details about one specific mapping:

    >mapping <mapping-name>
  
    >mapping employee