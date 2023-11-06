package it.unibz.krdb.kprime.domain.trace

import it.unibz.krdb.kprime.domain.cmd.read.TraceCmdTransApplicability
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.project.PrjContextName
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.setting.SettingService
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Gateway to command parser. It mantains the context of executions fo the commands.
 * Syncrhonize access and preserve mutable states per user.
 *
 * Every mutation method calls a parser parse command.
 */
class TraceService(val settingService: SettingService, val prjContextService: PrjContextService) {


    fun getProjectTraces(projectLocation:String,traceName: String): List<String> {
        val projectTracesDir = projectLocation + traceName
        println("getProjectTraces [$projectLocation] [$traceName]")
        if (!File(projectTracesDir).isDirectory) return emptyList()
        return listDirNamesInFolder(projectTracesDir)

    }

    fun getProjectTraceFiles(projectLocation:String,traceName: String): List<String> {
        val projectTracesDir = projectLocation + traceName
        println("getProjectTraceFiles [$projectLocation] [$traceName]")
        if (!File(projectTracesDir).isDirectory) return emptyList()
        return listFileNamesInFolder(projectTracesDir)

    }

    fun getTraces(): List<Trace> {
//TODO        val listTracesNames = TraceCmdListTraces.listTracesNames(currentCmdContext("no-author", "no-contextId")) as MutableList
        val listTracesNames = emptyList<String>()
        return listTracesNames.map { name -> nameToTrace(name) }.toMutableList()
    }

    private fun listFileNamesInFolder(dir: String): List<String> {
        return File(dir).listFiles().orEmpty()
                .filter { f -> !f.isDirectory }
                .map { f -> f.name }.toList().sorted()
    }

    private fun listDirNamesInFolder(dir: String): List<String> {
        return File(dir).listFiles().orEmpty()
                .filter { f -> f.isDirectory }
                .map { f -> f.name }.toList().sorted()
    }

    private fun nameToTrace(name: String) = Trace(name, mutableListOf(), mutableListOf())

    fun getTraceDir(): String {
//TODO        return this.contextEnv.currentTrace?:""
        return "no-dir"
    }

    fun getTraceFileNames(traceDir: String): List<String> {
//TODO        contextEnv.currentTrace = traceDir
        val workingTracesDir = settingService.getTraceDir(TraceName(traceDir))
        if (!File(workingTracesDir).isDirectory) return emptyList()
        return listFileNamesInFolder(workingTracesDir)
                .toList()
    }

    // creates a folder if not exists with a story file.
    fun traceGoal(goalName: String): String {
//TODO        return parse("no-author","${TraceCmdTraceGoal.getCmdName()} $goalName").message
        return ""
    }

    // creates a folder if not exists with a database file.
    fun traceSource(sourceName: String): String {
//TODO        return parse("no-author","${TraceCmdTraceSource.getCmdName()} $sourceName").message
        return ""
    }

    fun getTraceTransApplicability(traceName: String): List<TraceCmdTransApplicability.RestTransformApplicability> {
//TODO        return TraceCmdTransApplicability.computeRestTransf(currentCmdContext("no-author", "no-contextId"))
        return emptyList()
    }

    data class RestTransformApplyResult(val ok: Boolean, val message: String)

    fun applyTransformer(
            traceName: String,
            transformerName: String): RestTransformApplyResult {
//TODO        val result = parse("no-author", "${TraceCmdTransApply.getCmdName()} $transformerName decompose")
//        if (result.failure.isNotEmpty()) return RestTransformApplyResult(false, "applyTransformer: " + result.failure)
//        else return RestTransformApplyResult(true, result.message)
        return RestTransformApplyResult(true,"")
    }

    fun deleteTraceDir(contextName: String, traceName: String): Boolean {
        val workingTracesDir = prjContextService.projectByName(contextName)?.location?:""
        if (!File(workingTracesDir).isDirectory) return false
        File("$workingTracesDir$traceName").deleteRecursively()
        return true
    }

    fun deleteTraceFile(contextName: String, traceName: TraceName, traceFileName: String): Boolean {
        val workingTracesDir = prjContextService.projectByName(contextName)?.location?:""
        if (!File(workingTracesDir).isDirectory) return false
        val fileNameToDelete = "$workingTracesDir${traceName.toDirName()}$traceFileName"
        println("deleteTraceFile($fileNameToDelete)")
        val file = File(fileNameToDelete)
        if (!file.exists()) return false
        file.delete()
        return true
    }

