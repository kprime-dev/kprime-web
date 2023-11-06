# Stories

A Story is a [Markdown] text containers of Terms and Vocabularies, Goals and references to other Stories.
By convention stories are file with extension ".md"  
It is organized as a notebook, a sequence of cells.
Cells could be command or markdowntext or html cells.

## Story Commands

    >stories -path=<dir.path>
    >stories -path=stories/

    >story <file.path>
    >story stories/facts.md

    >story-note <file.path> <note.index>
    >story-note stories/facts.md 2

    >add-story ??

## Story Asking

    >find-term
    >find-paragraph
    >find-text
    >find-word


## Story Markdown

Specific conventions are used to enrich standard markdown. See [notebooks.md]

## Story Charts

Are story text files using [Maramaid] notation.
By convention drawable stories are file with extension ".chart"  
They could have lines starting with string "#" rendered as markdown titles, ignored in chart.
They could have lines starting with string "--" rendered as markdown text, ignored in chart.
They could have lines starting with string "```" rendered as markdown text, ignored in chart.

## Story Lines

Are story text files using [Markdown] text.
By convention drawable stories are file with standard extension ".md".
From story are recognized lines starting with reserved [Eventstorm] keywords:

+ event <EventName>
+ actor <ActorName>
+ readmodel <ModelName>
+ command <CommandName>
+ aggregate <AggregateName>
+ external <ExternalName>
+ policy <PolicyName>
+ goal <GoalName>
+ source
+ go
[](.md)
[](*)

Story Lines can be drawn an event  flow chart by clicking on "Storyline" toolbar link.

### References

[1] [notebooks.md]
[2] [Marmaid](http://mermaid.js.org/)
[3] [Markdown](https://www.markdownguide.org/)
[4] [Eventstorm](https://www.eventstorming.com/)