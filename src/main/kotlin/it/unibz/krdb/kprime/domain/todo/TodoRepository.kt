package it.unibz.krdb.kprime.domain.todo

interface TodoRepository {
    fun all(projectLocation:String=""): List<Todo>
    fun read(id:String): Todo
    fun write(todo: Todo)
    fun writeAll(todos: List<Todo>)
    fun delete(id:String)
    fun writeAll(workingDir: String, todos: List<Todo>)
}