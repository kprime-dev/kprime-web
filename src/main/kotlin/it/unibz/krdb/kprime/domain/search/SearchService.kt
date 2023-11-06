package it.unibz.krdb.kprime.domain.search

import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.story.StoryService
import it.unibz.krdb.kprime.domain.term.Term
import it.unibz.krdb.kprime.domain.term.TermService
import it.unibz.krdb.kprime.domain.todo.Todo
import it.unibz.krdb.kprime.domain.todo.TodoService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.support.WebEncoder

class SearchService (val settingService: SettingService,
                     val prjContextService: PrjContextService,
                     val todoService: TodoService,
                     val termService: TermService,
                     val storyService: StoryService
) {

    fun findTerm(termText: String): List<SearchResult> {
        val searchResults = mutableListOf<SearchResult>()
        searchResults.addAll(findInTodo(termText))
        searchResults.addAll(findInDictionary(termText))
        searchResults.addAll(findInStories(termText))
        return searchResults
    }

    // TODO filter by project location
    private fun findInStories(text: String):List<SearchResult> {
        val searchResult  = mutableListOf<SearchResult>()
        val projects = prjContextService.readAllProjects()
        for (project in projects) {
            if (project.activeTrace.isNotEmpty() && project.activeTermBase.isNotEmpty()) {
                searchResult.addAll(
                storyService.findText(project.location,  text)
                    .filter { (doc, score) -> doc.get("path").startsWith(project.location)}
                    .map { (doc, score) ->
                    SearchResult(
                        doc.get("filename"),
                        mapToStoryUrl(storyService.computeStoryUrl(doc.get("path"), project.location),doc.get("filename"),project), SearchResultType.STORY
                    )
                }
                )

            }
        }
        return searchResult
    }

    private fun findInTodo(text: String):List<SearchResult> {
        val searchResult  = mutableListOf<SearchResult>()
        val projects = prjContextService.readAllProjects()
        for (project in projects) {
            if (project.activeTrace.isNotEmpty() && project.activeTermBase.isNotEmpty()) {
                val todos = todoService.all(project.location)
                searchResult.addAll(todos.filter { it.title.contains(text, ignoreCase = true) }
                        .map { SearchResult(it.title, mapToCasesUrl(it, project), SearchResultType.GOAL) })
            }
        }
        return searchResult
    }

    private fun findInDictionary(textToFind: String):List<SearchResult> {
        val searchResults  = mutableListOf<SearchResult>()
        val projects = prjContextService.readAllProjects()
        for (project in projects) {
            try {
                if (project.activeTrace.isNotEmpty() && project.activeTermBase.isNotEmpty()) {
                    val allTerms = termService.getAllTerms(TraceName(project.activeTrace), project.activeTermBase, project.location)
                    searchResults.addAll(allTerms.filter { t -> t.name.contains(textToFind, ignoreCase = true) }
                            .map { SearchResult(it.name, mapToDictionaryUrl(it, project), SearchResultType.TERM) })
                }
            } catch (exception: Exception) {
                val errorMessage = " Problem with [${project.name}][${project.activeTrace}][${project.activeTermBase}] ${exception.localizedMessage}"
                searchResults.add( SearchResult(errorMessage, errorMessage , SearchResultType.ERROR))
            }
        }
        return searchResults
    }

    private fun mapToDictionaryUrl(term : Term, prjContext: PrjContext):String {
        return "<a target=\"search\" href=\"/project/${prjContext.name}/dictionary/${term.name}\">${prjContext.name}:${prjContext.activeTrace}:${prjContext.activeTermBase}:${term.name}</a>"
    }

    private fun mapToCasesUrl(todo : Todo, prjContext: PrjContext):String {
        //return "<a target=\"search\" href=\"/project/${project.name}/cases\">${project.name}:${project.activeTrace}:${project.activeTermBase}:${todo.title}</a>"
        return "<a target=\"search\" href=\"/project/${prjContext.name}/todo/${WebEncoder.encode(todo.title)}\">${prjContext.name}:${prjContext.activeTrace}:${prjContext.activeTermBase}:${todo.title}</a>"
    }

    private fun mapToStoryUrl(storyUrl : String, storyName:String, prjContext: PrjContext):String {
        return "<a target=\"search\" href=\"${storyUrl}\">${prjContext.name}:${prjContext.activeTrace}:${prjContext.activeTermBase}:${storyName}</a>"
    }

}