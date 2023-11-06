# Manual

---

After installation of KPrime HOME, if you meet the runtime requirements, then you could start playing along these guidelines.


---
## Goal of KPrime

---

KPrime helps in the process of writing a “living formal specification of a shared conceptualization”.

Living refers to striving as much as possible for automation the process of maintenance of models.

Formal refers to the fact that ontologies are frequently written in a formal (and often also machine-readable) language, which is a set of finite symbol structures taken from a finite alphabet of symbols and defined by syntax.

The word specification requires that an ontology is an appropriate representation of its universe of discourse, which is typically referred to as but not limited to a (semantic) domain.

The word shared refers to the need for social agreement about and shared understanding of the terms in the ontology.


---
## In a nutshell

---
You create a project for each bounded context to formally define your Dictionary, your ubiquitous language composed of terms.

Those Terms could have a data connection.

Those Terms are used inside Stories you narrate to fulfill some Goal. 

1. Give a name and position to the project.
2. Start from goals.
3. Write the stories to reach your goals using terms.
4. Build a language dictionary.
5. Expose and share your dictionary.



---
## 1. Give a name and position to the context

---
Is the first step.

Decide the filesystem position the KPrime WORKING DIR
has to be an existent writable file system folder.
It is where your projects folder will persist as text files. 
That will contain all his related goals, traces and dictionary. 

    Home > Settings



---
## 2. Start from goals

---
To start building your shared conceptualization you need at least one Goal.


    Home > Goals > Add goal


Edit goal details.

Give goals a priority.

The most important goal is the north star goal that will guide your next steps. 



---
## 3. Write the stories to reach your goals using terms

---

Add one source giving credential and source coordinates.


---
### 3a. From blank

---
### 3b. From template

---




---
## 4. Collect terms to build a language dictionary

---

To build a dictionary you could:
- add terms
- add facts

A dictionary is composed of terms and a database logical schema.

logical schema you have to create a Trace.

A trace is a folder containing Databases, Changesets and Stories. 

Using the command line you could create these objects and interract with them until
you have modeled your conceptualizazion of the context.

    Home > Traces > Command line


---
### 4a. Or Start from data

---
If you have a dataset you would like to integrate with your logical schema.
Start building your shared conceptualization you need a Datasource.

    Home > Traces > From Source > Editor

Actually there are Datasource connectors for:

* Databases via JDBC
* Text tables via CSV



---
## 5. Expose and share your dictionary

---

Using Dictionary you could expose your conceptualization following web standards and share it.

    Home > Dictionary

Using Dictionary REST URI you could:

Expose dataset (JSON)

Expose schema (JSON-LD) 

Expose provenance (JSON-PROV)

---
