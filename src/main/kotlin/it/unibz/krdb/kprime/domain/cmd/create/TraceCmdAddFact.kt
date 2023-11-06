package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.adapter.fact.FactParser
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.term.Term
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.vocabulary.Vocabulary
import unibz.cs.semint.kprime.domain.db.Database

/**

a- (arrow hierarchy)
has- (rombo)
of- (stereotype label) roleMixin
as- (role)
:dataset-restriction
@ language qualification
one #1
many #0..n
many mandatory #1..n (at-least-one)

entity-type name CamelCase no spaces
relation name lowercase with dash -
role name lowercase
stereotype label name CamelCase with initial lowercase
attributes name CamelCase with initial lowercase

-----------------------------------------------
+ Publisher of-roleMixin a-Agent
one Publisher as-publishers many-mandatory-EventPlan
1 Publisher as publishers "0..n" EventPlan

+ EventPlan of-relator is-NamedIndividual
... has-startDate:TimeInstant
... has-endDate:TimeInstant
... has-capacity:Int .


NamedIndividual in category
... has abstract:String
... has description:String
--- has url:String .

Agent of-category a-NamedIndividual
MediaObject of-category is-NamedIndividual

many-MediaObject as-multimediaDescriptions in-historicalDependency many-NamedIndividual
------------------------------------------------

add-fact Each President was born in at most one Country

Each  -> add-constraint mandatory President was-born-in
President -> add-table if-not-exisits President
was born in -> add-table | add-column was-born-in
at most one -> add-constraint <=1 Country was-born-in
Country -> add-table if-not-exisits Country


attribute label
constraint label, inverse-label
relazioni:
1:0
add-fact Each President was-born-in may-have-one Country

President(Name) was-born-in Country
President may-have-one Country

1:1
add-fact Each President was-born-in with-exactly-once Country
Each President was-born-in Country
-> add-table President: id,Key,Country
Each President has Name
-> add-column President name

Each President has-exactly-once Country
-> add-table President
-> add column Country

add-fact Each Country has exactly-once Name
changeset()
-> add-foreign President -> Country
-> add-table Country
-> add-column Country.name
-> rem-column President.country
-> add-constraint

0:n
add-fact Each President was-born-in may-have-some Countries

1:n
add-fact Each President was-born-in at-least-one Country

n:1
n:m

IS-A
 add-fact concept1 is-a {exclusive|cover|partition}

INVERSE NAVIGATION
 add-fact concept1 born/was-born-in may-have-some Country born/is-born-country
------------------
 v1

add-fact <concept1> <rel> <concept2>
add-fact <concept1> has <attribute>

facts
 ------------------
 v2

add-fact <rel-name>: <card1> <concept1>(key) as <role1>,...
add-fact eating:each Person as eater, some Food as dish
add-fact each Person as eater eating some Food as dish
add-fact Person eating Food

add-fact <concept1>: has <attr>[<type>],..,<attr>[<type>]
add-fact Person has Name, Surname, Age int
add-fact has:each Person as eater, exactly-one Name as name

add-fact <rel-name>: has <attr>
add-fact Food has Name
add-fact eating has TimeEating

add-fact <concept1> has-id <attr>,..,<attr>
add-fact Person has-id Name,Surname

add-fact <concept1> is-a {exclusive|cover|partition} <concept2>,...
add-fact Person is-a cover Student,Teacher

add-fact eater 'Giovanni' 'Pedot' eating 'salmon'
add-fact eater {name:'Giovanni',surname: 'Pedot'} eating {name:'salmon'}

alias as
attribute type, unit
----------------------
cardinalities:

each
exactly one
some
at most one
more than one

 ----------------------
examples

 add-fact Person was-born-in Country
=> add-fact Person-was-born-in-Country: each Person was-born-in, exactly-one Country

add-fact Born: each Person was-born-in, exactly-one Country

add-fact Person has smokes boolean

add-fact Person was-hired Firm on-date-hired DateHiring
=> add-fact Person-was-hired-in-Firm-on-DateHiring: each Person was-hired-in, some Firm, exactly-one DateHiring on-date-hired

 */

open class TraceCmdAddFact: TraceCmd {
    override fun getCmdName(): String {
        return "add-fact"
    }

