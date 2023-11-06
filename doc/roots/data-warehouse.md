# Data Warehouse

Un data warehouse è un sistema di archiviazione dati centralizzato che viene utilizzato per l'analisi dei dati. I data warehouse sono progettati per archiviare grandi quantità di dati da diverse fonti, come sistemi transazionali, database relazionali e file di testo. I dati vengono quindi trasformati e consolidati in un formato che è facile da analizzare.

I data warehouse sono utilizzati da un'ampia gamma di aziende e organizzazioni per prendere decisioni informate. Ad esempio, un'azienda potrebbe utilizzare un data warehouse per analizzare le vendite per identificare le tendenze e i modelli. Un'organizzazione sanitaria potrebbe utilizzare un data warehouse per analizzare i dati dei pazienti per migliorare la qualità dell'assistenza.

Esistono due tipi principali di data warehouse:

    Enterprise data warehouse (EDW): Un EDW è un data warehouse centralizzato che viene utilizzato da un'intera organizzazione.
    Data mart: Un data mart è un data warehouse più piccolo che viene utilizzato per un'unità specifica di un'organizzazione.

Può essere visto come una grande banca dati in sola lettura (schema on read), utile quindi ad analisi storiche, 
ovvero senza le usuali operazioni di CRUD tipiche delle banche dati relazionali operazionali (schema on write). 
Nell'ambito dell'analisi multidimensionale OLAP il sottoinsieme del DW è detto invece data mart.

I data warehouse sono costituiti da tre componenti principali:

    Trasformazione: trasformazione dei dati: è il livello che si occupa di acquisire i dati e validarli;
    Data layer: Il data layer è il repository di dati che contiene i dati archiviati nel data warehouse.
    Application layer: L'application layer è il software che viene utilizzato per accedere ai dati nel data warehouse.
    Presentation layer: Il presentation layer è il software che viene utilizzato per visualizzare i risultati dell'analisi dei dati.

I data warehouse sono un componente importante di un sistema di business intelligence (BI). I sistemi BI consentono alle aziende di raccogliere, analizzare e visualizzare i dati per prendere decisioni informate.

I data warehouse possono essere un investimento significativo per le aziende, ma offrono una serie di vantaggi che possono aiutare le aziende a migliorare le proprie prestazioni.

Le caratteristiche tecniche di un data warehouse sono le seguenti:

    Orientamento al soggetto: I dati in un data warehouse sono organizzati in base al soggetto, ovvero al tema o all'area di attività che rappresentano. Questo consente agli utenti di accedere ai dati in modo coerente e di ottenere informazioni utili per prendere decisioni informate.
    Integrazione dei dati: I dati in un data warehouse provengono da una varietà di fonti, come sistemi operativi, applicazioni, database e file. I dati vengono integrati in un unico repository, che consente agli utenti di avere una visione completa dei dati aziendali.
    Non volatilità: I dati in un data warehouse sono conservati in modo permanente, in modo da poter essere analizzati in qualsiasi momento. Questo consente agli utenti di utilizzare i dati storici per identificare tendenze e modelli.
    Tempo storico: I dati in un data warehouse sono archiviati per un periodo di tempo significativo, in modo da poter essere analizzati in prospettiva. Questo consente agli utenti di identificare cambiamenti nel comportamento dei clienti o dei dipendenti.
    Scalabilità: I data warehouse devono essere in grado di scalare per soddisfare le esigenze di un'organizzazione in crescita. Questo può essere fatto utilizzando architetture distribuite o cloud-based.

