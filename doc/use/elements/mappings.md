# Mappings

A mapping is a relational view expressed as SQL query, connecting logical level to physical level databases.

SQL syntax maps from basic algebraic operations plus grouping:

    SELECT
    FROM
    WHERE
    GROUP BY

No anonymous sub-select are allowed but usually can reuse other mapping referenced by name in FROM clause.

## Editing Mappings

>mappings
>mapping <mapping-name>
>add-mapping <mapping-name> <SQL>
>rem-mapping <mapping-name>
>rename-mapping <oldname> <newname>

## Query Mappings

>select <mapping-name>

## Mapping Explosion

Computes the real mapping al logical level. 

    TraceCmdSqlSelect.explodeMapping(database: Database, mapping: Mapping): Mapping

    test_sqlCommandViaMappingNameExplodedThreeMapping

## Mapping Unwrapping

Computes the real mapping at pysical SQL syntax.

    Database.mappingSql(mappingName:String):Result<String>

    test_mapping_unwrapping_man_hour_spent_by_location