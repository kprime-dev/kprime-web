package it.unibz.krdb.kprime.domain.cmd.rest

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration


object TraceCmdCallHttp: TraceCmd {

    private const val PROJECT_ARG = "_prj"
    private const val TRACE_ARG = "_tr"
    private const val TRACE_FILE_ARG = "_tb"

    override fun getCmdName(): String {
        return "http"
    }

    override fun getCmdDescription(): String {
        return "Call http endpoint."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <URL> : [<post-text>] "
    }

    override fun getCmdTopics(): String {
        return "read,write,http"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        println("command:$command")
        val toPost = if (command.contains(":"))
            command.substringAfter(":").trim()
            else ""
        println("toPost:$toPost")
        val tokens = command.
                split(System.lineSeparator()).joinToString("").
                substringBefore("::").split(" ")
        if (tokens.size < 2) {
            var result = "HTTP Verb required. Available verbs=GET " + System.lineSeparator()
            val httpVerbOptions = listOf("http::get")
            return TraceCmdResult() message result options httpVerbOptions
        }

        val verbName = tokens[1]
        require(it.unibz.krdb.kprime.domain.cmd.TraceCmd.isValidArgument(verbName)) {"Expert name has invalid characters."}

        var httpCallUrl = tokens[2]


        val client: HttpClient = HttpClient.newBuilder()
//                .version(Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
//                .proxy(ProxySelector.of(InetSocketAddress("proxy.example.com", 80)))
//                .authenticator(Authenticator.getDefault())
                .build()

        val requestBuilder =  HttpRequest.newBuilder()
                .uri(URI.create(httpCallUrl))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")

        var request : HttpRequest = if (toPost.isEmpty())
            requestBuilder.GET().build()
        else
            requestBuilder.POST(HttpRequest.BodyPublishers.ofString(toPost)).build()
        // request = requestBuilder.POST(HttpRequest.BodyPublishers.ofFile(Paths.get("file.json")))


        val responseStr: HttpResponse<String> = client.send(request, BodyHandlers.ofString())
        var expertMessage = ""
        var expertOptions = emptyList<String>()
        try {
            val response = ObjectMapper().readTree(responseStr.body())
//            val response =
//                ObjectMapper().registerModule(KotlinModule()).readValue(responseStr.body(), ExpertResponse::class.java)
            expertMessage = response.asText()
//            expertMessage = "[${response.msg.uuid}] HTTP $verbName called with ${request.method()} $httpCallUrl posted ${toPost.length}" + System.lineSeparator() +
//                    "response: ${response.msg.label.name} ${response.msg.level.name}" + System.lineSeparator()
//            expertMessage += response.msg.description.replace("\\n", System.lineSeparator())
//            expertOptions = response.options.options.map { op -> "expert::"+op.optComand}
        } catch (e: Exception) {
            e.printStackTrace()
            expertMessage = "HTTP $verbName called with $httpCallUrl " + System.lineSeparator() +
                    "response: " + System.lineSeparator()
            expertMessage += responseStr.body().replace("\\n", System.lineSeparator())
        }
        return TraceCmdResult() message expertMessage options expertOptions
    }

}