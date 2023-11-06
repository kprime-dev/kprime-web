# PRINCIPLES
## Manifesto for Software Craftsmanship and KPrime implications 

     Not only working software,
    but also well-crafted software

So a living model is a requirement not a write-and-forget artifact. 

    Not only responding to change,
    but also steadily adding value

So a model has to fulfill an explicit goal. 
    
    Not only individuals and interactions,
    but also a community of professionals

So a model has to be anytime readily enriched by the team. 

    Not only customer collaboration,
    but also productive partnerships 

So a model has to be as fast as possible accessible and readable by the customer. 
 
## FAIR principles

https://www.go-fair.org/fair-principles/

### Findable
The first step in (re)using data is to find them. Metadata and data should be easy to find for both humans and computers. Machine-readable metadata are essential for automatic discovery of datasets and services, so this is an essential component of the FAIR process.

F1. (Meta)data are assigned a globally unique and persistent identifier

    Each semantically relevant kprime entity has a UUID, subparts have a relative suffix.
    
    Relevant kprime entities are each:
    * goal, subparts: subgoal
    * story, subparts: paragraph
    * database, subparts: schema, tables, contraint, mapping
    * changeset, subparts: change
    * dictionary, subparts: term
    
    Each semantically relevant kprime entity has a name, unique inside a project.
    Each project has a name, unique inside a kprime server.
    
    So, each semantically relevant kprime entity has two unique URI:
    /<kprime-server>/<project-name>/<entity-name>[<subpart-suffix>]  
    /<kprime-server>/<project-name>/<entity-UUID>[<subpart-suffix>]  

F2. Data are described with rich metadata (defined by R1 below)

F3. Metadata clearly and explicitly include the identifier of the data they describe

F4. (Meta)data are registered or indexed in a searchable resource

### Accessible
Once the user finds the required data, she/he needs to know how can they be accessed, possibly including authentication and authorisation.

A1. (Meta)data are retrievable by their identifier using a standardised communications protocol

A1.1 The protocol is open, free, and universally implementable

A1.2 The protocol allows for an authentication and authorisation procedure, where necessary

A2. Metadata are accessible, even when the data are no longer available

### Interoperable
The data usually need to be integrated with other data. In addition, the data need to interoperate with applications or workflows for analysis, storage, and processing.

I1. (Meta)data use a formal, accessible, shared, and broadly applicable language for knowledge representation.

I2. (Meta)data use vocabularies that follow FAIR principles

I3. (Meta)data include qualified references to other (meta)data

### Reusable
The ultimate goal of FAIR is to optimise the reuse of data. To achieve this, metadata and data should be well-described so that they can be replicated and/or combined in different settings.

R1. (Meta)data are richly described with a plurality of accurate and relevant attributes

R1.1. (Meta)data are released with a clear and accessible data usage license

R1.2. (Meta)data are associated with detailed provenance

R1.3. (Meta)data meet domain-relevant community standards

The principles refer to three types of entities: data (or any digital object), metadata (information about that digital object), and infrastructure. For instance, principle F4 defines that both metadata and data are registered or indexed in a searchable resource (the infrastructure component).



### FAIR Data Point

https://github.com/FAIRDataTeam/FAIRDataPoint-Spec


FAIR Data Point (FDP) is a metadata repository that provides access to metadata in a FAIR way. FDP uses a REST API for creating, storing and serving FAIR metadata. FDP is a software that, from one side, allows digital objects owners/publishers to expose the metadata of their digital objects in a FAIR manner and, for another side, allows digital objects' consumers to discover information (metadata) about offered digital objects. Commonly, the FAIR Data Point is used to expose metadata of datasets but metadata of other types of digital objects can also be exposed such as ontologies, repositories, analysis algorithms, websites, etc.

A basic assumption for the FDP is its distributed nature. We believe that big warehouses spanning multiple domains are not feasible and/or desirable due to issues concerning scalability, separation of concerns, data size, costs, etc. A completely decoupled and distributed infrastructure also does not seem realistic. The scenario we envision has a mixed nature, with a number of reference repositories, containing a relevant selection of core digital objects, e.g., EBI's repositories, Zenodo, BioPortal, etc., integrated with smaller distributed repositories, e.g., different biobanks, digital objects' repositories created within the scope of research projects, etc. Many different repositories and their digital objects should interoperate in order to allow increasingly complex questions to be answered. Interoperability, however, takes place in different levels, such as syntactical and semantical. A collection of FDPs aim to address this interoperability issues by enabling digital objects' producers/publishers to share the metadata of their objects in FAIR manner and, therefore, fostering findability, accessibility, interoperability and reusability.

The main goal of the FDP is to establish a common method for metadata provisioning and accessing and, as a consequence, (client) applications have a predictable way of accessing and interacting with metadata content. To fulfill this goal, we created two types of artefacts. A set of specifications to help developers extend the funciontalities of their applications so that they behave also as FAIR Data Points and a reference application for those who would like to have the FDP functionality in a stand-alone web application.