    fun putTraceFile(
        projectDir: String,
        traceName: String,
        traceFileName: String,
        fileContent: String
    ) {
        val traceNameWithSeparators = if (traceName=="/" || traceName=="___") ""
            else traceName.replace("___","/")
        if (!File(projectDir + traceNameWithSeparators).exists()) File(projectDir + traceNameWithSeparators).mkdirs()
        val workingTraceFileName = "$projectDir$traceNameWithSeparators/$traceFileName"
        println("---- putTraceFile PUT TRACE DOC $workingTraceFileName")
        println(fileContent)
        File(workingTraceFileName).writeText(fileContent,Charsets.UTF_8)
        println("---- putTraceFile PUT TRACE DOC END")
    }

    fun getStoryNames(traceName: String, projectDir: String = settingService.getWorkingDir()): List<String> {
        //val workingTraceDir = projectDir + SettingService.TRACES_DIR + traceName
        val traceNameOrRoot = if (traceName!="root") traceName else ""
        val workingTraceDir = projectDir + traceNameOrRoot
        println("getStoryNames.workingTraceDir:[$workingTraceDir]")
        return File(workingTraceDir)
                .listFiles{ f -> f.isFile && f.name.endsWith(".md") }.orEmpty()
                .mapNotNull { f->  f.nameWithoutExtension }
    }

    fun getTraceFileContent(
            traceName: String,
            traceFileName: String,
            projectLocation: String  = settingService.getWorkingDir()
    ): String {
        var projectDir = projectLocation
        if (projectDir.isEmpty()) projectDir = settingService.getWorkingDir()
        val traceFileNameWithExtension = if (traceFileName.endsWith("_db.xml")||traceFileName.endsWith(".md")) traceFileName
            else traceFileName+"_db.xml"
        println("getTraceFileContent traceName=[$traceName]")
        println("getTraceFileContent traceFileName=[$traceFileName]")
        println("getTraceFileContent projectLocation=[$projectLocation]")
        //var workingTraceFileName = projectDir + SettingService.TRACES_DIR + traceName + "/" + traceFileNameWithExtension
        var workingTraceFileName = projectDir + traceName + "/" + traceFileNameWithExtension
        workingTraceFileName = workingTraceFileName.replace("___","/")
        return File(workingTraceFileName).readText(Charsets.UTF_8)
    }

    fun setTraceContext(
            traceName: String,
            traceFileName: String,
            content: String) {
//TODO        contextEnv.currentTrace = traceName
//TODO        contextEnv.currentTraceFileName = traceFileName
        if (content.startsWith("<database")) {
            try {
//TODO                this.contextEnv.database = XMLSerializerJacksonAdapter().deserializeDatabase(content)
            } catch (e: Exception) {
                e.printStackTrace()
//TODO                this.contextEnv.database = dataService.newBase()
//TODO                this.contextEnv.changeSet = ChangeSet()
            }
        } else {
//TODO            contextEnv.changeSet = ChangeSet()
        }
    }

    fun getTraceFileLines(
            projectDir: String,
            traceName: String,
            traceFileName: String
            ): List<String> {
        val workingDir = if (projectDir == PrjContext.NO_WORKING_DIR) settingService.getWorkingDir() else projectDir
        val workingTraceFileName = workingDir +  traceName + "/" + traceFileName
        return File(workingTraceFileName).readLines(Charsets.UTF_8)
    }

