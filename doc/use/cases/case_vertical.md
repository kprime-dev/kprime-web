# esempio person

add-database person-trace persondb
add-table person:ssn,name,dep_name,dep_address
add-key person:name
add-functional person:dep_name-->dep_address

person-trace
persondb_9_675000000_tracedb.xml

Tables:
person: ssn , name , dep_name , dep_address

-------------
Constraints:
 person.primaryKey of type PRIMARY_KEY
 person.functional of type FUNCTIONAL


compute vertical decomp

person-trace
20200401_135507_0058000000_db.xml

Tables:
person_1: name , dep_name , ssn
person_2: dep_name , dep_address

-------------
Constraints:
 person.doubleInclusion of type DOUBLE_INCLUSION
 person_2.primaryKey of type PRIMARY_KEY
 person_1.primaryKey of type PRIMARY_KEY


mappings

---------
CREATE OR REPLACE VIEW public.person_1 AS
SELECT name,dep_name,ssn
FROM   person
---------
CREATE OR REPLACE VIEW public.person_2 AS
SELECT dep_name,dep_address
FROM   person
