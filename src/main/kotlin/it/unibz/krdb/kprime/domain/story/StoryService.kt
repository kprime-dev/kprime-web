package it.unibz.krdb.kprime.domain.story

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.exception.ItemNotFoundException
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.project.PrjContextName
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.trace.TraceFileName
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.trace.TraceService
import it.unibz.krdb.kprime.support.substring
import it.unibz.krdb.kprime.view.controller.StoryController
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.FSDirectory
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Value
import java.io.File
import java.io.FileReader
import java.net.URLEncoder
import java.nio.file.Paths
import javax.ws.rs.BadRequestException

class StoryService(val settingService: SettingService,
                   val prjContextService: PrjContextService,
                   val traceService: TraceService
) {

    companion object {
        private const val STORY_INDEX_DIR = "storyindex/"

        internal fun toFileContentWithImages(fileContent: String,projectName: String): String {
            var newFileContent = ""
            var remainFileContent = fileContent
            while (remainFileContent.contains("![")) {
                newFileContent += remainFileContent.substringBefore("](") + "]("
                val linkUrl = remainFileContent.substringAfter("](").substringBefore(")")
                newFileContent += toKPrimeURL(linkUrl,projectName) + ")"
                remainFileContent = remainFileContent.substringAfter(")")
            }
            newFileContent += remainFileContent
                return newFileContent
        }

        private fun toKPrimeURL(linkUrl: String, projectName: String): String {
            val dir =
                if (linkUrl.contains("/"))
                linkUrl.substringBeforeLast("/").replace("/","___")
                else "___"
            val fileName = linkUrl.substringAfterLast("/")
            return "/project/$projectName/file/$dir/$fileName"
        }

        internal fun computeMarkdownFrom(notes: List<StoryController.Note>): String {
            var result = ""
            for (note in notes) {
                result += note.title
                if (note.commandResult.isNotEmpty()) {
                    result += "\n[result:${note.commandResult.removeSuffix("\n")}]]"
                }
                if (note.commandFailure.isNotEmpty()) {
                    result += "\n[failure:${note.commandFailure.removeSuffix("\n")}]]"
                }
                if (!result.endsWith("---\n")) result += "\n\n---\n"
                //if (!result.endsWith("\n")) result += "\n"
                // adds a cell separator

            }
            return result
        }

        internal fun notesFromMarkdown(
            markdown: String,
            edit: Boolean
        ): MutableList<StoryController.Note> {
            val lines = markdown.split("\n")
            val notebook = mutableListOf<StoryController.Note>()
            var lineId = 1
            var noteContent = ""
            var result = ""
            var failure = ""
            for (line in lines) {
                if (line.startsWith("---")) {
                    //noteContent += line //+ System.lineSeparator()
                    noteContent = noteContent.removeSuffix("\n\n")
                    if (noteContent.removeSuffix("\n").isNotEmpty()) {
                        noteContent = noteContent.removePrefix("\n")
                        notebook.add(StoryController.Note(lineId, noteContent, "??", false, result, failure))
                        noteContent = ""
                        result = ""
                        failure = ""
                    }
                } else if (line.startsWith("#")) {
                    noteContent = noteContent.removeSuffix("\n\n")
                    if (noteContent.removeSuffix("\n").isNotEmpty()) {
                        noteContent += System.lineSeparator()
                        notebook.add(StoryController.Note(lineId, noteContent, "", false, result, failure))
                        noteContent = ""
                        result = ""
                        failure = ""
                    }
                    notebook.add(StoryController.Note(lineId, line, "!!!", false, result, failure))
                } else if (line.contains("[result:") && edit) {
                    result = line.substring("[result:","]]")
                } else if (line.contains("[failure:") && edit) {
                    failure = line.substring("[failure:","]]")
                } else if (line.contains("[[[") && !edit) {
                    noteContent += parseAnchor(line) + System.lineSeparator()
                } else {
                    noteContent += line + System.lineSeparator()
                    //println(noteContent)
                }
                lineId++
            }
            noteContent = noteContent.removeSuffix(System.lineSeparator())
            if (noteContent.isNotEmpty()) {
                notebook.add(StoryController.Note(lineId, noteContent, "xx", false, result, failure))
            }
            return notebook
        }

        private fun parseAnchor(line: String): String {
            val prefix = line.substringBefore("[[[")
            val postfix = line.substringAfter("]]]")
            val anchor = line.substringAfter("[[[").substringBefore("]]]")
            val label = anchor.substringBefore("|")
            val target = anchor.substringAfter("|")
            return "$prefix[$label](/noteview.html?$target)$postfix"
        }

    }


    fun addStory(contextName: PrjContextName, traceName: TraceName, storyName: String, templateName: String): Result<String> {
        // copy file
        // from setting.templateDir + templateName (extension included)
        println("StoryService.addStory BEGIN")
        val templateFileName = settingService.getTemplatesDir() + templateName
        val extension = templateName.substringAfterLast(".")
        // to contextName + trace + storyName + extension
        val contextLocation = prjContextService.projectByName(contextName.value)?.location
            ?: throw IllegalArgumentException("contextName not found")
        val targetFileName = computeTargetFileName(contextLocation, traceName, templateName, extension)
        val targetFile = File(targetFileName)
        return kotlin.runCatching {
            println("StoryService.addStory [$templateFileName] to [$targetFileName]")
            File(templateFileName).copyTo(targetFile, overwrite = true);
            println("StoryService.addStory END")
            targetFileName
        }
    }

    private fun computeTargetFileName(
        contextLocation: String,
        traceName: TraceName,
        templateName: String,
        extension: String
    ): String {
        var fileExists = true
        val templateNameNoExtension = templateName.substringBeforeLast(".")
        var fileNameTarget = ""
        var inc = 0
        do {
            fileNameTarget = contextLocation+ traceName.toDirName() + "${templateNameNoExtension}_${(inc++)}.$extension"
            if (!File(fileNameTarget).exists()) fileExists = false
        } while (fileExists)
        return fileNameTarget
    }

    fun renameProjectTraceFile(projectName: PrjContextName, traceName: String, oldName:String, newName:String): Result<String> {
        val projectLocation = prjContextService.projectByName(projectName.value)?.location
            ?: throw IllegalArgumentException("contextName not found")
        val projectTracesDir = "$projectLocation$traceName/"
        println("getProjectTraceFileRename [$projectLocation] [$traceName] [$oldName] [$newName]")
        var newDir = ""
        var newFileName = ""
        if (oldName=="___") {
            val newFileNameToCreate = "$projectLocation$traceName$newDir$newName"
            println(" renameProjectTraceFile AS NEW:[$newFileNameToCreate]")
            File(newFileNameToCreate).createNewFile()
            return Result.success("New file $newName created.")
        }
        if (newName.contains('/')) {
            newDir = newName.substringBeforeLast('/')
            newFileName = newName.substringAfterLast('/')
            println("StoryService: mkdir [$projectLocation$traceName/$newDir] newFile [$newFileName]")
            prjContextService.makeProjectDir("$projectLocation$traceName/$newDir")
            File("$projectLocation$traceName/$newDir/$newFileName").createNewFile()
        }
        println("renameProjectTraceFile:[$projectLocation] [$traceName] [$oldName] [$newFileName]")
        return kotlin.runCatching {
            File(projectTracesDir + oldName).renameTo(File(projectTracesDir + newName))
            projectTracesDir+newName
        }
    }

    fun listTrace(
        projectLocation: PrjContextLocation,
        traceName: String,
        context: CmdContext
    ): Result<String> {
        println(" list stories `${projectLocation.value}` traceName : `$traceName`")
        val projectTraceFiles = context.pool.traceService.getProjectTraceFiles(projectLocation.value, traceName)
        val projectTraces = context.pool.traceService.getProjectTraces(projectLocation.value, traceName)
        var result = "Ok. stories. " + System.lineSeparator()
        result += projectTraces.joinToString("/" + System.lineSeparator()) + "/" + System.lineSeparator()
        result += projectTraceFiles.joinToString(System.lineSeparator())
        return Result.success(result)
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
        println("getStoryFileContent traceName=[$traceName]")
        println("getStoryFileContent traceFileName=[$traceFileName]")
        println("getStoryFileContent projectLocation=[$projectLocation]")
        var workingTraceFileName = projectDir + traceName + "/" + traceFileNameWithExtension
        workingTraceFileName = workingTraceFileName.replace("___","/")
        return File(workingTraceFileName).readText(Charsets.UTF_8)
    }

    fun getDocumentMd(
        projectName: PrjContextName,
        traceName: TraceName,
        traceFileName: TraceFileName
    ): Result<String> {
        val projectLocation = prjContextService.projectByName(projectName.value)?.location ?: ""
        println("StoryService.getDocumentMd():${traceName.toDirName()}")
        val traceFileContent = traceService.getTraceFileContent(traceName.toDirName(), traceFileName.getFileName(), projectLocation)
        return Result.success(traceFileContent)
    }

    sealed interface StoryServiceError {
        data class ContextNameEmpty(val mes:String): StoryServiceError, IllegalStateException()
        data class ProjectUnknown(val mes:String, val wrongName:String): StoryServiceError, IllegalArgumentException(mes)
        data class LocationIsEmpty(val mes:String) : StoryServiceError, IllegalAccessError()
        data class BadRequest(val mes:String) : StoryServiceError, BadRequestException()
    }

    fun putDocumentMd(
        projectName: PrjContextName,
        traceName: TraceName,
        traceFileName: String,
        fileContent: String
    ) : Result<Boolean> {
        val project = prjContextService.projectByName(projectName.value)
        if (project==null) {
            return Result.failure(StoryServiceError.ProjectUnknown("", projectName.value))
        } else {
            try {
                traceService.putTraceFile(project.location, traceName.toDirName(), traceFileName, fileContent)
            } catch (exception: Exception) {
                println("--------------- PUT DOC SC_BAD_REQUEST ${exception.localizedMessage}")
                exception.printStackTrace()
                return Result.failure(StoryServiceError.BadRequest(exception.localizedMessage))
            }
        }
        return Result.success(true)
    }


    fun readNotes(
        projectName: PrjContextName,
        traceName: TraceName,
        traceFileName: TraceFileName,
        edit: Boolean,
        noteIndex: Int = -1
    ): Result<MutableList<StoryController.Note>> {
        val project = prjContextService.projectByName(projectName.value) ?: return Result.failure(IllegalArgumentException("Project not found."))
        val fileContent =
            traceService.getsetTraceFileContentLinear(project.location, traceName.toDirName(), traceFileName.getFileName())
        // val fileContentWithImages = toFileContentWithImages(fileContent,projectName) TODO ON Browser
        val notebook = notesFromMarkdown(fileContent, edit)
        return if (noteIndex>=0)
            if (noteIndex > notebook.size) Result.failure(ItemNotFoundException("There are ${notebook.size} cells."))
            else Result.success(mutableListOf(notebook[noteIndex]))
        else Result.success(notebook)
    }

    fun writeNotes(
        projectName: PrjContextName,
        traceName: TraceName,
        traceFileName: TraceFileName,
        edit: Boolean,
        noteIndex: Int = -1,
        notes: List<StoryController.Note>
    ): Result<Boolean> {
        val project = prjContextService.projectByName(projectName.value) ?: return Result.failure(IllegalArgumentException("Project not found."))
        val fileContent = computeMarkdownFrom(notes)
        traceService.putTraceFile(project.location, traceName.toDirName(), traceFileName.getFileName(), fileContent)
        return Result.success(true)
    }

    fun getStoryIndexDir() : String {
        return settingService.getInstanceDir()+ STORY_INDEX_DIR
    }

    fun indexAllStories(updated: Boolean,storyIndexDir: String= getStoryIndexDir()) {
        if (File(storyIndexDir).walk().none() || updated) {
            val projects = prjContextService.readAllProjects()
            for (project in projects) {
                indexStories(project.location, storyIndexDir, updated)
            }
        }
    }

    fun indexStories(workingDir: String, storyIndexDir: String, updated: Boolean) {
        File(workingDir).walk()
            .filter { it.isFile && isFileToGrep(it) }
            .map { file -> indexText(file, storyIndexDir, updated) }
            .joinToString()

    }

    private fun isFileToGrep(file: File) = file.name.endsWith(".md")

    private fun indexText(fileToIndex:File, indexDirName:String, update:Boolean): String {
        val indexDir = FSDirectory.open(Paths.get(indexDirName))
        val analyzer = StandardAnalyzer()
        val indexWriterConfig = IndexWriterConfig(analyzer)
        val indexWriter = IndexWriter(indexDir, indexWriterConfig)
        val document = Document()
        val fileReader = FileReader(fileToIndex)
        document.add( TextField("contents", fileReader))
        document.add( StringField( "mod_date" , DateTools.timeToString(fileToIndex.lastModified(), DateTools.Resolution.DAY), Field.Store.YES))
        document.add( StringField("path", fileToIndex.path, Field.Store.YES))
        document.add( StringField("filename", fileToIndex.name, Field.Store.YES))
        val indexedFileName = if (update) {
            indexWriter.updateDocument(Term("path", fileToIndex.path), document)
            fileToIndex.absolutePath
        } else {
            indexWriter.addDocument(document)
            fileToIndex.absolutePath
        }
        indexWriter.close()
        return indexedFileName
    }

    fun findText(workingDir: String, termQuery: String, indexDirName: String = getStoryIndexDir()): List<Pair<Document, Float>> {
        if (File(indexDirName).walk().none()) return  emptyList()
        val indexDir = FSDirectory.open(Paths.get(indexDirName))
        val analyzer = StandardAnalyzer()
        val indexReader = DirectoryReader.open(indexDir)
        val indexSearcher = IndexSearcher(indexReader)
        if (termQuery.isEmpty()) return emptyList()
        val query = QueryParser("contents", analyzer).parse(termQuery)
        val topTenDocs = indexSearcher.search(query, 10)
        return topTenDocs.scoreDocs
            .map { Pair(indexSearcher.doc(it.doc),it.score)  }
    }

    fun findTextAsString(workingDir: String, findResult: List<Pair<Document, Float>>):String {
        return findResult.joinToString(System.lineSeparator()) { (doc,score) -> "[${doc.get("path")}](${computeStoryUrl(doc.get("path"),workingDir)}) $score" }
    }

    fun computeStoryUrl(storyFilePath:String,workingDir: String): String {
        val docFileName = URLEncoder.encode(storyFilePath.substringAfterLast("/") , "utf-8")
        val storyTraceDir = storyFilePath.removePrefix(workingDir).substringBeforeLast("/")
        val storyProject = workingDir.dropLast(1).substringAfterLast("/").lowercase()
        val storyTraceDirUrlized = storyTraceDir.replace("/","___")
        return "/noteview.html?pr=$storyProject&tr=$storyTraceDirUrlized&tf=$docFileName"
    }

    fun execJs(jsFun:String):Result<String> {
        try {
            Context.create().use { context ->
                println("[[$jsFun]]")
                val value: Value = context.eval("js", jsFun)
                return Result.success(value.execute(22, 4555).asString())
            }
        } catch (e: Exception) { return Result.failure(e) }
    }
}