# Link between goal, story, term.

* a goal has an ID
* a term has an ID
* a source has an ID
* the connection between goal and a term is a story.

## from goal

* add a goal
* click on link a root story
* edit the story or edit as new
  * if new
    * add story metadata goal ID
    * backlink is added in story-goal section. 
* inside the story each word enclosed in [[]] will be linked to term editor or viewer.
  * each word-link is added in story-term section.



## from story
a story links to a goal with ID using syntax:

    + goal ID
    e.g.
    + goal 1678119715887


a story links to an actor with ID using syntax:

    + actor ID

a story links to a source with ID using syntax:

    + source ID


## from noteedit.html noteedit.js

@keyup.ctrl.space="autocompGoal" @keyup.enter="addGoal"
http://localhost:7000/context/kprime-case-confucius-mysql/todo
goalLinks += '['+this.notegoals[goal].label+']('+ this.notegoals[goal].url + ')\n'
[goal:Model Confucius Data Workflow in MySQL](/project/kprime-case-confucius-mysql/todo/Model%20Confucius%20Data%20Workflow%20in%20MySQL)

@keyup.ctrl.space="autocompRefTo" @keyup.enter="addRefTo"
http://localhost:7000/project/kprime-case-confucius-mysql/forcetree/json/
ChartForceTreeHandlers.getForceTreeProjectJson
reftoLinks += '['+this.noterefsto[refto].label+']('+ this.noterefsto[refto].url + ')\n'

@keyup.ctrl.space="autocompTerm" @keyup.enter="addTerm"
http://localhost:7000/context/kprime-case-confucius-mysql/terms/root/base
termLinks += '['+this.noteterms[term].label+']('+ this.noteterms[term].url + ')\n'
[term:abs_daily_monitoring](/project/kprime-case-confucius-mysql/dictionary/abs_daily_monitoring)
[term:project](/project/kprime-case-confucius-mysql/dictionary/project)


## from goal detail page

prints and links stories

(nothing done)

## from term detail page, dictionary-term.html

print and links stories

<#list termStories as storyTerm>
<#list termGoals as goalTerm>

TermController
private fun getTermPageContent
val findTermResult = searchService.findTerm(termName)
val termStoriesUrls = findTermResult.filter { it.type == SearchResultType.STORY }.map { it.position }
val termGoalsUrls = findTermResult.filter { it.type == SearchResultType.GOAL }.map { it.position }

SearchService

    >set-as-active
    private fun findInStories(text: String):List<SearchResult> {
        storyService.findText(project.location,  text)

StoryService

    fun findText(workingDir: String, termQuery: String, indexDirName: String = getStoryIndexDir()): List<Pair<Document, Float>> {}
        settingService.getInstanceDir()+ STORY_INDEX_DIR = "storyindex/"
        val indexDir = FSDirectory.open(Paths.get(indexDirName))

    fun indexStories
        private fun indexText(fileToIndex:File, indexDirName:String, update:Boolean): String {

    fun indexAllStories(updated: Boolean,storyIndexDir: String= getStoryIndexDir()) {

    TraceCmdFindText
        "find-text" -updated

>find-text -updated project

    /home/nicola/Workspace/kprime-cases-bz/kprime-case-confucius/readme.md 1.1184803
    /home/nicola/Workspace/kprime-cases-bz/kprime-case-confucius-mysql/readme.md 1.0916023
    /home/nicola/Workspace/kprime-cases-bz/kprime-case-employee/stories/book-team_0.md 1.0751644
    /home/nicola/Workspace/kprime-cases-bz/kprime-case-confucius/articles/Process Management in Construction: Expansion of the Bolzano Hospital.md 1.0067128
    /home/nicola/Workspace/kprime-cases-bz/kprime-case-confucius/meeting/meet20230309.md 0.9733448
    /home/nicola/Workspace/kprime-cases-bz/kprime-case-alpinebits/destinations/mountainAreas/traces/root/commands_story.md 0.67855847
    /home/nicola/Workspace/kprime-cases-bz/kprime-case-confucius/articles/Increasing Control in Construction Processes: the Role of Digitalization.md 0.57765603

Search page: 'project'
STORY 	kprime-case-employee:root:base:book-team_0.md
STORY 	kprime-case-confucius-mysql:root:base:readme.md
