package it.unibz.krdb.kprime.adapter.chart

import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.term.TermService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.trace.TraceService
import it.unibz.krdb.kprime.domain.user.User
import it.unibz.krdb.kprime.domain.user.UserApi

// TODO Switch to TraceName usage instead of String.
class SlideService(val traceService: TraceService, val termService: TermService) {

    fun getProjectSlideHtml(userApi: UserApi, prjContext: PrjContext, traceName: String, traceFileName: String): String {
        val storyFileContentLines = traceService.getTraceFileLines(prjContext.location,traceName, traceFileName)
        val slideTemplate = SlideService::class.java.getResource("/public/slide.html")?.readText()
            ?: throw IllegalStateException("Resource /public/slide.html not found")
        val databaseFileName = extraxtDatabaseFileName(storyFileContentLines)
        val storyFileContent = computeStoryFileContent(databaseFileName, traceName, prjContext, storyFileContentLines)
        val menu = computeMenuLine(prjContext, traceName, traceFileName, userApi)
        return slideTemplate
                .replace("{{markdownStory}}", storyFileContent)
                .replace("{{traceName}}",     traceName)
                .replace("{{traceFileName}}", traceFileName)
                .replace("{{menu}}",          menu)
    }

    private fun computeStoryFileContent(databaseFileName: String?, traceName: String, prjContext: PrjContext, storyFileContentLines: List<String>): String {
        return if (databaseFileName != null) {
            storyFileContentWithTermLinks(traceName, databaseFileName, prjContext, storyFileContentLines)
        } else {
            storyFileContentLines.joinToString(System.lineSeparator())
        }
    }

    private fun computeMenuLine(prjContext: PrjContext, traceName: String, traceFileName: String, userApi: UserApi): String {
        val urlStoryline = if (prjContext.isNoProject()) {
            "[storyline](/chartactivities/${traceName}/${traceFileName})"
        } else {
            "[storyline](/project/${prjContext.name}/chartactivities/${traceName}/${traceFileName})"
        }
        return if (userApi.role != User.ROLE.ANONYMOUS.name) {
            menuLineForRegisteredUser(traceName, traceFileName, urlStoryline)
        } else {
            manuLineForAnonymousUser(urlStoryline)
        }
    }

    private fun manuLineForAnonymousUser(urlStoryline: String): String {
        return """
                    $urlStoryline
                    """.trimIndent()
    }

    private fun menuLineForRegisteredUser(traceName: String, traceFileName: String, urlStoryline: String): String {
        return """
                    [play](/traceplay/${traceName}/${traceFileName})
                    $urlStoryline
                    """.trimIndent()
    }

    private fun storyFileContentWithTermLinks(traceName: String, databaseFileName: String, prjContext: PrjContext, storyFileContentLines: List<String>): String {
        println("storyFileContentWithTermLinks: [$traceName] [$databaseFileName]")
        val terms = termService.getAllTerms(TraceName(traceName), databaseFileName, prjContext.location).map { term -> term.name }
        return storyFileContentLines.joinToString(System.lineSeparator())
        { if (isNotHeaderOrLinkLine(it)) replaceLineTermsWithLinks(prjContext, traceName, databaseFileName, it, terms) else it }
    }

    private fun isNotHeaderOrLinkLine(it: String) = !(it.startsWith("#") || it.contains("["))

    private fun extraxtDatabaseFileName(storyFileContent: List<String>): String {
        return storyFileContent
                .filter { it.startsWith("+ termbase ") }
                .map { it.substring(11).trim() }
                .firstOrNull() ?: "base.xml"
    }


    private fun replaceLineTermsWithLinks(prjContext: PrjContext, traceName:String, traceFileName: String, lineContent: String, terms: List<String>): String {
        var newFileContent = lineContent
        if (prjContext.isNoProject()) {
            for (term in terms) {
                newFileContent = newFileContent.replace(term, "[${term}](/dictionary/$traceName/$traceFileName/${term})")// TOFIX from term name to term gid
            }
        } else {
            for (term in terms) {
                newFileContent = newFileContent.replace(term, "[${term}](/project/${prjContext.name}/dictionary/$traceName/$traceFileName/${term})")// TOFIX from term name to term gid
            }
        }
        return newFileContent
    }


}