    fun getsetTraceFileContentLinear(projectLocation: String, traceName: String, traceFileName: String): String {
        val newTraceFileName = if (!traceFileName.contains('.'))
            traceFileName.replace(' ','_')+ ".md"
        else traceFileName
        val workingTraceFileName = projectLocation + traceName + "/" + newTraceFileName
println("getsetTraceFileContentLinear(): new file: $workingTraceFileName")
        //settingService.writeCurrentTrace(traceName)
//TODO        contextEnv.currentTrace = traceName
//TODO        contextEnv.currentTraceFileName = traceFileName
        var result = ""
        val file = File(workingTraceFileName)
println("getsetTraceFileContentLinear(): trace file: $traceName/$traceFileName")
        val readText = if (file.exists()) file.readText(Charsets.UTF_8) else {
            File(workingTraceFileName).writeText("")
            return "" }
        if (readText.startsWith("<database")) {
            try {
//TODO                this.contextEnv.database = XMLSerializerJacksonAdapter().deserializeDatabase(readText)
//TODO                result = parse("no-author","all").message
            } catch (e: Exception) {
                e.printStackTrace()
//TODO                this.contextEnv.database = dataService.newBase()
            }
        } else if (readText.startsWith("<changeSet")) {
            try {
//TODO                contextEnv.changeSet = XMLSerializerJacksonAdapter().deserializeChangeSet(readText)
            } catch (e:Exception) {
                e.printStackTrace()
//TODO                contextEnv.changeSet = ChangeSet()
            }
        }
        else {
//TODO            contextEnv.changeSet = ChangeSet()
        }
        if (result.isEmpty()) result = readText
        return result
    }

    // creates a new file based on current database.
    fun getsetTraceDatabaseContent(traceName: String): String {
        var sourceContent = "no-content"
//TODO        if (this.contextEnv.database.isEmpty()) return sourceContent
        val revision = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_nnnnnnnnnn"))
        val dbName = "revision_${revision}_tracedb.xml"
//TODO        this.contextEnv.database.name = dbName
//TODO        sourceContent = XMLSerializerJacksonAdapter().prettyDatabase(this.contextEnv.database)
        val sourceTraceFileName = settingService.getWorkingDir() + SettingService.TRACES_DIR + "/" + traceName + "/" + dbName
        File(sourceTraceFileName).writeText(sourceContent)
//TODO        return  parse("no-author","all").message
        return ""
    }

    // creates a new file based on current changeset.
    fun getsetTraceChangeSetContent(traceName: String): String {
        var sourceContent = "no-content"
//TODO        if (contextEnv.changeSet.isEmpty()) return sourceContent
        val revision = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddkkmmss"))
        val dbName = "revision_${revision}_tracedb.xml"
//TODO        this.contextEnv.database.name = dbName
//TODO        sourceContent = XMLSerializerJacksonAdapter().prettyDatabase(this.contextEnv.database)
        val sourceTraceFileName = settingService.getWorkingDir() + SettingService.TRACES_DIR + "/" + traceName + "/" + dbName
        File(sourceTraceFileName).writeText(sourceContent)
//TODO        return parse("no-author","all").message
        return ""
    }


    fun setCurrentTrace(traceName: String) {
        // settingService.writeCurrentTrace(traceName)
    }


    // -----------------------------------------------------------------
    // experimental for a tree representation
                interface TreeData
                data class TreeDataWithChildren(val name: String, val children: MutableList<TreeData>) : TreeData

                data class TreeDataName(val name: String) : TreeData

                // experimental tree
                private fun listFileNamesInFolderAsTreeData(rootDir:String, treeData: TreeDataWithChildren) : TreeData {
                    val listDirs = File(rootDir).listFiles().orEmpty().filter { f->f.isDirectory }
                    for (dir in listDirs) {
                        if (dir.listFiles { f -> f.isDirectory }.orEmpty().isNotEmpty())
                            treeData.children.add(listFileNamesInFolderAsTreeData(dir.absolutePath, TreeDataWithChildren(dir.name, mutableListOf())))
                        else
                            treeData.children.add(TreeDataName(dir.name))
                    }
                    return treeData
                }

                // experimental tree
                fun getTracesTree(): TreeData {
                    val workingTracesDir = settingService.getWorkingDir() + SettingService.TRACES_DIR
                    if (!File(workingTracesDir).isDirectory) return TreeDataWithChildren("root", mutableListOf())
                    val treeData = TreeDataWithChildren("Traces", mutableListOf())
                    return listFileNamesInFolderAsTreeData(workingTracesDir,treeData)
                }

    fun getTraceFile(projectName: PrjContextName, traceName: TraceName, traceFileName: TraceFileName): Result<InputStream> {
        val project = prjContextService.projectByName(projectName.value)
            ?: return Result.failure(FileNotFoundException("Project ${projectName.value} not found."))
        return try {
            val inputStream = File( project.location + traceName.toDirName() + traceFileName).inputStream()
            Result.success(inputStream)
        } catch (ex:Exception) {
            Result.failure(ex)
        }
    }


}

