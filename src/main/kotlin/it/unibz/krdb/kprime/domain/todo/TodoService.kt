package it.unibz.krdb.kprime.domain.todo

import it.unibz.krdb.kprime.domain.Task
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.support.TimeSupport
import java.io.File
import java.util.*


class TodoService(val settingService: SettingService, private val todoRepo: TodoRepositoryBuilder) {

    private val kpRepoDir = ".kprime/"

    fun all(projectLocation: String):List<Todo> {
        return todoRepo.build(projectLocation + kpRepoDir).findAll()
    }

    fun markdown(projectLocation: String): Result<Unit> {
        val all =  todoRepo.build(projectLocation + kpRepoDir).findAll()
        var goalText = "ID | Title | Assignee | Completed"+System.lineSeparator()
        goalText +=  "|---|---|---|---|"+System.lineSeparator()
        goalText += all.map { "${it.id} | ${it.title} | ${it.assignee} | ${it.completed}" }.joinToString(System.lineSeparator())
        return kotlin.runCatching {
            File(projectLocation+"goals.md").writeText(goalText)
        }
    }

    fun writeAll(projectLocation: String, newTodos: List<Todo>) {
        todoRepo.build(projectLocation + kpRepoDir).saveAll(newTodos)
    }

    fun appendAll(projectLocation:String, newTodos: List<Todo>) {
        val repo = todoRepo.build(projectLocation + kpRepoDir)
        val todos = repo.findAll().toMutableList()
        todos.addAll(newTodos)
        repo.saveAll(todos)
    }

    fun add(projectLocation: String, newTodo: Todo):Result<String> {
        val repo = todoRepo.build(projectLocation + kpRepoDir)
        val todos = repo.findAll()
        val idMax = if (todos.isEmpty()) 0 else todos.maxOf { it.id }
        val todo = Todo(idMax + 1,
                newTodo.title,
                newTodo.completed,
                newTodo.hidden,
                newTodo.key,
                newTodo.dateCreated,
                newTodo.dateOpened,
                newTodo.dateClosed,
                newTodo.dateDue,
                newTodo.priority,
                newTodo.estimate,
                newTodo.partof,
                newTodo.assignee,
                newTodo.isOpened,
                newTodo.isClosed
        )
        todo.resetLabels(newTodo.labelsAsString())
        repo.save(todo)
        return Result.success(todo.gid)
    }

    fun delete(projectLocation: String, todoPos:Int):Boolean {
        val repo = todoRepo.build(projectLocation + kpRepoDir)
        val todoEntity = repo.findAll()[todoPos]
        return repo.delete(todoEntity)
    }

    fun deleteId(projectLocation: String, todoId:Long):Boolean {
        val repo = todoRepo.build(projectLocation + kpRepoDir)
        return repo.delete{it.id == todoId} > 0
    }

    fun getId(projectLocation: String, todoId:Long):Result<Todo> {
        val repo = todoRepo.build(projectLocation + kpRepoDir)
        val todo = repo.findFirstBy { it.id == todoId } ?:
            return Result.failure(IllegalArgumentException("Not Found todo $todoId"))
        return Result.success(todo)
    }

    fun tasks(projectLocation: String): Map<String, Task> {
        val workingDir = projectLocation.ifEmpty { settingService.getWorkingDir() }
        return File(workingDir).walk()
                .filter {it.isFile && isStoryFile(it)}
                .map {findTask(it,projectLocation)}
                .map { it.goalId to it}.toMap()
    }

    private fun findTask(file: File, projectLocation: String): Task {
        var title = ""
        val storyFileName = file.name
        val traceName = if (file.parent == projectLocation) "___"
                        else file.parent.substringAfter(projectLocation).replace("/","___")
        var goalID = UUID.randomUUID().toString()
        var assignee = ""
        val sources = mutableListOf<String>()
        file.readLines().forEach {
            if(it.startsWith("# ")) {title = it.substring(2).trim()}
            if(it.startsWith("+ goal ")) {goalID = it.substring(7).trim()}
            if(it.startsWith("+ actor ")) {assignee = it.substring(8).trim()}
            if(it.startsWith("+ source ")) {sources.add(it.substring(9).trim())}
        }
        return Task(title,traceName,storyFileName,goalID,assignee,sources)

    }

    private fun isStoryFile(it: File) = it.name.endsWith(".md")

    fun update(projectLocation: PrjContextLocation, newGoal: Todo) {
        val repo = todoRepo.build(projectLocation.value + kpRepoDir)
        repo.update(newGoal, { it.id == newGoal.id })
    }

    fun status(projectLocation:PrjContextLocation, referenceDate:Date
               ,daysAlert:Int=7,monthsAlert:Int=0,yearsAlert:Int=0): String {
        val goals = all(projectLocation.value)
        var result = ""
        for (goal in goals) {
            val dueDistance = TimeSupport.distance(referenceDate,goal.dateDue)
            if (dueDistance.toString().contains("-")) result+="Goal ${goal.id} is EXPIRED from ${dueDistance}.\n"
            if (dueDistance.years==0 && dueDistance.months==0 && dueDistance.days<daysAlert && dueDistance.days>0)
                result+="Goal ${goal.id} is in $daysAlert days ALERT ZONE ${dueDistance}.\n"
        }
        if (result.isEmpty()) result = "All ${goals.size} goals are fine."
        if (goals.isEmpty()) result = "There are no goals."
        return result
    }

}