    override fun getCmdDescription(): String {
        return "Adds two concepts with one relation to current database."
    }

    override fun getCmdUsage(): String {
        return "adds-fact <concept1> <rel-c1-c2> <concept2>"
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.CONCEPTUAL,
            TraceCmd.Topic.DATABASE).joinToString()
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        return parseFactCommand(context, command)
    }

    private fun parseFactCommand(
        context: CmdContext,
        command: String
    ): TraceCmdResult {
        val database = context.env.database
        val tokens = command.trim().split(" ")
        val lastToken = tokens.last()
        if (tokens.last().trim() == ":") return TraceCmdResult() options completeWithTablesOrTerms(
            context,
            database,
            command
        )
        if (command.endsWith("?:")) return TraceCmdResult() message hintOf(lastToken, context)
        if (command.endsWith(":"))
            return TraceCmdResult() options completeWith(
                termsFromProjectsOrVocabularies(
                    lastToken,
                    context
                ).map { it.name }, command
            )
//        tokens.drop(1).forEach {
//            require(TraceCmd.isValidArgument(it, TraceCmd.QNAME_PATTERN)) { "Argument '$it' not valid." }
//        }
        val knownPrefixes = context.pool.termService.readInstanceVocabularies().map { it.prefix }
        val unknownPrefixes = Vocabulary.checkUnknownPrefixes(tokens, knownPrefixes)
        if (unknownPrefixes.isNotEmpty()) return TraceCmdResult() failure "Not found prefix " + unknownPrefixes.joinToString()
        val tokensMap =
            FactParser.parseAsTripletMap(command) ?: return TraceCmdResult() failure "Required at least two arguments."
        var subj = tokensMap[FactParser.TripletRole.SUBJECT] ?: error("No subject")
        if (subj == "...")
            subj = context.env.currentElement ?: error("No subject")
        if (subj.isEmpty()) error("subject empty")
        val changeSet = context.env.changeSet
        val result = FactParser.parseFact(command,subj,changeSet,database)
        context.env.currentElement = result.payload as String
        return result
    }

    private fun completeWithTableNames(database: Database, command: String): List<String> {
        return database.schema.tables?.map { t -> command + t.name } ?: emptyList()
    }

    private fun completeWithTerms(context: CmdContext, command: String): List<String> {
        val traceName = context.env.currentTrace
        val traceFileName = context.env.currentTraceFileName ?: return emptyList()
        val terms = context.pool.termService.getAllTerms(traceName = traceName,traceFileName = traceFileName, PrjContext.NO_PrjContext.location)
        return terms.map {  command + it.name }
    }

    private fun completeWithTablesOrTerms(context: CmdContext, database: Database, command: String): List<String> {
        val tablesAndTerms = mutableListOf<String>()
        tablesAndTerms.addAll(completeWithTableNames(database,command))
        tablesAndTerms.addAll(completeWithTerms(context,command))
        return tablesAndTerms
    }

    private fun hintOf(lastToken: String, context: CmdContext): String {
        val prefix = lastToken.substringBefore(":")+":"
        val termName = lastToken.substringAfter(":").dropLast(2)
        val listTerms = termsFromProjectsOrVocabularies(prefix,context)
        val term = listTerms.firstOrNull { it.name == termName }
        return termToHint(term)
    }

    private fun termToHint(term: Term?) =
            if (term != null) "${term.url} ${term.description}" else ""

    private fun completeWith(listTerms: List<String>, command: String): List<String> {
        return listTerms.map { "$command$it" }
    }

    private fun termsFromProjectsOrVocabularies(prefix: String, context: CmdContext): List<Term> {
        var terms = projectTerms(prefix, context)
        val traceName = context.env.currentTrace
        if (terms.isEmpty() ) {
            terms = context.pool.termService.getVocabularyTerms(traceName,prefix)?: emptyList()
        }
        return terms
                //context.pool.termService.getVocabularyMap()[lastToken]?:

    }

    private fun projectTerms(prefix: String, context: CmdContext): List<Term> {
        val projectName = prefix.dropLast(1) // a prefix is a project ending with char ':'
        val project = context.pool.prjContextService.projectByName(projectName)?: return emptyList()
        return context.pool.termService.getAllTerms(TraceName(project.activeTrace), project.activeTermBase,project.location)
    }

}