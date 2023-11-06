# db expert transformations

Given a chat gpt key set it as env variable

        export OPENAI_KEY

## list experts

    experts 
    
Check the presence of Chat GPT

## eventually, if not is already present add expert 

    add-expert gpt <url-expert>

## list available expert skills

    expert gpt

## invoke select generation

        >expert gpt select:
        ```
        Can you please give me some SQL query over my table `worker_performing_task` with explicit full attribute names?
        ```
        ---

given the current database schema fields    
will return a query from natural language.

## invoke select suggestion

        >expert gpt select:
        ```
        Can you please suggest me some SQL business query on tables?
        ```
        ---

given the current database schema fields    
will return a query from natural language.

## invoke direct chat

        >expert gpt chat:
        ```
        Given these tables:
        Table worker, columns = [worker_id, worker_name, worker_acronym, project_id].
        Table worker_performing_task, columns = [ID, worker_id, monitoring_date_id, hrs_worked, project_id, total_hours].  
        Can you join the two tables ?

        ```
        ---

