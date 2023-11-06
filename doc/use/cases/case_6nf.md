# Case 6th Normal Form

* Load data from a CSV

        > sql-create-table-from-csv perso /stories/persone.csv

* Or load data from a story note CSV cell

      sql-create-table-from-csv-note : Creates a physical database table starting from data file in CSV format.
      TABLE_NAME : New table name. default:null required:true
      NOTE_FILE_PATH : Note File path to reach the CSV file to load. default:null required:true
      NOTE_INDEX_CSV : Story CSV cell index. default:null required:true

        > sql-create-table-from-csv-note personas stories/facts.md 11


* Or execute a SQL script

        > sql-batch /stories/person.sql

* Display table content

        > select * from perso

* Extracts metadata from table

        > meta-to-db -table=PERSO

* Add table key 

        > add-key PERSO:NOME

* Show 6nf
  
        > sql-show-6nf PERSO

* Show table details

        > table PERSO