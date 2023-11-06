# Database Normalization

https://www.studytonight.com/dbms/database-normalization.php
https://docs.microsoft.com/it-it/office/troubleshoot/access/database-normalization-description

Database Normalization is a technique of organizing the data in the database. Normalization is a systematic approach of decomposing tables to eliminate data redundancy(repetition) and undesirable characteristics like Insertion, Update and Deletion Anomalies. It is a multi-step process that puts data into tabular form, removing duplicated data from the relation tables.

Normalization is used for mainly two purposes,

    Eliminating redundant(useless) data.
    Ensuring data dependencies make sense i.e data is logically stored.


## Problems Without Normalization

If a table is not properly normalized and have data redundancy then it will not only eat up extra memory space but will also make it difficult to handle and update the database, without facing data loss. Insertion, Updation and Deletion Anomalies are very frequent if database is not normalized. To understand these anomalies let us take an example of a employee table.

create table Employee (rollno int,	name varchar(50),	branch	varchar(50),hod	varchar(50),office_tel varchar(20))
insert into Employee values(401, 'Akon', 'CSE' ,'Mr. X' ,'53337')

### Insertion Anomaly

Suppose for a new admission, until and unless an employee opts for a branch, data of the employee cannot be inserted, or else we will have to set the branch information as NULL.

Also, if we have to insert data of 100 employees of same branch, then the branch information will be repeated for all those 100 employees.

These scenarios are nothing but Insertion anomalies.

### Updation Anomaly

What if Mr. X leaves the college? or is no longer the HOD of computer science department? In that case all the employee records will have to be updated, and if by mistake we miss any record, it will lead to data inconsistency. This is Updation anomaly.

### Deletion Anomaly

In our employee table, two different informations are kept together, employee information and Branch information. Hence, at the end of the academic year, if employee records are deleted, we will also lose the branch information. This is Deletion anomaly.


## First Normal Form (1NF)

For a table to be in the First Normal Form, it should follow the following 4 rules:

   1. It should only have single(atomic) valued attributes/columns.
   2. Values stored in a column should be of the same domain
   3. All the columns in a table should have unique names.
   4. And the order in which data is stored, does not matter.


## Second Normal Form (2NF)

For a table to be in the Second Normal Form,

   1. It should be in the First Normal form.
   2. And, it should not have Partial Dependency.

So all I need is id and every other column depends on it, or can be fetched using it.
This is Dependency and we also call it Functional Dependency.

Partial Dependency, where an attribute in a table depends on only a part of the primary key and not on the whole key.

## Third Normal Form (3NF)

A table is said to be in the Third Normal Form when,

   1. It is in the Second Normal form.
   2. And, it doesn't have Transitive Dependency.

 Transitive Dependency. When a non-prime attribute depends on other non-prime attributes rather than depending upon the prime attributes or primary key.



## Boyce and Codd Normal Form (BCNF)

Boyce and Codd Normal Form is a higher version of the Third Normal form. This form deals with certain type of anomaly that is not handled by 3NF. A 3NF table which does not have multiple overlapping candidate keys is said to be in BCNF. For a table to be in BCNF, following conditions must be satisfied:

    R must be in 3rd Normal Form
    and, for each functional dependency ( X → Y ), X should be a super Key.


 for a dependency A → B, A cannot be a non-prime attribute, if B is a prime attribute.


## Fourth Normal Form (4NF)

A table is said to be in the Fourth Normal Form when,

    It is in the Boyce-Codd Normal Form.
    And, it doesn't have Multi-Valued Dependency.

