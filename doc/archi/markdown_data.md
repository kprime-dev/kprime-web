# from Obsidian

Dataview generates data from your vault by pulling information from Markdown frontmatter and Inline fields.

    Markdown frontmatter is arbitrary YAML enclosed by --- at the top of a markdown document which can store metadata about that document.
    Inline fields are a Dataview feature which allow you to write metadata directly inline in your markdown document via Key:: Value syntax.

Examples of both are shown below:

    ---
    alias: "document"
    last-reviewed: 2021-08-17
    thoughts:
    rating: 8
    reviewable: false
    ---

    # Markdown Page
    
    Basic Field:: Value
    **Bold Field**:: Nice!
    You can also write [field:: inline fields]; multiple [field2:: on the same line].
    If you want to hide the (field3:: key), you can do that too.



Querying

Once you've annotated documents and the like with metadata, you can then query it using any of Dataview's four query modes:

* Dataview Query Language (DQL): A pipeline-based, vaguely SQL-looking expression language which can support basic use cases. See the documentation for details.

    ```dataview
    TABLE file.name AS "File", rating AS "Rating" FROM #book
    ```

* Inline Expressions: DQL expressions which you can embed directly inside markdown and which will be evaluated in preview mode. See the documentation for allowable queries.

    We are on page `= this.file.name`.

* DataviewJS: A high-powered JavaScript API which gives full access to the Dataview index and some convenient rendering utilities. Highly recommended if you know JavaScript, since this is far more powerful than the query language. Check the documentation for more details.


    ```dataviewjs
    dv.taskList(dv.pages().file.tasks.where(t => !t.completed));
    ```

* Inline JS Expressions: The JavaScript equivalent to inline expressions, which allow you to execute arbitrary JS inline:

    This page was last modified at `$= dv.current().file.mtime`.


## Proposed


Mardown data container cells:

```json

```

```yaml

```

```csv

```

```rdf

```

Import data file commands:

>import-json <filename>


Query with commands:
SQL
RDF
