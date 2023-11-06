# Repository

interface Repository<T> {
    fun save(entity: T)
    fun saveAll(entities: List<T>)
    fun findBy(criteria: (T) -> Boolean): T?
    fun findPage(page: Page): List<T>
    fun findAll(): List<T>
    fun delete(entity: T)
    fun deleteAll()
    fun countAll(): Int
    fun countPages(page: Page): Int
}

interface ContextRepository<T> : Repository<T> {
    val repoDir:String
    val repoFileName:String
}

open class JsonFileContextRepository<T> (override val repoDir: String, override val repoFileName: String)
: ContextRepository<T> {


ActorRepositoryBuilder
    build(location: String): ContextRepository<Actor>

ActorService(ProjectService,ActorRepositoryBuilder)


## Working dir .kprime

* Repository in instance
    - prjContext
    - setting
    - expert
    - user
    - driver

* Repository in context and instance
    + source
    + vocabulary

* Repository in context
      + actor
      + term
      + todo

* in Service
  
     readInstanceVocabularies
     writeInstanceVocabularies

     readContextVocabularies
     writeContextVocabularies

    