Gli elementi costitutivi dell'architettura sono:

    I dati provenienti dai sistemi transazionali: sono quell'insieme di dati elaborati dai sistemi transazionali dell'azienda. Essi possono essere contenuti all'interno della stessa banca dati, provenienti da diverse banche dati o anche esterni all'azienda. Spesso l'architettura di un data warehouse prevede l'integrazione dei dati interni con quelli esterni. L'utilizzo di questi ultimi consente di arricchire il patrimonio informativo.
    Il data movement: tale componente è responsabile dell'estrazione dei dati dai sistemi transazionali, dell'integrazione tra dati aziendali e dati esterni, del pre-processing dei dati, del controllo della consistenza dei dati, della conversione delle strutture dati, e dell'aggiornamento dei dizionari dei dati.
    Il data warehouse: i dati estratti dagli archivi transazionali vengono memorizzati internamente al data warehouse. Nel data warehouse l'accesso ai dati è consentito in sola lettura. Tali dati hanno una dimensione storica e sono riferiti a soggetti d'impresa. Essi possono essere memorizzati in un archivio centrale o in un data mart. Il termine data mart identifica un data warehouse di dimensioni ridotte, specializzato per una particolare area di attività. Si pensi, ad esempio, al data mart per il marketing, in cui i dati filtrati dagli archivi transazionali sono memorizzati per consentire l'analisi della clientela. All'interno della banca possono quindi esistere più data mart, aventi finalità diverse e orientati a coprire diverse aree d'impresa. I dati contenuti nel data warehouse possono essere aggregati e indicizzati per rispondere a specifiche necessità informative.
    I metadati: i metadati costituiscono informazione aggiuntiva che arricchisce i dati contenuti nel data warehouse. Spesso essi vengono chiamati in gergo "data about data", indicando la provenienza, l'utilizzo, il valore o la funzione del dato. A tale proposito vengono costituiti dei veri e propri information catalog. Questi ultimi sono i file che contengono i metadati. Il catalog consente di spiegare all'utente la natura dei dati nel data warehouse, il loro significato semantico, da quali archivi essi provengono e la loro storicità.
    L'utente finale: i dati contenuti nel data warehouse vengono presentati all'utente finale, il quale dispone di un insieme di strumenti per effettuare elaborazioni e produrre informazioni appropriate. Gli strumenti a disposizione dell'utente possono essere semplici generatori di query e report, interfacce grafiche che consentono la rappresentazione dei dati o sistemi di analisi dati più complessi.


## Kimball Inmon

Kimball e Inmon sono due approcci comuni alla progettazione di data warehouse. Entrambi gli approcci hanno i loro vantaggi e svantaggi, e la scelta del giusto approccio dipende dalle esigenze specifiche di un'organizzazione.

Approccio Kimball

L'approccio Kimball è un approccio bottom-up alla progettazione di data warehouse. Inizia con le esigenze degli utenti e costruisce un modello di dati che soddisfa quelle esigenze. L'approccio Kimball utilizza un modello dimensionale, che è un modello di dati che organizza i dati in una serie di dimensioni e fatti. Le dimensioni sono attributi che descrivono i fatti, mentre i fatti sono misure che descrivono le attività aziendali.

Vantaggi dell'approccio Kimball

    Facilità di comprensione: L'approccio Kimball è relativamente facile da capire e implementare.
    Flessibilità: L'approccio Kimball è flessibile e può essere adattato a una varietà di esigenze.
    Efficacia: L'approccio Kimball è efficace per soddisfare le esigenze degli utenti.

Svantaggi dell'approccio Kimball

    Redundanza dei dati: L'approccio Kimball può portare a una certa ridondanza dei dati, poiché i dati vengono archiviati più volte in dimensioni diverse.
    Difficoltà di manutenzione: L'approccio Kimball può essere difficile da mantenere, poiché i modelli dimensionali devono essere aggiornati man mano che le esigenze degli utenti cambiano.

Approccio Inmon

L'approccio Inmon è un approccio top-down alla progettazione di data warehouse. Inizia con la definizione di un modello di dati coerente che possa essere utilizzato per soddisfare le esigenze di un'intera organizzazione. L'approccio Inmon utilizza un modello relazionale, che è un modello di dati che organizza i dati in una serie di tabelle.

