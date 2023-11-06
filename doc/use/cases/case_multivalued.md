# multivalued dep

add-database course-trace coursedb

add-table teaching:id,course,book,professor,startdate
add-key teaching:id
add-functional teaching:course-->book
add-functional teaching:course-->professor

Tables:
teaching: id , course , book , professor , startdate

-------------
Constraints:
 teaching.primaryKey of type PRIMARY_KEY
 teaching.functional of type FUNCTIONAL
 teaching.functional of type FUNCTIONAL


compute vertical :

course-trace
20200401_142123_0163000000_db.xml

Tables:
teaching_1: id , course , startdate
teaching_2: course , book , professor

-------------
Constraints:
 teaching.functional of type FUNCTIONAL
 teaching.doubleInclusion of type DOUBLE_INCLUSION
 teaching_2.primaryKey of type PRIMARY_KEY
 teaching_1.primaryKey of type PRIMARY_KEY

CAN'T compute vertical :
    è rimasta teaching.functional of type FUNCTIONAL
    invece di essere promossa a teaching_2.functional of type FUNCTIONAL

atteso
Constraints:
 teaching_2.functional of type FUNCTIONAL course-->book
 teaching_2_teaching_1.doubleInclusion of type DOUBLE_INCLUSION teaching_2:course<->teaching_1.course
 teaching_2.primaryKey of type PRIMARY_KEY course
 teaching_1.primaryKey of type PRIMARY_KEY id

a fronte di una trasformazione
serve la copia automatica
    dei vincoli rimanenti una volta applicata le scomposizione

a fronte di una cancellazione tabella
serve la cancellazione automatica
    dei vincoli che hanno target o source una volta rimossa la tabella di origine

a fronte di una creazione tabella
il puntatore database rimane sul vecchio db
    seppur venga correttamente aggiornato il contenuto file visualizzato in textarea
