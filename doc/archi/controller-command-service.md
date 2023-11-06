# Sequence Controller-Command-Service

Es. StoryController.putTemplate

    var putTemplate = Handler { ctx: Context ->
        val currentUser = ctx.sessionAttribute<String>(User.CURRENT_USER)?: User.NO_USER
        val contextName = ContextName(ctx.pathParam("contextName"))
        val storyName = StoryName(ctx.pathParam("storyName"))
        val traceName = TraceName(ctx.pathParam("traceName"))
        val templateName = TemplateName(ctx.pathParam("templateName"))
        val result = cmdService.parse(
            currentUser, "${TraceCmdFromTemplate.getCmdName()} " +
                    "${templateName.value} " +
                    "${storyName.value} " +
                    "${traceName.toDirName()}", contextName = contextName.value
        )
        ctx.json(result)
    }
