# Command Parse

## from protected endpoint

    ApiBuilder.put("/context/:contextName/tracecommand",traceController.playCommands)

    CmdService

        fun parseCmdEnvelop(reqBody:String)
        :CmdEnvelope
    
        fun parse(author: String, command: String, contextId: String = "no-context", contextName: String = "")
        : TraceCmdResult

    CmdParser

        fun parse(command: String, context: CmdContext)
        : Pair<TraceCmdResult, CmdContext>
    
        private fun parseCommand(command: String, context: CmdContext)
        : Pair<TraceCmdResult, CmdContext>
    
        private fun executeCommand(command_name: String, context: CmdContext,commandLine: String)
        : TraceCmdResult

    TraceCmd

        fun execute(context: CmdContext, command:String)
        : TraceCmdResult
    
        fun executeTokens(tokens: List<String>,context: CmdContext)
        : TraceCmdResult
        
        fun executeMap(context: CmdContext, args: Map<String,String>)
        : TraceCmdResult



## from cli endpoint

    ApiBuilder.put("/cli/:contextName/tracecommand",traceController.playApiCommands)

