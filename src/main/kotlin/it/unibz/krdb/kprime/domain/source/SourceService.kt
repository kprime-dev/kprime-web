package it.unibz.krdb.kprime.domain.source

import it.unibz.krdb.kprime.domain.exception.ItemNotFoundException
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.project.PrjContextName
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.trace.ScopedName
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.datasource.DataSourceConnection

class SourceService(
    val settingService: SettingService,
    val prjContextService: PrjContextService,
    private val sourceRepositoryBuilder: SourceRepositoryBuilder,
    private val driverRepositoryBuilder: DriverRepositoryBuilder
)
    :DriverServices by DriverService(settingService, driverRepositoryBuilder){

    fun readContextSources(contextName:String): List<Source> {
        val context = prjContextService.projectByName(contextName) ?: return emptyList()
        return sourceRepositoryBuilder.build(context.location+".kprime/").findAll()
    }

    fun writeContextSources(contextName: String, sources: List<Source>) {
        val context = prjContextService.projectByName(contextName) ?: return
        return sourceRepositoryBuilder.build(context.location+".kprime/").saveAll(sources)
    }

    fun addContextSource(contextName: String, source: Source) {
        val context = prjContextService.projectByName(contextName) ?: return
        return sourceRepositoryBuilder.build(context.location+".kprime/").save(source)
    }

    fun remContextSource(contextName: String, sourceName: SourceName) {
        val context = prjContextService.projectByName(contextName) ?: return
        sourceRepositoryBuilder.build(context.location+".kprime/").delete{ it.name == sourceName.getValue() }
    }

    fun readInstanceSources(): List<Source> {
        return sourceRepositoryBuilder.build(settingService.getInstanceDir()).findAll()
    }

    fun writeInstanceSources(sources: MutableList<Source>) {
        sourceRepositoryBuilder.build(settingService.getInstanceDir()).saveAll(sources)
    }

    fun addInstanceSource(source: Source) {
        sourceRepositoryBuilder.build(settingService.getInstanceDir()).save(source)
    }

    fun dropInstanceSource(name:String) {
        sourceRepositoryBuilder.build(settingService.getInstanceDir()).delete { it.name == name }
    }

    fun getInstanceSourceByName(name:String): Source? {
        val allInstanceSources = readInstanceSources()
        return allInstanceSources.firstOrNull { s -> s.name == name }
    }

    fun getInstanceDataSourceByName(name:String): DataSource? {
        val source = readInstanceSources().firstOrNull { s -> s.name == name }
            ?: return null
        return newWorkingDataSource(source)
    }

    fun getContextSourceByName(prjContext: PrjContextName, name:String): Source? {
        return readContextSources(prjContext.value).firstOrNull { s -> s.name == name }
    }

    fun getContextDataSourceByName(prjContext: PrjContextName, name:String): DataSource? {
        val source = readContextSources(prjContext.value).firstOrNull { s -> s.name == name }
            ?: return null
        return newWorkingDataSource(source)
    }

    fun getSourceByScopedId(prjContextName: PrjContextName, scopedSourceId: ScopedName): Result<Source> {
        if (scopedSourceId.isContextScope()) {
            val source = readContextSources(prjContextName.value).firstOrNull { s -> s.id == scopedSourceId.getName() }
                ?: return Result.failure(ItemNotFoundException("Source ${scopedSourceId.getName()} not found."))
            return Result.success(source)
        } else {
            val source = readInstanceSources().firstOrNull { s -> s.id == scopedSourceId.getName() }
                ?: return Result.failure(ItemNotFoundException("Source ${scopedSourceId.getName()} not found."))
            return Result.success(source)
        }
    }

    fun newWorkingDataSource(datasourceName: String): DataSource? {
        val source = getInstanceSourceByName(datasourceName)?: return null
        return newWorkingDataSource(source)
    }

    fun newWorkingDataSource(prjContext: PrjContext, datasourceName: String): DataSource? {
        val source = getContextSourceByName(PrjContextName(prjContext.name),datasourceName)?: return null
        return newWorkingDataSource(prjContext.location, source)
    }

    fun newWorkingDataSourceOrH2(datasourceName: String): DataSource {
        val h2MemDataSource = DataSource("h2", "mem_db", "org.h2.Driver", "jdbc:h2:mem:mem_db", "sa", "")
        h2MemDataSource.connection = DataSourceConnection("mem_db","sa","", autocommit = true, commited = true, closed = false)
        val source = getInstanceSourceByName(datasourceName)
                ?: return h2MemDataSource
        return newWorkingDataSource(source)
    }

    private fun newWorkingDataSource(source: Source): DataSource {
        val workingDir = settingService.getWorkingDir()
        val workingDataSourcePath = source.location.replace("~/", workingDir)
        return DataSource(
            source.type,
            source.name,
            source.driver,
            workingDataSourcePath,
            source.user,
            source.pass,
            source.driverUrl
        )
    }

    private fun newWorkingDataSource(workingDir:String, source: Source): DataSource {
        val workingDataSourcePath = source.location.replace("~/", workingDir)
        return DataSource(
            source.type,
            source.name,
            source.driver,
            workingDataSourcePath,
            source.user,
            source.pass,
            source.driverUrl
        )
    }


}