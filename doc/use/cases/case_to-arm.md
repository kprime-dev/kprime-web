# TO ARM

From a denormalized table to an abstract relational model tables.

create table0:

        drop table if exists table0
        create table table0(SSN varchar(64), Phone varchar(64), Name varchar(64), DepName varchar(64), DepAddress varchar(64))
        insert into table0 values('SSN1', 'Phone1', 'Name1', 'DepName1', 'DepAddress1')
        insert into table0 values('SSN2', 'Phone2', 'Name2', 'DepName2', 'DepAddress2')
        insert into table0 values('SSN3', 'Phone3', 'Name3', 'DepName2', 'DepAddress2')
        insert into table0 values('SSN4', 'Phone4', 'Name4', NULL, NULL)

        select * from table0

        -----------------------------------------------------SSN: SSN1
        PHONE: Phone1
        NAME: Name1
        DEPNAME: DepName1
        DEPADDRESS: DepAddress1
        -----------------------------------------------------SSN: SSN2
        PHONE: Phone2
        NAME: Name2
        DEPNAME: DepName2
        DEPADDRESS: DepAddress2
        -----------------------------------------------------SSN: SSN3
        PHONE: Phone3
        NAME: Name3
        DEPNAME: DepName2
        DEPADDRESS: DepAddress2
        -----------------------------------------------------SSN: SSN4
        PHONE: Phone4
        NAME: Name4
        DEPNAME: null
        DEPADDRESS: null


split MVD SSN->>Phone:

        add-cs-table table1:SSN,Phone
        add-cs-table table2:SSN,Name,DepName,DepAddress
        add-cs-key table1:SSN,Phone
        add-cs-double-inc table1:SSN<->table2:SSN
        del-cs-table table0
        add-cs-mapping table1 select SSN,Phone from table0
        add-cs-mapping table2 select SSN,Name,DepName,DepAddress from table0

        changeset-apply

        select table1

        -----------------------------------------------------SSN: SSN1
        PHONE: Phone1
        -----------------------------------------------------SSN: SSN2
        PHONE: Phone2

        select table2

        -----------------------------------------------------SSN: SSN1
        NAME: Name1
        DEPNAME: DepName1
        DEPADDRESS: DepAddress1
        -----------------------------------------------------SSN: SSN2
        NAME: Name2
        DEPNAME: DepName2
        DEPADDRESS: DepAddress2

split FD SSN-->Name:

        add-cs-table table3:SSN,Name
        add-cs-table table4:SSN,DepName,DepAddress
        add-cs-key table1:SSN,Phone
        add-cs-key table3:SSN
        add-cs-key table4:SSN
        add-cs-double-inc table3:SSN<->table4:SSN
        add-cs-double-inc table4:SSN<->table4:SSN
        del-cs-table table2
        del-cs-mapping table2
        add-cs-mapping table3 select SSN,Name from table0
        add-cs-mapping table4 select SSN,DepName,DepAddress from table0

        select table 3

        -----------------------------------------------------SSN: SSN1
        NAME: Name1
        -----------------------------------------------------SSN: SSN2
        NAME: Name2

        select table 4

        -----------------------------------------------------SSN: SSN1
        DEPNAME: DepName1
        DEPADDRESS: DepAddress1
        -----------------------------------------------------SSN: SSN2
        DEPNAME: DepName2
        DEPADDRESS: DepAddress2
        


split H DepName not null:

        add-cs-table table5:SSN
        add-cs-table table6:SSN,DepName,DepAddress

        add-cs-key table5:SSN
        add-cs-key table6:SSN
        add-cs-double-inc table3:SSN<->table5:SSN
        add-cs-double-inc table3:SSN<->table6:SSN
        add-cs-double-inc table1:SSN<->table5:SSN
        add-cs-double-inc table1:SSN<->table6:SSN

        del-cs-table table4
        del-cs-mapping table4

        add-cs-mapping table5 select SSN from table0 where DepName is null and DepAddress is null
        add-cs-mapping table6 select SSN,DepName,DepAddress from table0 where DepName is not null and DepAddress is not null

        changeset-apply

        select table5

        empty no null value

        select table6

        -----------------------------------------------------SSN: SSN1
        DEPNAME: DepName1
        DEPADDRESS: DepAddress1
        -----------------------------------------------------SSN: SSN2
        DEPNAME: DepName2
        DEPADDRESS: DepAddress2


split FD SSN-->DepName:

        add-cs-table table7:SSN,DepName
        add-cs-table table8:DepName,DepAddress

        add-cs-key table7:SSN
        add-cs-key table8:DepName
        add-cs-double-inc table3:SSN<->table7:SSN
        add-cs-double-inc table1:SSN<->table7:SSN
        add-cs-double-inc table3:SSN<->table8:SSN
        add-cs-double-inc table1:SSN<->table8:SSN

        del-cs-table table6
        del-cs-mapping table6

        add-cs-mapping table7 select SSN,DepName from table0 where DepName is null and DepAddress is null
        add-cs-mapping table8 select DepName,DepAddress from table0 where DepName is not null and DepAddress is not null

        changeset-apply

        select table7

        is empty

        select table8

        -----------------------------------------------------DEPNAME: DepName1
        DEPADDRESS: DepAddress1
        -----------------------------------------------------DEPNAME: DepName2
        DEPADDRESS: DepAddress2


POID e DOID:

        add-cs-table table9:POID,SSN,Name
        add-cs-table table10:DOID,DepName,DepAddress
        add-cs-mapping table9 select SSN as POID, SSN, Name from table0
        add-cs-mapping table10 select DepName as DOID, DepName, DepAddress from table0 where DepName is not null

        changeset-apply

        select table9

        -----------------------------------------------------POID: SSN1
        SSN: SSN1
        NAME: Name1
        -----------------------------------------------------POID: SSN2
        SSN: SSN2
        NAME: Name2

        select table10

        -----------------------------------------------------DOID: DepName1
        DEPNAME: DepName1
        DEPADDRESS: DepAddress1
        -----------------------------------------------------DOID: DepName2
        DEPNAME: DepName2
        DEPADDRESS: DepAddress2
        -----------------------------------------------------DOID: DepName2
        DEPNAME: DepName2
        DEPADDRESS: DepAddress2


RENAMING:

        rename-mapping table1 poid-phone
        rename-mapping table2 poid-name
        rename-mapping table3 poid-dept
        rename-mapping table10 doid-address
        