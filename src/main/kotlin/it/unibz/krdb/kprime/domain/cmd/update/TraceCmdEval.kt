package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import javax.script.ScriptContext
import javax.script.ScriptEngineManager
import javax.script.SimpleScriptContext

// TODO eval js over model
object TraceCmdEval: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    val engine = ScriptEngineManager().getEngineByExtension("kts")!!

    override fun getCmdName(): String {
        return "eval"
    }

    override fun getCmdDescription(): String {
        return "Evaluate over the current model, eventually returning errors,warning."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <commandToEval>"
    }

    override fun getCmdTopics(): String {
        return "read,kotlin"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val commandToEval = command.substring(getCmdName().length)
        try {
            engine.context.setAttribute("hello2","world", ScriptContext.GLOBAL_SCOPE)
            engine.context.setAttribute("hello6","world", ScriptContext.ENGINE_SCOPE)
            //val engineName = engine.factory.engineName
            //val languageName = engine.factory.languageName
            val bindings = engine.createBindings()
            bindings.put("schema",context.env.database.schema)
            engine.put("schema",context.env.database.schema)
            val simpleContext = SimpleScriptContext()
            val bind = engine.createBindings()
            bind.put("hello3","world")
            simpleContext.setBindings(bind,ScriptContext.GLOBAL_SCOPE)
            engine.put("hello","world")
            engine.getBindings(ScriptContext.GLOBAL_SCOPE).put("hello4","world")
            engine.getBindings(ScriptContext.ENGINE_SCOPE).put("hello5","world")
            val res = engine.eval(commandToEval)
            val result =  res?.toString()?:"No result."
            return TraceCmdResult() message result
        }catch (e:Exception) {
            return TraceCmdResult() failure e.message.toString()
        }
    }
}