# Sources (3)

* A Source is an external representation of a Data Source.

* A Source could be a relational database.

* A Source could be a json, csv, yaml markdown notebook cell.

* A Source could be a json, csv, yaml file.

* A Source has a scope, it could be of context or instance. 

* A Source of instance is shared between all instance users.

* A Source of context is shared between all context users.

* A Source has a name.

* A Source has a type.

* A Source has a location.

* A Source may have a Driver.

* A Source may have user and pass credentials to authorize.

* A Source may have a license label.

* A Source may have a license URL.

## Drivers

* A Driver is a JDBC Driver.

* Drivers are collected from Java ClassPath.

## DataSource (26)

* A DataSource is an internal representation of a handler to a physical Data Source.

* A DataSource is a physical level artifact.

* A DataSource maintain as Resources a cached instance of Driver Connection.

## DataSourceConnection (9)

* A DataSourceConnection is the internal wrapper of Source connection parameters.


## Source Commands

    > sources -scope=<instance|context>

    > source <scope.prefix.I|C><Source.id><_>
    
    > add-source mydb org.h2.Driver sa jdbc:h2:mem:mydb

    > rem-source <sourceName>

    > use-source <sourceName>

    > meta <sourceName>