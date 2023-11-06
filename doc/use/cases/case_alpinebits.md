# Case Alpinebits

- To import ttl ontology and metadata. From https://github.com/OntoUML/ontouml-models
- To connect with json api data source. From https://api.tourism.testingmachine.eu/v1/SkiArea


-------------


http://purl.org/ontouml-models/dataset/alpinebits2022/Ocg3Uf6AUB0C9C23 http://www.w3.org/1999/02/22-rdf-syntax-ns#type https://purl.org/ontouml-models/vocabulary/Class
http://purl.org/ontouml-models/dataset/alpinebits2022/Ocg3Uf6AUB0C9C23 https://purl.org/ontouml-models/vocabulary/project http://purl.org/ontouml-models/dataset/alpinebits2022/lLpkJeaGAqACBwhZ
http://purl.org/ontouml-models/dataset/alpinebits2022/Ocg3Uf6AUB0C9C23 https://purl.org/ontouml-models/vocabulary/name Event Series Category
http://purl.org/ontouml-models/dataset/alpinebits2022/Ocg3Uf6AUB0C9C23 https://purl.org/ontouml-models/vocabulary/stereotype https://purl.org/ontouml-models/vocabulary/subkind
http://purl.org/ontouml-models/dataset/alpinebits2022/Ocg3Uf6AUB0C9C23 https://purl.org/ontouml-models/vocabulary/restrictedTo https://purl.org/ontouml-models/vocabulary/type
http://purl.org/ontouml-models/dataset/alpinebits2022/Ocg3Uf6AUB0C9C23 https://purl.org/ontouml-models/vocabulary/isAbstract false
http://purl.org/ontouml-models/dataset/alpinebits2022/Ocg3Uf6AUB0C9C23 https://purl.org/ontouml-models/vocabulary/isDerived false
http://purl.org/ontouml-models/dataset/alpinebits2022/Ocg3Uf6AUB0C9C23 https://purl.org/ontouml-models/vocabulary/isExtensional false
http://purl.org/ontouml-models/dataset/alpinebits2022/Ocg3Uf6AUB0C9C23 https://purl.org/ontouml-models/vocabulary/isPowertype false
http://purl.org/ontouml-models/dataset/alpinebits2022/Ocg3Uf6AUB0C9C23 https://purl.org/ontouml-models/vocabulary/order 1

    :dcg3Uf6AUB0C9C28 rdf:type ontouml:Generalization;
    ontouml:project :lLpkJeaGAqACBwhZ;
    ontouml:general <http://purl.org/ontouml-models/dataset/alpinebits2022/1BQ9Wh6GAqACHRWi>;
    ontouml:specific :Ocg3Uf6AUB0C9C23.
    
    
    :V_K3Uf6AUB0C9C4T rdf:type ontouml:Relation;
    ontouml:project :lLpkJeaGAqACBwhZ;
    ontouml:stereotype ontouml:instantiation;
    ontouml:isDerived "true"^^xsd:boolean;
    ontouml:sourceEnd :V_K3Uf6AUB0C9C4U;
    ontouml:targetEnd :V_K3Uf6AUB0C9C4W;
    ontouml:relationEnd :V_K3Uf6AUB0C9C4U, :V_K3Uf6AUB0C9C4W.


    :V_K3Uf6AUB0C9C4W rdf:type ontouml:Property;
        ontouml:project :lLpkJeaGAqACBwhZ;
        ontouml:name "category"@en;
        ontouml:cardinality :V_K3Uf6AUB0C9C4W_cardinality.
    :V_K3Uf6AUB0C9C4W_cardinality rdf:type ontouml:Cardinality;
        ontouml:cardinalityValue "0..*";
        ontouml:lowerBound "0";
        ontouml:upperBound "*".
    :V_K3Uf6AUB0C9C4W ontouml:isDerived "true"^^xsd:boolean;
        ontouml:isReadOnly "false"^^xsd:boolean;
        ontouml:isOrdered "false"^^xsd:boolean;
        ontouml:aggregationKind ontouml:none;
        ontouml:propertyType :Ocg3Uf6AUB0C9C23.
    

    :V_K3Uf6AUB0C9C4U rdf:type ontouml:Property;
        ontouml:project :lLpkJeaGAqACBwhZ;
        ontouml:cardinality :V_K3Uf6AUB0C9C4U_cardinality.
    :V_K3Uf6AUB0C9C4U_cardinality rdf:type ontouml:Cardinality;
        ontouml:cardinalityValue "*".
    :V_K3Uf6AUB0C9C4U ontouml:isDerived "false"^^xsd:boolean;
        ontouml:isReadOnly "false"^^xsd:boolean;
        ontouml:isOrdered "false"^^xsd:boolean;
        ontouml:aggregationKind ontouml:none;
        ontouml:propertyType <http://purl.org/ontouml-models/dataset/alpinebits2022/gjQq0.aGAqAovRJq>.


<http://purl.org/ontouml-models/dataset/alpinebits2022/gjQq0.aGAqAovRJq> rdf:type ontouml:Class;
ontouml:project :lLpkJeaGAqACBwhZ;
ontouml:name "Event Series"@en;
ontouml:stereotype ontouml:subkind;
ontouml:isAbstract "false"^^xsd:boolean;
ontouml:isDerived "false"^^xsd:boolean;
ontouml:isExtensional "false"^^xsd:boolean;
ontouml:isPowertype "false"^^xsd:boolean;
ontouml:order "1"^^xsd:positiveInteger;
ontouml:attribute <http://purl.org/ontouml-models/dataset/alpinebits2022/_MBJspaGAqAovRKE>.

------

flowchart TD
classDef role fill:#ff0000,stroke:#333,stroke-width:2px;
classDef relator fill:#ffaaaa,stroke:#333,stroke-width:2px;
classDef collective fill:#ffcccc,stroke:#333,stroke-width:2px;
classDef datatype fill:#ffffff,stroke:#333,stroke-width:2px;
classDef category fill:#eeeeee,stroke:#333,stroke-width:2px;
classDef quality fill:#aaaaff,stroke:#333,stroke-width:2px;
classDef kind fill:#bbbbff,stroke:#333,stroke-width:2px;
classDef subkind fill:#ddddff,stroke:#333,stroke-width:2px;
classDef type fill:#aaaaff,stroke:#333,stroke-width:2px;
classDef phaseMixin fill:#ffdddd,stroke:#333,stroke-width:2px;
classDef phase fill:#ffbbbb,stroke:#333,stroke-width:2px;
classDef roleMixin fill:#ffdddd,stroke:#333,stroke-width:2px;

EventSeriesCategory{{role </br> Event Series Category}}
class EventSeriesCategory role

EventSeriesCategory2{{role </br> Event Series Category2}}
class EventSeriesCategory2 role

EventSeriesCategory2-->EventSeriesCategory
