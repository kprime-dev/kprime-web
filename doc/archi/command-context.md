# CmdContext

* container of information required for the execution of a command.
    .env
* container of services required for the execution of a command.
    .pool

## WARNINGS

* There are known concurrency problems in user-context paring, and service resource consumption.
   No proper lock or immutability. 