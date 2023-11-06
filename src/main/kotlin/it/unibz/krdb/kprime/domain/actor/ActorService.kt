package it.unibz.krdb.kprime.domain.actor

import it.unibz.krdb.kprime.domain.AggregateID
import it.unibz.krdb.kprime.domain.Page
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.actor.ActorService.Error.*
import it.unibz.krdb.kprime.domain.project.PrjContextLocation

class ActorService(private val prjContextService : PrjContextService,
                   private val actorRepositoryBuilder: ActorRepositoryBuilder
) {

    private val kpRepoDir = ".kprime/"

    sealed interface Error {
        data class ContextNameEmpty(val mes:String): Error, IllegalStateException()
        data class ProjectUnknown(val mes:String, val wrongName:String): Error, IllegalArgumentException(mes)
        data class LocationEmpty(val mes:String) : Error, IllegalAccessError()
    }


    fun pageActors(location:PrjContextLocation, pageNr:Int, pageSize:Int = 10)
        : Result<List<Actor>> {
        val repo = actorRepositoryBuilder.build(location.value + kpRepoDir)
        return Result.success(repo.findPage(Page(pageSize,pageNr)))
    }

    fun allActors(location:String): Result<List<Actor>> {
        if (location.isEmpty()) return Result.failure(LocationEmpty("Location is empty."))
        return Result.success(actorRepositoryBuilder.build(location + kpRepoDir).findAll())
    }

    fun removeActor(location:PrjContextLocation, criteria: (Actor) -> Boolean):Result<String> {
        val repo = actorRepositoryBuilder.build(location.value + kpRepoDir)
        val deletedActorNr = repo.delete(criteria)
        return Result.success("Ok. $deletedActorNr actor removed.")
    }

    fun addActor(location:PrjContextLocation, name: String, role:String, memberOf:String, pass:String, mail:String)
        : Result<Actor> {
        val repo = actorRepositoryBuilder.build(location.value + kpRepoDir)
        val actors = repo.findAll()
        val idMax = if (actors.isEmpty()) 0 else { actors.maxOf { it.id } }
        val newActor = Actor(idMax+1, name=name, role=role, memberOf = memberOf, pass = pass, email = mail)
        repo.save(newActor)
        return Result.success(newActor)
    }

    fun replaceAllActors(contextName: String,actors:List<Actor>)
        :Result<String> {
        val context = prjContextService.projectByName(contextName)
            ?: return Result.failure(ProjectUnknown("Unknown context.",contextName))
        actorRepositoryBuilder.build(context.location  + kpRepoDir).saveAll(actors)
        return Result.success("OK.")
    }

    fun getById(projectLocation: String, actorId:AggregateID):Result<Actor> {
        val repo = actorRepositoryBuilder.build(projectLocation + kpRepoDir)
        val actor = repo.findFirstBy { it.id.equals(actorId)  } ?:
            return Result.failure(IllegalArgumentException("Not Found todo $actorId"))
        return Result.success(actor)
    }

    fun getByName(projectLocation: String, actorName:String):Result<Actor> {
        val repo = actorRepositoryBuilder.build(projectLocation + kpRepoDir)
        val actor = repo.findFirstBy { it.name == actorName  } ?:
            return Result.failure(IllegalArgumentException("Not Found todo $actorName"))
        return Result.success(actor)
    }

}