package it.unibz.krdb.kprime.domain.story

@JvmInline
value class StoryName (val value:String) {
    fun isEmpty():Boolean {return value.isEmpty()}

    companion object {
        val NO_STORY_NAME = StoryName("")
    }

    fun toFileName() = value

}