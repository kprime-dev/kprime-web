# Expert Ollama

[Ollama](https://github.com/jmorganca/ollama) it's a LLM that could be locally installed on linux with:

    curl https://ollama.ai/install.sh | sh

It could be started with:

    ollama run mistrall

for info about [Mistral model](https://mistral.ai/news/)

performance depends on config and your hardware. 

## list experts

    experts 

Check the presence of Ollama expert

## eventually, if not is already present add expert

    add-expert ollama <url-expert>

## list available expert skills

    expert ollama

## invoke select generation

        >expert ollama select:
        ```
        Can you please give me some SQL query over my table `worker_performing_task` with explicit full attribute names?
        ```
        ---

given the current database schema fields    
will return a query from natural language.

## invoke select suggestion

        >expert ollama select:
        ```
        Can you please suggest me some SQL business query on tables?
        ```
        ---

given the current database schema fields    
will return a query from natural language.

## invoke direct chat

        >expert ollama chat:
        ```
        Given these tables:
        Table worker, columns = [worker_id, worker_name, worker_acronym, project_id].
        Table worker_performing_task, columns = [ID, worker_id, monitoring_date_id, hrs_worked, project_id, total_hours].  
        Can you join the two tables ?

        ```
        ---

