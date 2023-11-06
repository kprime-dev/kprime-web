package it.unibz.krdb.kprime.domain.cmd.expert

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import it.unibz.cs.semint.kprime.expert.adapter.ExpertResponse
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmd.Companion.isValidArgument
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration


object TraceCmdCallExpert: it.unibz.krdb.kprime.domain.cmd.TraceCmd {

    private const val PROJECT_ARG = "_prj"
    private const val TRACE_ARG = "_tr"
    private const val TRACE_FILE_ARG = "_tb"

    override fun getCmdName(): String {
        return "expert"
    }

    override fun getCmdDescription(): String {
        return "Call expert."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <expert-name> [<expert-action> [<param=value>...]] "
    }

    override fun getCmdTopics(): String {
        return "read,expert"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        println("command:$command")
        val toPost = if (context.envelope.cmdPayload.isNotEmpty())
                context.envelope.cmdPayload.joinToString("\n")
            else if (command.contains(":"))
                command.substringAfter(":").trim()
            else ""
        println("toPost:[$toPost]")
        val tokens = command.
                split(System.lineSeparator()).joinToString("").
                substringBefore(":").split(" ")
        if (tokens.size < 2) {
            val experts = context.pool.expertService.listExperts()
            var result = "Ok. ${experts.size} experts. " + System.lineSeparator()
            result += experts.joinToString(System.lineSeparator()) { " ${it.name} ${it.url}" }
            val expertOptions = experts.map { "expert::"+it.name }
            return TraceCmdResult() message result options expertOptions
        }

        val (mandatory,optionals) = TraceCmd.separateArgsOptionals(command)

        val expertName = tokens[1]
        require(isValidArgument(expertName)) {"Expert name has invalid characters."}
        val expert = context.pool.expertService.getExpert(expertName)
                ?: return TraceCmdResult() failure "Expert $expertName not found."

        var expertUrl = expert.url

        expertUrl = appendExpertAction(tokens, expertUrl)
        expertUrl = appendActionParams(tokens, expertUrl)
        expertUrl = appendProjectName(context, expertUrl)
        expertUrl = appendProjectTrace(context, expertUrl)
        expertUrl = appendCurrentFileName(context, expertUrl)

        val client: HttpClient = HttpClient.newBuilder()
//                .version(Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
//                .proxy(ProxySelector.of(InetSocketAddress("proxy.example.com", 80)))
//                .authenticator(Authenticator.getDefault())
                .build()

        val requestBuilder =  HttpRequest.newBuilder()
                .uri(URI.create(expertUrl))
                .timeout(Duration.ofSeconds(300))
                .header("Content-Type", "application/json")

        var request : HttpRequest = if (toPost.isEmpty())
            requestBuilder.GET().build()
        else
            requestBuilder.POST(HttpRequest.BodyPublishers.ofString(toPost)).build()
        // request = requestBuilder.POST(HttpRequest.BodyPublishers.ofFile(Paths.get("file.json")))


        val responseStr: HttpResponse<String> = client.send(request, BodyHandlers.ofString())
        var expertMessage = ""
        var expertOptions = emptyList<String>()
        var expertMessageType = ""
        try {
            val response =
                ObjectMapper().registerModule(KotlinModule()).readValue(responseStr.body(), ExpertResponse::class.java)

            expertMessageType = response.msg.contentType
            if (expertMessageType=="text") {
                expertMessage = "[${response.msg.uuid}] expert $expertName called with ${request.method()} $expertUrl posted ${toPost.length}" + System.lineSeparator() +
                        "response: ${response.msg.label.name} ${response.msg.level.name}" + System.lineSeparator()
                expertMessage += response.msg.description.replace("\\n", System.lineSeparator())
            } else {//TODO JSON Response
                expertMessage = "[${response.msg.uuid}] expert $expertName called with ${request.method()} $expertUrl posted ${toPost.length}" + System.lineSeparator() +
                        "response: ${response.msg.label.name} ${response.msg.level.name}" + System.lineSeparator()
                expertMessage += response.msg.description.replace("\\n", System.lineSeparator())
            }
            if (optionals["format"]=="html") {
                expertMessage = expertMessage.replace("\n", "</BR>")
                println("expertMessage HTML=[$expertMessage]")
            } else println("expertMessage =[$expertMessage]")


            expertOptions = response.options.options.map { op -> "expert::"+op.optComand}
        } catch (e: Exception) {
            e.printStackTrace()
            expertMessage = "Expert $expertName called with $expertUrl " + System.lineSeparator() +
                    "response: " + System.lineSeparator()
            expertMessage += responseStr.body().replace("\\n", System.lineSeparator())
        }
        return TraceCmdResult() message expertMessage options expertOptions messageType expertMessageType
    }

    private fun appendActionParams(
        tokens: List<String>,
        expertUrl: String
    ): String {
        var expertUrl1 = expertUrl
        val expertParams = if (tokens.size > 3) {
            tokens.drop(3).joinToString("&")
        } else {
            ""
        }
        if (expertParams.isNotEmpty()) expertUrl1 += "?" + expertParams
        return expertUrl1
    }

    private fun appendExpertAction(
        tokens: List<String>,
        expertUrl: String
    ): String {
        var expertUrl1 = expertUrl
        val expertAction = if (tokens.size > 2) {
            tokens[2]
        } else {
            ""
        }
        expertUrl1 += expertAction
        return expertUrl1
    }

    private fun appendCurrentFileName(
        context: CmdContext,
        expertUrl: String
    ): String {
        var expertUrl1 = expertUrl
        val currentTraceFileName = context.env.currentTraceFileName
        if (currentTraceFileName != null && !expertUrl1.contains(TRACE_FILE_ARG)) {
            if (expertUrl1.contains("?")) expertUrl1 += "&" else expertUrl1 += "?"
            expertUrl1 += "$TRACE_FILE_ARG=$currentTraceFileName"
        }
        return expertUrl1
    }

    private fun appendProjectTrace(
        context: CmdContext,
        expertUrl: String
    ): String {
        var expertUrl1 = expertUrl
        val traceName = context.env.currentTrace //"root"//context.pool.settingService.getTraceName()
        if (traceName != null && !expertUrl1.contains(TRACE_ARG)) {
            if (expertUrl1.contains("?")) expertUrl1 += "&"
            else expertUrl1 += "?"
            expertUrl1 += "$TRACE_ARG=traces___$traceName"
        }
        return expertUrl1
    }

    private fun appendProjectName(
        context: CmdContext,
        expertUrl: String
    ): String {
        var expertUrl1 = expertUrl
        val projectName = context.env.prjContextName.value
        if (projectName != null && !expertUrl1.contains(PROJECT_ARG)) {
            if (expertUrl1.contains("?")) expertUrl1 += "&"
            else expertUrl1 += "?"
            expertUrl1 += "$PROJECT_ARG=$projectName"
        }
        return expertUrl1
    }
}