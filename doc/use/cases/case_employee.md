# Case Employee

## Symbols Legend

      🌑 - to start
      🌒
      🌓
      🌔
      🌕 - completed
      🔴 - to fix
      🔨 - work in progress

## Steps

1. create context

           🌕 > add-context case_employee

2. add metadata

           🌕 > set-context case_employee -license=MIT
           🌕 > set-context case_employee -location=</home/.../>
           🌕 > add-label :case_employee kp:company 'Informatelier'

3. use new context

           🌕 > properties-set context=case_employee

4. add readme page

           🌕 > add-doc readme.md readme1

5. add data source

   🌕 use a temporary csv file in context

           🌕 > sql-create-table-from-csv table0 data/table0.csv
           🌕 > use-source mem_db

   🌓 or use a csv file in context 

           🌑 > add-source emp -csv=emp:csv/table0.csv
           🌕 > use-source emp

   🌑 or add a csv cell of readme file

           🌑 > use -page=readme.md -cell=table0
           🌑 > add-csv-row readme.md table0 (SSN, Name,Phone,DepName,DepAddress)
           🌑 > add-csv-row readme.md table0 ('SSN1','nome1','telefono','dipartimento','indirizzo_dipartimento')
           🌑 > add-csv-row readme.md table0 ('SSN2','nome2','telefono2','dipartimento','indirizzo_dipartimento')

6. add logic constraints

           🌕 > add-key table0:SSN
           🌑 > add-nullable table0:Phone
           🌕 > add-double-fd table0:DepName<->DepAddress (to test)
           🌑 > add-nullable table0:DepName

7. add semantic annotation

           🌕 > add-mapping department select DepName,DepAddress from table0
           🌕 > add-mapping person select SSN,Name,Phone from table0
           🌕 > add-mapping employee select * from person, department where DepName is not null
8.

           🌑 > add-fact person is-a table0:SSN, Name, Phone
              🌑 > add-fact person has-key table0:SSN 
              🌑 > add-fact person has-one table0:Name
              🌑 > add-fact person has-zero-or-many table0:Phone
           🌑 > add-fact department is-a table0:DepName,DepAddress
              🌑 > add-fact department has-one-unique table0:DepName as "Department Name"
              🌑 > add-fact department has-one-unique table0:DepAddress as "Department Name"
           🌑 > add-fact person works-in-one department
              🌑 > add-fact employee is-covering person where department is-not-null 
              🌑 > add-fact employee is-a person where department is-not-null 


           🌕 > add-label :person ontouml:stereotype ontouml:kind
           🌕 > add-label :department ontouml:stereotype ontouml:kind
           🌕 > add-label :employee ontouml:stereotype ontouml:subkind

8. view data

           🌕 > select person
           🌕 > select department
           🌕 > select employee

           select SSN,Name,Phone from table0 where DepName is not null

9. insert data

           🌑 > insert person gigi,proietti

           insert into table0(nome,cognome) values ('gigi','proietti') 

10. diff data

           🌑 > diff table0 person

11. sql schema

           🌑 > sql-schema person

12. expose data

           🌑 > expose-data public person

13. expose schema

           🌑 > expose-schema public person

14. browse pages

           🌑 > expose-pages public person