Vantaggi dell'approccio Inmon

    Efficacia: L'approccio Inmon è efficace per soddisfare le esigenze di un'intera organizzazione.
    Riduzione della ridondanza dei dati: L'approccio Inmon può ridurre la ridondanza dei dati, poiché i dati vengono archiviati una sola volta in tabelle separate.
    Facilità di manutenzione: L'approccio Inmon è relativamente facile da mantenere, poiché i modelli relazionali sono più semplici dei modelli dimensionali.

Svantaggi dell'approccio Inmon

    Difficoltà di comprensione: L'approccio Inmon può essere difficile da capire e implementare.
    Flessibilità limitata: L'approccio Inmon può essere meno flessibile dell'approccio Kimball, poiché è più difficile adattarlo a esigenze specifiche.

Tabella di confronto

Caratteristica	|Approccio Kimball	                                |Approccio Inmon
Orientamento	|Bottom-up	                                        |Top-down
Modello di dati	|Dimensionale	                                    |Relazionale
Vantaggi	    |Facilità di comprensione, flessibilità, efficacia	|Efficacia, riduzione della ridondanza dei dati, facilità di manutenzione
Svantaggi	    |Redundanza dei dati, difficoltà di manutenzione    |Difficoltà di comprensione, flessibilità limitata




Bill Inmon, ampiamente considerato come il padre del data warehousing, definisce un data warehouse come:

