# vocabularies

Each set of terms could refer to shared vocabularies.

To list available vocabularies:

    >available-vocabularies

To enrich available vocabularies:

    >create-vocabulary <vocabulary-name> <url>

To remove from available vocabularies:

    >drop-vocabulary <vocabulary-name>

To add a vocabulary to current context:

    >add-vocabulary <vocabulary-name>

To remove a vocabulary to current context:

    >rem-vocabulary <vocabulary-name>

To list datasources into current context:

    >vocabularies

---------------------

A vocabulary is used in a command:

    >add-fact <vocabulary-prefix>: (press-enter to see options) 


A vocabulary is available per active context:

    <context>/terms/root/base.json

A vocabulary is available if present as file turtle extension .ttl in directory:
    AND added to current dictionary vocabularies config.
    AND file ends with "ttl"

    KPRIME_HOME/vocabularies/<vocabulary>.ttl

A vocabulary is remote when reference starts with "http"
    AND file ends with "ttl"

    http://rs.tdwg.org/dwc/version/terms/2021-07-15.ttl
