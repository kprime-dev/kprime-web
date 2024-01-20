# Logger

There are 2 loggers:

    logger, LoggerService

    commandEventLogger, CmdLoggerService

actually:
    serverLogger = LoggerService used in Controllers e CmdParser

    commandEventLogger = logger istanziato in CmdParser come 
        var commandEventLog = mutableListOf<CommandEvent>()

    sealed class CommandEvent {
    abstract fun toLog(): String
    }
    
    class CommandResponseEvent(
    val time: LocalDateTime,
    val author: String,
    private val commandId: String,
    val result: String,
    val warn: String,
    val errors: String,
    val oid: String = ""
    ) : CommandEvent() {

