package it.unibz.krdb.kprime.domain.project

import it.unibz.krdb.kprime.domain.exception.ItemNotFoundException
import it.unibz.krdb.kprime.domain.setting.SettingService
import java.io.File

class PrjContextService(val settingService: SettingService, prjContextRepositoryBuilder: PrjContextRepositoryBuilder) {

    private val prjContextRepository = prjContextRepositoryBuilder.build(settingService.getInstanceDir())

    fun readAllProjects():List<PrjContext> {
        return prjContextRepository.findAll()
    }

    fun writeAllProjects(prjContexts:List<PrjContext>) {
        prjContextRepository.saveAll(prjContexts)
    }

    fun addProject(prjContext: PrjContext):Result<PrjContext> {
        val allProjects = readAllProjects()
        val idMax = if (allProjects.isEmpty()) 0 else allProjects.maxOf { it.id }
        val newContext = prjContext.copy( id = idMax + 1 )
        prjContextRepository.save(newContext)
        return Result.success(newContext)
    }

    fun remProject(prjName: String):Result<Int> {
        val nrDeleted = prjContextRepository.delete { it.name == prjName }
        if (nrDeleted==0) return Result.failure(ItemNotFoundException())
        return Result.success(nrDeleted)
    }

    fun projectByGid(projectGid: String): PrjContext? {
        if (projectGid.isEmpty()) return null
        return prjContextRepository.findFirstBy { p -> p.gid == projectGid }
    }

    fun projectByGids(projectGids: Set<String>): List<PrjContext> {
        if (projectGids.isEmpty()) return emptyList()
        return prjContextRepository.findByCriteria { p -> projectGids.contains(p.gid) }
    }

    // TODO change from String to ProjectName
    fun projectByName(projectName: String): PrjContext? {
        if (projectName.isEmpty()) return null
        return prjContextRepository.findFirstBy { p -> p.name == projectName }
    }

    fun projectByLocation(projectLocation: String): PrjContext? {
        if (projectLocation.isEmpty()) return null
        return prjContextRepository.findFirstBy { p -> p.location == projectLocation }
    }

    fun update(prjContext: PrjContext) {
        val projects = readAllProjects().toMutableList()
        projects.removeIf{ it.name == prjContext.name }
        projects.add(prjContext)
        writeAllProjects(projects)
    }

    fun makeProjectDir(newDir: String) {
        File(newDir).mkdirs()
    }
}