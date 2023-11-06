# Transformers


## Vertical Composer

**Rules**

originTable1==/database/schema/constraints/constraints[@type='DOUBLE_INCLUSION'][1]/source/@table > 0
originTable2==/database/schema/constraints/constraints[@type='DOUBLE_INCLUSION'][1]/target/@table > 0
lhs==/database/schema/tables/tables[@name='originTable1'][1]/columns/columns/@name
rhs==/database/schema/tables/tables[@name='originTable2'][1]/columns/columns/@name
originTable==/database/schema/tables/tables[@name='originTable2'][1]/@view >0

**Template**

<changeSet id="234">
  <createTable name="${originTable[0]}" id="" view="${originTable[0]}" condition="">
    <columns>
        <#list lhs as att1>
        <columns name="${att1}" id="id.${att1}" dbname="" nullable="false" dbtype=""/>
        </#list>
        <#list rhs as att1>
        <columns name="${att1}" id="id.${att1}" dbname="" nullable="false" dbtype=""/>
        </#list>
    </columns>
  </createTable>
   <dropTable path="" schemaName="" tableName="${originTable2[0]}"/>
   <dropTable path="" schemaName="" tableName="${originTable1[0]}"/>
  <dropConstraint path="" schemaName="" constraintName="${originTable2[0]}.primaryKey"/>
  <dropConstraint path="" schemaName="" constraintName="${originTable[0]}.doubleInclusion"/>
</changeSet>



## Vertical Decomposer

**Rules**

originTable==/database/schema/constraints/constraints[@type='FUNCTIONAL'][1]/source/@table > 0
all==/database/schema/tables/tables[@name='originTable'][1]/columns/columns/@name
keys==/database/schema/constraints/constraints[@type='PRIMARY_KEY'][1]/source[@table='originTable']/columns/columns/@name
lhss==/database/schema/constraints/constraints[@type='FUNCTIONAL'][1]/source[@table='originTable']/columns/columns/@name > 0
rhss==/database/schema/constraints/constraints[@type='FUNCTIONAL'][1]/target[@table='originTable']/columns/columns/@name > 0
rests==- all keys lhss rhss > 0
table==/database/schema/tables/tables[@name='originTable'][1]/@name
view1==+ keys lhss rests
view2==+ lhss rhss


**Template**

<changeSet id="234">
  <createTable name="${table[0]}_1" id="" view="${table[0]}" condition="">
    <columns>
        <#list view1 as att1>
        <columns name="${att1}" id="id.${att1}" dbname="" nullable="false" dbtype=""/>
        </#list>
    </columns>
  </createTable>
  <createTable name="${table[0]}_2" id="" view="${table[0]}" condition="">
    <columns>
        <#list view2 as att2>
        <columns name="${att2}" id="id.${att2}" dbname="" nullable="false" dbtype=""/>
        </#list>
    </columns>
  </createTable>
  <createConstraint name="${table[0]}.doubleInclusion" id="" type="DOUBLE_INCLUSION">
    <source name="" id="" table="${table[0]}_1">
        <columns>
            <#list lhss as lhs>
            <columns name="${lhs}" id="id.${lhs}" dbname="" nullable="false" dbtype=""/>
            </#list>
        </columns>
    </source>
    <target name="" id="" table="${table[0]}_2">
        <columns>
            <#list lhss as lhs>
            <columns name="${lhs}" id="id.${lhs}" dbname="" nullable="false" dbtype=""/>
            </#list>
        </columns>
    </target>
  </createConstraint>
  <createConstraint name="${table[0]}_2.primaryKey" id="" type="PRIMARY_KEY">
    <source name="" id="" table="${table[0]}_2">
        <columns>
            <#list lhss as lhs>
            <columns name="${lhs}" id="id.${lhs}" dbname="" nullable="false" dbtype=""/>
            </#list>
        </columns>
    </source>
    <target name="" id="" table="">
        <columns>
            <columns name="" id="" dbname="" nullable="false" dbtype=""/>
        </columns>
    </target>
  </createConstraint>
<#if keys?size gt 0 >
  <createConstraint name="${table[0]}_1.primaryKey" id="" type="PRIMARY_KEY">
    <source name="" id="" table="${table[0]}_1">
        <columns>
            <#list keys as key>
            <columns name="${key}" id="id.${key}" dbname="" nullable="false" dbtype=""/>
            </#list>
        </columns>
    </source>
    <target name="" id="" table="">
        <columns>
            <columns name="" id="" dbname="" nullable="false" dbtype=""/>
        </columns>
    </target>
  </createConstraint>
</#if>
  <createMapping name="${table[0]}_1">
    <select>
      <attributes>
        <#list view1 as view1col>
        <attributes name="${view1col}" />
        </#list>
      </attributes>
      <from>
        <from tableName="${table[0]}" alias="" joinOn=""/>
      </from>
      <where condition="" />
    </select>
  </createMapping>
  <createMapping name="${table[0]}_2">
    <select>
      <attributes>
        <#list view2 as view2col>
        <attributes name="${view2col}"/>
        </#list>
      </attributes>
      <from>
        <from tableName="${table[0]}" alias="" joinOn=""/>
      </from>
      <where condition="" />
    </select>
  </createMapping>

  <dropTable path="" schemaName="" tableName="${table[0]}"/>
  <dropConstraint path="" schemaName="" constraintName="${table[0]}.primaryKey"/>
  <dropConstraint path="" schemaName="" constraintName="${table[0]}.functional"/>
</changeSet>

## Horizontal Composer

## Horizontal Decomposer

**Rules**

((originTable)),((condition)),((not_condition))
all==/database/schema/tables/tables[@name='originTable'][1]/columns/columns/@name

**Template**

<changeSet id="345">
  <createTable name="${originTable[0]}_1" id="" view="${originTable[0]}" condition="${condition[0]}">
    <columns>
        <#list all as att1>
        <columns name="${att1}" id="id.${att1}" dbname="" nullable="false" dbtype=""/>
        </#list>
    </columns>
  </createTable>
  <createTable name="${originTable[0]}_2" id="" view="${originTable[0]}" condition="${not_condition[0]}">
    <columns>
        <#list all as att1>
        <columns name="${att1}" id="id.${att1}" dbname="" nullable="false" dbtype=""/>
        </#list>
    </columns>
  </createTable>
</changeSet>

## Advanced transformations

It is required a specific engine [1].
You could use an expert like [Expert TPP][2]

## References

[1] TPP Engine
[2] [Expert TPP](experts/expert_spinone.md)