package it.unibz.krdb.kprime.mock

import it.unibz.krdb.kprime.domain.Repository
import it.unibz.krdb.kprime.domain.todo.Todo
import it.unibz.krdb.kprime.domain.todo.TodoRepositoryBuilder

class TodoRepositoryBuilderMock: TodoRepositoryBuilder {
    private val repo = RamRepository<Todo>()
    override fun build(location: String): Repository<Todo> {
        return repo
    }

}