# KPrime


## KPrime Deployment

* KPrime could be run as a web server or standalone shell application.
* KPrime as a server could have many instances per physical machine identified by URL with specific portnumber.
* KPrime as a standalone app has a CLI to run from operating system shell.

## KPrime Goal

To build a Free and Open Ubiquitous Language Editor. 

    - free: so any one can model, without barriers.
    - open: so that your model will be yours forever, without lock-ins.

## Introduction

### Why a Ubiquitous Language

One of the problems much software development efforts face is the constant friction introduced by translation between 
two technical vocabularies, that of the business domain on the one hand and that of the developers on the other.

To some extent this duality is inevitable: developers must frame their work in terms of algorithms and computation, 
which generally have no direct equivalent in the business vocabulary.

However, the technical vocabulary often tends to “leak” out from reasonable boundaries and take over design 
conversations to the point where business experts start feeling alienated and disengage from crucial conversations.

Deliberately and explicitly adopting a “ubiquitous language” policy mitigates these difficulties and 
is therefore a success factor in Agile projects.

[Source: Agile Alliance](https://www.agilealliance.org/glossary/ubiquitous-language)



### What is a Ubiquitous Language (Ubi Lang)

Ubiquitous Language is the term Eric Evans uses in Domain Driven Design for the practice of building up a common, 
rigorous language between developers and users. This language should be based on the Domain Model used in the software 
- hence the need for it to be rigorous, since software doesn't cope well with ambiguity.

[Source: Martin Fowler](https://www.martinfowler.com/bliki/UbiquitousLanguage.html)

We expect every software project has an Ubi Lang, used orally each day at work in informal communication.
This is the required language and it's dependencies every team newcomers has to learn as quick as possible 
in order to understand, correctly modify the software system and express to stakeholders impact on requirements and features.
And also to stakeholder to completely understand the system outcomes, limits and behaviours. 

To avoid ambiguities it is clear that an informal specification it's not enough.

### Why a Ubiquitous Language Editors - Features

In our experience a fully expressed Ubiquitous Language it requires a specific Editors 
with many ingredients requiring specific features and attentions:

    - Contexts editor: define lang composition subcontexts amd neighborhood contexts. 
    - Goals editor: define lang priorities
    - Story editor: define top priority requirements applying the lang
    - Terms editor: define lang single terms and their relationships.

A Ubi Lang has to be used as-is inside artifacts, as projections.
Or transformed to a new version, evolving.
Or mapped via context-mapping to neighbours Ubi Langs.
Or mapped via data-mapping to connected data sources.

### Transformations

An Ubi Lang transformation depending on the level of care and formallity required.
Changes could be an oral communication.
A change log file collecting changes in natual language.
A structured log.
A KPrime Changeset log.
A validated Changeset log, using validation experts.

### Context Mappings

As defined in Ubi Lang, API used in SOA or Microservices have to respect Contracts to share information.
See [contexts.md]

### Data Mappings

As defined in Ubi Lang, Data source have to respect the structure (DDL) and behaviour (DML, triggers).
See [databases.md]

### Code Mappings

As defined in Ubi Lang, Domain source coda has to implement the structure (Classes) and behaviour (Functions) expected 
from stakeholders and software users. 

## Actors

Using in business a Ubiquitous Language are people called actors, for not exaustive example:

    - Triggers: Machine to machine communications.
    - Developers: As Ubiquitous Language .
    - UX Designers: 
    - Anyone Connected with the software development: As Ubiquitous Language Connoisser  (read)

## Users

Users are those that require to interact with KPrime to get some Ubi Lang services, are: 

    - Admins, those who manage the instances and applications.
    - Authors, those who create and edit a Ubi Lang.
    - Readers, those who use a Ubi Lang to get an unbiguos communication reference for business.

---
[PRINCIPLES](doc/principles.md)

Anything that guides design and implementation decisions.

[CONSTRAINTS](doc/constraints.md)

Anything that constrains design and implementation decisions.

[CONTEXT AND SCOPE](doc/archi/context.md)

Delimits your system from its (external) communication partners (neighboring systems and users). 
Specifies the external interfaces. Shown from a business/domain perspective (always) 
or a technical perspective (optional)

[CODE ARCHITECTURE](doc/archi/archi.md)

About Implementation:

- Building Blocks
- Scenarios
- Crosscutting Concerns
- Architecture Decision Records

[GLOSSARY](doc/glossary.md)

[BUILD AND RUN](doc/build_run.md)

How to build and run the server with options and configuration to start writing your models.

[QUICKSTART](doc/use/quickstart.md)

The User Manual

- [ELEMENTS USAGE](doc/use/elements/index.md)
- [PAGES USAGE](doc/use/pages/index.md)
- [CASESTUDIES USAGE](doc/use/index.md)
- [COMMANDS USAGE](doc/use/usage.md)
- [EXPERTS USAGE](doc/use/index.md)

The Command Reference manual

[TODO](doc/todo.md)

