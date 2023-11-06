# Archi

## Layers

* adapter: domain dependent, implements contracts in a specific technology.
* domain: no technology dependency. Pure business entities, services with interfaces for contract.
* support: no domain dependency. Pure algorithms and structures that can be reused in projects.
* view: domain dependent, uses domain services to accomplish user requests.

* Starter: single point to layer wiring and start the server accepting user requests.

### No mapping strategy

Domain is exposed to users.

## Server Config Files

    ${env:KPRIME_HOME}/
    drivers/
        drivers.json
    experts/
        experts.json
    logs/
        YYYY-MM/*.log.gz
        commands.log
        app.log
    settings/
        settings.json
    templates/
        *.md
    users/
        user.json  => users.json
    vocabularies/
        *.ttl
    projects.json

## Project Config Files

    PROJECT_HOME/
    data/
        *.db
        *.base.xml
    sources/
        source.json => sources.json
    terms/
        <trace>/*_db.json
        vocabularies.json
    todos/ => goals
        todo.json => goals.json
    traces/ => stories/ + bases/
    stories/
        <folder>/
        *.md
    bases/
        <trace>/base*.xml
        <trace>/changeset*.xml
    transformers/
        _name_/
            compose/
            decompose/
    users/
    *.md


## Authentication

LoginController.

## Authorization

Not constrained.

## Start sequence

    Starter
        Services to repositories and services
        Controllers to services
        Router ro controllers

## Domain Service Call Chain

    View.Router
        View.before LoginController // Validazione credenziali
        View.XController API, HTML: JSON, XML, HTML  // Validazione protocollo IO adapter  
            Domain.CmdService.parse : TraceCmdResult
            Domain.Cmd.execute(command,context) : TraceCmdResult   // Validazione sintattica IO domain
                Domain.context.env.serviceX : Result // Validazione semantica IO domain
                    Domain.Entity
                    Domain.Repository

## Loggers

* To allow user know past action, events and eventually undo.

    command.log : File

call chain:

    CmdParser.execute
        logCommandRequest
        logCommandResponse
            log file append


* To allow developer know system past status, user behaviour.

  app.log : log4j.Logger

call chain:  

      CmdService
        CmdParser(logger = LogManager.getLogger())   
            logger.debug()

## Monitors

Not monitored.

## User Error Handling

exposed via Controllers:

    | ErrorType   | Component
    -------------------------
    | Credentials | router filter login controller
    | Channel     | controller
    | Domain      | command, service, entity
    | Adapter     | adapter

## System Error Handling

logged to app.log (see logger).

## Global IDs (Gids)

Following entities have global ids (to be in valid state).
- Story: filename
- Goal: UUID todo.gid
- Term: UUID term.gid
- Context: UUID project.gid 
- Database: database.gid
  - Table: database.gid + '_tab'+ table.id
  - Column: database.gid + '_col'+ column.id
  - Constraint: database.gid + '_con'+ constraint.id
  - Mapping: database.gid + '_que'+ query.ig
- Source: UUID source.gid (TO ADD)
- Actor: UUID actor.gid (TO ADD)
- ChangeSet: UUID changest.gid

### GIDs lifecycle

