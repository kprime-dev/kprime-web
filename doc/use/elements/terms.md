# Terms

Terms are conceptual level artifacts.

Each term must have a name. Just enter a label with a-z A-Z 0-9 chars.


Each term may have a short free text description.

Each term may have a type.

Each term may have attributes.

Each term may have relations.

Each term may have a datatype.

Each term may have a data mapping.

## commands

    > terms

    > term <term.name>
    > add-term <term.name> <term.description>
    > rem-term <term.name>

## term termtypes

    >termtypes <vocabulary-name>

example:

    >termtypes schema

outputs:

    AMRadioChannel	A radio channel that uses AM.
    APIReference	Reference documentation for application programming interfaces (APIs).
    Abdomen   	Abdomen clinical examination.
    AboutPage 	Web page type: About page.    

## set term type

    >set-type 



## term datatypes

Each term could have a definition of data instance.

To list available data-types for a specific datasource.

    >datatypes <datasource-name>


example:

    >datatypes persondb

outputs:


## set term datatype

To map a table to a term:

    >map-data <term-name> <data-source>.<table-name>

To set a data-type for an attribute:

    >set-datatype <term-name>.<att-name> <data-type> 

