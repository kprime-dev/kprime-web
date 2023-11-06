# Notebooks

A notebook is a Markdown text file using KPrime markup conventions.

## Notebook KPrime Conventions

* Each markdown separator (---) split text into two note cells before and after the separator.
* Each cell could be moved up, down, removed.


1. text cell
    free text
   * Each markdown title (#) has its own note cell.
   * Each line of text that contains ([[[) is parsed as an KPrime anchor.

2. command cell
   * Each cell starting with a (>) identifies a command cell.
   * Each cell is executed as one command.
   * It could be executed.
   * It could be multiline with contiguous line starting with '>'.
   * It could be free text using ```   ```

3. reference cell
   first line starts with
  ```ref  
  last line ends with 
  ```

4. goal cell
   first line starts with
  ```goal
  last line ends with 
  ```

5. term cell
   first line starts with
  ```term
  last line ends with 
  ```

6. meta cell
    first line starts with
  ```meta
  last line ends with 
  ```

special meta labels:
  gid: <gid-value>

7. data cell
    first line starts with
    ```csv 
    or 
    ```json
    or
    ```yaml 
    last line ends with 
   ```
data cells could be used as data sources. See [sources.md].

## Free text cell extensions

* Html
* Math
* Slide
* TeX

## Current Limitations

* No support for relative file paths.
    - Images have to be loaded from absolute web path. 
    - Anchor to siblings notebooks have to use absolute paths.

## References

[1] [sources.md](sources.md)