# Command (TraceCmd)

Trace command because it will be applied to a trace, to a specific version of the context.

## first version:

    name: used in cmd parsing and help descriptions.
    description: text to inform user, triggered by "?".
    usage: text to help user, triggered by "??" .
    topics: used in help filter, and behaviour distinguish write operations.
    execute: single method to operate on CmdContext and return a TraceCmdResult.

usage:

  by overriding methods:
  - getCmdName
  - getCmdDescription
  - getCmdTopics
  - getCmdUsage
  - execute

---

## second version: inline extension of the first version for better parameter management.

    have a shareable enumeration of arguments
    useful for shared validation in parse
    useful for a shared description in usage

methods:
  execute with default help and validation behaviour
  getCmdArguments:
  executeTokens:
  executeMap:
  validate:

usage:

  by overriding methods:
  - getCmdName
  - getCmdDescription
  - getCmdTopics
  - getCmdArguments
  - executeMap

---

## third version: added a method for returning hints in response to even a partial command line.

  by overriding method:
  - getCmdSuggestions

* TraceCmdResult

It's a command result wrapper.

* TraceCmdArgumentI

It's a command argument descriptor.

Example: TraceCmdListStories

---

##  fourth version: added a first enumeration of Topic and automatic writing in the case of a topic WRITE DATABASE command.


---

## fifth version: added optional command parameters as tokens

  any optional can be any token but first.
  They are removed from mandatory field list. 

  -paramkey=paramvalue

    e.g. TraceCmdSqlSelect

    val (tokens,optionals) = TraceCmd.separateOptionals(tokens2) 
    val datasourceNameFromCmd = optionals["source"]?:""

      >select -source=confu * from project;
      >select * from project; -source=confu 
  

## sixth version: added command payload 

  multiline text as cmd payload as list of strings trimmed of spaces.



    e.g. TraceCmdSqlSelect

    context.evelope.cmdPayload.isEmpty()

      >select -source=confu
      ```
      * from project;
      ```

## seventh version: optional arguments now are checked too.

    e.g. see TraceCmdTest


## WARNINGS

* Actually all versions are in use. 