“Una raccolta di dati orientata al soggetto, non volatile e variabile nel tempo a sostegno delle decisioni della direzione”
(http://en.wikipedia.org/wiki/Bill_Inmon)
Star schema

Ralph Kimball (http://en.wikipedia.org/wiki/Ralph_Kimball), un architetto pioniere del data warehousing, ha sviluppato 
la metodologia di “modellazione dimensionale” ora considerata come lo standard de-facto nell’area del supporto alle decisioni.
Il modello dimensionale (chiamato “schema a stella”) è diverso dalla metodologia di “modellazione normalizzata” di Inman 
(talvolta chiamata “schema a fiocco di neve”). 
Nello Star Schema di Kimball, i dati transazionali sono partizionati in “fatti” aggregati con “dimensioni” referenziali 
che circondano e forniscono descrittori che definiscono i fatti. Il modello normalizzato (3NF o “terza forma normale”) 
memorizza i dati in “tabelle” correlate seguendo le regole di progettazione dei database relazionali stabilite da E. F. Codd e Raymond F. Boyce nei primi anni ’70 che eliminano la ridondanza dei dati.


## Data Mart

Un data mart è un sottoinsieme di un data warehouse che è progettato per soddisfare le esigenze di un particolare gruppo di utenti o di un'unità aziendale. I data mart sono spesso utilizzati per supportare l'analisi dei dati in un particolare dominio, come le vendite, il marketing o la finanza.

I data mart sono tipicamente più piccoli e più semplici dei data warehouse. Questo perché sono progettati per 
soddisfare le esigenze di un gruppo specifico di utenti, invece di essere utilizzati da un'intera organizzazione. 
I data mart sono spesso creati da un processo di estrazione, trasformazione e caricamento (ETL). 
In questo processo, i dati vengono estratti da una o più fonti, trasformati in un formato coerente e 
caricati nel data mart.

I data mart sono una componente importante di un sistema di business intelligence (BI). I sistemi BI consentono alle aziende di raccogliere, analizzare e visualizzare i dati per prendere decisioni informate.

Ecco alcuni esempi di data mart:

    Un data mart per le vendite potrebbe contenere dati sulle vendite di prodotti, clienti e canali di vendita.
    Un data mart per il marketing potrebbe contenere dati sui clienti, sulle campagne di marketing e sui risultati delle campagne.
    Un data mart per la finanza potrebbe contenere dati sui bilanci, sui rendimenti degli investimenti e sui rischi finanziari.

La progettazione di un data mart è un processo che coinvolge una serie di passaggi, tra cui:

    Definizione degli obiettivi: Il primo passo è definire gli obiettivi del data mart. Quali sono le esigenze degli utenti che il data mart deve soddisfare? Quali sono le domande che il data mart deve essere in grado di rispondere?
    Raccolta dei dati: Il secondo passo è raccogliere i dati che verranno memorizzati nel data mart. Questi dati possono provenire da una varietà di fonti, come sistemi operativi, applicazioni, database e file.
    Modellazione dei dati: Il terzo passo è modellare i dati nel data mart. Questo processo consiste nel definire la struttura dei dati e le relazioni tra i dati.
    Implementazione del data mart: Il quarto passo è implementare il data mart. Questo processo consiste nel creare le strutture fisiche per memorizzare i dati e le applicazioni per accedere ai dati.
    Test del data mart: Il quinto passo è testare il data mart. Questo processo consiste nel verificare che il data mart soddisfi gli obiettivi e le esigenze degli utenti.

Ecco una descrizione più dettagliata di ciascun passaggio:

Definizione degli obiettivi

Il primo passo nella progettazione di un data mart è definire gli obiettivi del data mart. Questi obiettivi dovrebbero essere definiti in collaborazione con gli utenti che utilizzeranno il data mart. Gli obiettivi dovrebbero essere specifici, misurabili, raggiungibili, rilevanti e limitati nel tempo.

Raccolta dei dati

Il secondo passo nella progettazione di un data mart è raccogliere i dati che verranno memorizzati nel data mart. Questi dati possono provenire da una varietà di fonti, come sistemi operativi, applicazioni, database e file. La raccolta dei dati può essere un processo complesso e dispendioso in termini di tempo.

Modellazione dei dati

Il terzo passo nella progettazione di un data mart è modellare i dati nel data mart. Questo processo consiste nel definire la struttura dei dati e le relazioni tra i dati. La modellazione dei dati è un processo importante che può influire sulla performance, sulla scalabilità e sulla facilità d'uso del data mart.

Implementazione del data mart

Il quarto passo nella progettazione di un data mart è implementare il data mart. Questo processo consiste nel creare le strutture fisiche per memorizzare i dati e le applicazioni per accedere ai dati. L'implementazione del data mart può essere eseguita utilizzando una varietà di tecnologie, come database relazionali, database multidimensionali e software di data warehouse.

Test del data mart

Il quinto passo nella progettazione di un data mart è testare il data mart. Questo processo consiste nel verificare che il data mart soddisfi gli obiettivi e le esigenze degli utenti. Il test del data mart può essere eseguito utilizzando una varietà di tecniche, come test di unità, test di integrazione e test di sistema.

Ecco alcuni suggerimenti per la progettazione di un data mart efficace:

    Partire dalle esigenze degli utenti: Il data mart deve essere progettato per soddisfare le esigenze degli utenti. Ascolta attentamente le esigenze degli utenti e assicurati che il data mart sia in grado di rispondere a queste esigenze.
    Utilizzare un approccio coerente: Utilizza un approccio coerente alla progettazione del data mart. Questo renderà più facile la manutenzione e l'espansione del data mart in futuro.
    Pianificare per l'espansione: Progetta il data mart in modo da poter essere espanso in futuro. Questo ti consentirà di soddisfare le esigenze crescenti degli utenti.

La progettazione di un data mart è un processo importante che può portare a un sistema di analisi dei dati efficace. Seguendo i passaggi e i suggerimenti sopra riportati, puoi creare un data mart che soddisfi le esigenze della tua organizzazione.

## BEAM model [1]

Secondo il BEAM model dimensionale, un data warehouse è un repository di dati strutturati in modo dimensionale, 
progettato per supportare l'analisi dei dati. I dati sono organizzati in dimensioni e fatti, che consentono agli analisti di interrogarli in modo efficiente.

Nel BEAM model dimensionale, le dimensioni rappresentano gli attributi di un fatto. Ad esempio, la dimensione "cliente" potrebbe includere gli attributi "nome", "indirizzo" e "codice postale". I fatti rappresentano le misurazioni o le attività che si verificano in un sistema. Ad esempio, il fatto "vendita" potrebbe includere i misurazioni "quantità venduta", "prezzo di vendita" e "data di vendita".


## Data Vault

l Data Vault è una metodologia ibrida di modellazione dei dati che fornisce una rappresentazione storica dei dati da più fonti,
progettata per essere resiliente ai cambiamenti ambientali. Originariamente concepito nel 1990 e rilasciato nel 2000
come metodologia di modellazione di pubblico dominio, Dan Linstedt, il suo creatore, descrive un database Data Vault risultante come:

“Un dettaglio orientato, tracciamento storico e un insieme univocamente collegato di tabelle normalizzate che
supportano una o più aree funzionali di business. È un approccio ibrido che comprende il meglio della razza tra
3NF e Star Schemas. Il design è flessibile, scalabile, coerente e adattabile ai bisogni dell’impresa.”
(http://en.wikipedia.org/wiki/Data_Vault_Modeling)


Il modello Data Vault è composto da tre tipi di tabelle di base:

Il data vaultHUB (blu): contenente una lista di chiavi di business uniche con una propria chiave surrogata. I metadati che descrivono l’origine della chiave aziendale, o la “fonte” del record, sono anche memorizzati per tracciare dove e quando i dati hanno avuto origine.

LNK (rosso): che stabilisce relazioni tra chiavi aziendali (tipicamente hub, ma i collegamenti possono collegarsi ad altri collegamenti); essenzialmente descrivono una relazione molti-a-molti. I collegamenti sono spesso usati per gestire i cambiamenti nella granularità dei dati, riducendo l’impatto dell’aggiunta di una nuova chiave aziendale a un hub collegato.

SAT (giallo): tenere attributi descrittivi che possono cambiare nel tempo (simile a una dimensione Kimball di tipo II che cambia lentamente). Dove gli Hub e i Link formano la struttura del modello di dati, i Satelliti contengono attributi temporali e descrittivi, compresi i metadati che li collegano alle loro tabelle Hub o Link. Gli attributi di metadati all’interno di una tabella Satellite che contengono una data di validità del record e una data di scadenza forniscono potenti capacità storiche che permettono di effettuare query che possono andare “indietro nel tempo”.


il Data Vault eccelle quando si tratta di enormi insiemi di dati, diminuendo i tempi di ingestione e 
consentendo inserzioni parallele che sfruttano la potenza dei sistemi Big Data.


Il Data Vault definisce essenzialmente l’ontologia di un’impresa in quanto descrive il dominio del business e le 
relazioni al suo interno. L’elaborazione delle regole di business deve avvenire prima di popolare uno Star Schema. 
Con un Data Vault è possibile spingerle a valle, dopo l’ingestione dell’EDW. Un’ulteriore filosofia di Data Vault 
è che tutti i dati sono rilevanti, anche se sono sbagliati. Dan Linstedt suggerisce che i dati sbagliati sono un 
problema di business, non un problema tecnico.

I progetti Data Vault hanno cicli di rilascio brevi e controllati e possono consistere in un rilascio di produzione 
ogni 2 o 3 settimane, adottando automaticamente i progetti ripetibili, coerenti e misurabili previsti al livello 5 di CMMI.

## Anchor Modeler [5] 



is a database modeling tool for creating database models that handles temporalization using the sixth normal form.

Anchor Modeling is an Open Source database modeling technique built on the premise that the environment surrounding 
a data warehouse is in constant change. A large change on the outside of the model will result in a small change within. 
The technique incorporates the natural concepts of objects, attributes and relationships making it easy to 
use and understand. It is based on the sixth normal form, resulting in a highly decomposed implementation, which 
avoids many of the pitfalls associated with traditional database modeling. Thanks to its modular nature the technique 
supports separation of concerns and simplifies project scoping. You can start small with prototyping and later grow 
into an enterprise data warehouse without having to redo any of your previous work.

Even though its origins were the requirements found in data warehousing it is a generic modeling approach, 
also suitable for other types of systems. Every change is implemented as an independent non-destructive extension 
in the existing model. As a result all current applications will remain unaffected. Changes in the input to and 
output from the database can thereby be handled asynchronously, and all versions of an application can be run against 
the same evolving database. Any previous version of the database model still exists as a subset within an Anchor Model.

A fixed model can rarely survive for any longer periods of time. At some point a change will occur that could not have
been foreseen, and if the initial effort was to create an all-encompassing model you may now be facing dramatic 
alterations to cope with the new situation. Anchor Modeling is built upon the assumption that perfect predictions 
never can be made. Database models should not be built to last, they should be built to change. Only then can they 
truly last in an ever changing environment.


## Data Mesh

Un data mesh è un'architettura di dati decentralizzata che organizza i dati in base al loro dominio, ovvero al tema o all'area di attività che rappresentano. I dati vengono gestiti dai team di dominio, che sono responsabili della loro qualità, integrità e disponibilità.

I data mesh si basano su quattro principi fondamentali:

    Proprietà del dominio: I dati sono di proprietà dei team di dominio, che sono responsabili della loro gestione.
    Data as a product: I dati vengono trattati come prodotti, con un ciclo di vita simile a quello di un prodotto software.
    Self-service: I team di dominio hanno accesso a strumenti e risorse per accedere e utilizzare i dati in modo indipendente.
    Architettura distribuita: I dati vengono archiviati e gestiti in modo distribuito, in modo da essere più accessibili agli utenti.

I data mesh offrono una serie di vantaggi, tra cui:

    Aumento dell'agilità: I data mesh rendono più facile per le aziende adattarsi ai cambiamenti del mercato.
    Migliore qualità dei dati: I data mesh migliorano la qualità dei dati, poiché i team di dominio sono responsabili della loro gestione.
    Riduzione dei costi: I data mesh possono ridurre i costi di gestione dei dati.

I data mesh sono un approccio relativamente nuovo alla gestione dei dati, ma sta rapidamente guadagnando popolarità. Le aziende che adottano i data mesh possono migliorare l'agilità, la qualità e l'efficienza della loro gestione dei dati.




I data mesh possono essere implementati utilizzando una varietà di tecnologie, tra cui:

    Database relazionali
    Database NoSQL
    Software di data lake
    Software di data warehouse

Il data mesh si basa su quattro principi principali: 

1. Proprietà del dominio

     Coloro che conoscono meglio i dati hanno la responsabilità di renderli prontamente disponibili affinché i loro colleghi possano utilizzarli e accedervi come ritengono opportuno. I proprietari di domini si consultano con i potenziali consumatori per individuare i requisiti relativi ai dati e garantire che le esigenze aziendali siano soddisfatte. Rimangono responsabili della protezione del modello del dominio interno dalla sovraesposizione e da livelli inaccettabili di accoppiamento da parte di parti esterne in modo che sia il modello interno che il prodotto dei dati esterni possano evolversi e cambiare in modo indipendente.

2. I dati come prodotto

     I dati aziendali importanti devono essere disponibili in modo rapido e affidabile come primitive di elementi costitutivi per le tue applicazioni, indipendentemente dal runtime, dall'ambiente o dalla base di codice dell'applicazione. Possiamo raggiungere questo obiettivo concentrandoci sulla creazione dei dati come prodotto, trattandoli come cittadini di prima classe, completi di proprietà dedicata, garanzie minime di qualità, SLA e meccanismi scalabili per un accesso pulito e affidabile.

     I prodotti dati fungono da elementi costitutivi di base per la composizione dei servizi aziendali, consentendo ai consumatori di accedere e utilizzare i dati per i propri casi d'uso.

3. Governo federato

     La governance federata si preoccupa di mantenere la stabilità e l’ordine, bilanciando l’autonomia individuale e il controllo centralizzato dall’alto verso il basso.

     Un team di governance, composto da coloro che partecipano al data mesh, ha il potere di soddisfare le esigenze dei proprietari di domini, dei creatori di prodotti dati, degli utenti di prodotti dati e dei fornitori di infrastrutture. Come ogni forma di governo efficace, il gruppo di governance ha bisogno di partecipazione, rappresentanza, dibattito e azione collaborativa per portare a termine un lavoro significativo. Il team di governance guida i requisiti per la piattaforma self-service, concentrandosi su una maggiore rilevabilità, facilità d'uso e intercompatibilità con altri prodotti dati.

     Una governance federata di successo si traduce in prodotti dati facili da creare, gestire e utilizzare. I proprietari dei prodotti dati dovrebbero avere opzioni ragionevoli per creare e gestire i propri prodotti dati nella piattaforma self-service. Dovrebbero inoltre essere dotati di guardrail per supportare i necessari requisiti aziendali non funzionali, come la crittografia, i controlli di accesso e la gestione automatizzata della conservazione dei dati.

4. Piattaforma self-service

     Proprio come ogni implementazione della rete dati sarà unica, così sarà la sua piattaforma self-service. Gli obiettivi principali della creazione di una piattaforma self-service includono la possibilità di:

         Sfoglia, scopri e cerca tra i prodotti dati disponibili

         Concedi, limita e gestisci i controlli di accesso

         Richiedi componenti di elaborazione, archiviazione e gestione, inclusi flussi di eventi

         Gestisci il ciclo di vita del prodotto dati, comprese la prototipazione, la pubblicazione, la deprecazione e l'eliminazione

Per costruire una piattaforma self-service è necessario lavorare a stretto contatto con i proprietari dei prodotti 
dati e con il team di governance federato. La tua migliore possibilità di successo implica iniziare con un prodotto 
minimo praticabile costituito da tecnologie e sistemi di controllo che stai già utilizzando. Tratta la tua piattaforma 
dati self-service come qualsiasi altro prodotto, aggiungendo e testando in modo iterativo nuove funzionalità man mano 
che i modelli e i requisiti di utilizzo diventano più chiari.


## Data Lake

Un data lake è un repository di dati non strutturati, semistrutturati e strutturati, archiviati in formato nativo. I data lake sono progettati per archiviare dati di qualsiasi tipo e dimensione, provenienti da una varietà di fonti.

I data lake offrono una serie di vantaggi, tra cui:

    Agilità: I data lake consentono alle aziende di archiviare dati di qualsiasi tipo e dimensione, provenienti da una varietà di fonti. Questo rende le aziende più agili e in grado di adattarsi ai cambiamenti del mercato.
    Scalabilità: I data lake sono scalabili, in modo da poter crescere con le esigenze dell'azienda.
    Trasparenza: I data lake consentono alle aziende di archiviare i dati in formato nativo, in modo da poter essere analizzati senza bisogno di trasformazioni.

I data lake sono spesso utilizzati per supportare le attività di analisi dei dati, come l'apprendimento automatico e l'intelligenza artificiale. I data lake possono essere utilizzati anche per archiviare dati storici, che possono essere utilizzati per identificare tendenze e modelli.

Ecco alcuni esempi di dati che possono essere archiviati in un data lake:

    Dati strutturati: Dati che sono organizzati in tabelle, come i dati di un database relazionale.
    Dati semistrutturati: Dati che sono organizzati in un formato strutturato, ma che possono contenere elementi non strutturati, come i dati di un file JSON.
    Dati non strutturati: Dati che non sono organizzati in un formato strutturato, come i dati di un file di testo o di un'immagine.


## References

[1] Agile Data Warehouse Design, 2011,  Lawrence Corr , Jim Stagnitto
[2] Data Vault Hubs, Links, and Satellites With Associated Loading Patterns - Making Data Meaningful.pdf 2020
    https://makingdatameaningful.com/data-vault-hubs-links-and-satellites-with-associated-loading-patterns/
[3] The Data Warehouse Toolkit: The Definitive Guide to Dimensional Modeling, 3rd Edition, 2013, Ralph Kimball, Margy Ross
[4] Building a Scalable Data Warehouse with Data Vault 2.0, 2015, Daniel Linstedt, Michael Olschimke
[5] https://www.anchormodeling.com/
    https://github.com/roenbaeck/anchor
[6] Data Mesh, 2022, Zhamak Dehghani