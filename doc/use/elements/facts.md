# Facts

Facts are statements in controlled language to define and constraint data values.

Facts have grain levels.

First most fine-grained are instance data level:
"'Mary Jones' was hired on '3 March 2010'"

One level of abstraction above, values are classified to types:
"'Mary Jones' is an Employee"
"Employee was hired on HiringDate"

Above there are type hierarchies including subtypes classification:
"Person are Employee,Managers,Customers not-exaustive, not-exclusive"

## Writing Facts

>add-fact

**consistency check**
it requires a logic engine

## Reading Facts

>facts

**inference derivations**
it requires a logic engine

## Querying Facts

>select

**semantic query answering**
it requires a logic engine and query rewriting

## Natual language 

You could try to freely chat using LLM engines like [ChatGpt] or [Mistral] via [Expert Ollama]  

## References

[1] https://en.wikipedia.org/wiki/Dimensional_fact_model
[2] https://en.wikipedia.org/wiki/Data_warehouse#Facts
[3] https://en.wikipedia.org/wiki/Object%E2%80%93role_modeling
[5] ORMie Engine
[6] OnTop Engine
[7] Expert ORM
[8] [ChatGPT](https://openai.com/chatgpt) 
[9] [Mistral](https://mistral.ai/) - The best 7B model to date, Apache 2.0
[10] Expert chatgpt
[11] Expert ollama