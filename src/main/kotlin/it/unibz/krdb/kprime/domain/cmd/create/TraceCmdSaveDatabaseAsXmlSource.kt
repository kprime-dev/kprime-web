package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.source.Source
import it.unibz.krdb.kprime.domain.source.SourceService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.Database
import java.io.File
import java.util.*

object TraceCmdSaveDatabaseAsXmlSource : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "save-database-as-source"
    }

    override fun getCmdDescription(): String {
        return "Save current database as XML source."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "write,database,logical,source"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database: Database = context.env.database
        val sourcesDir: String = context.pool.settingService.getSourcesDir()
        val sourceService: SourceService = context.pool.sourceService
        val sourceContent = context.pool.dataService.prettyDatabase(database)
        val sourceTraceFileName = sourcesDir + "/" + database.name
        File(sourceTraceFileName).writeText(sourceContent)
        val source = Source(UUID.randomUUID().toString(),"xml",database.name,"",sourceTraceFileName,"","")
        sourceService.addInstanceSource(source)
        return TraceCmdResult() message "Db ${database.name} saved as source."
    }

}

