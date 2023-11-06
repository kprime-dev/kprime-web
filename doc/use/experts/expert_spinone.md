# db expert transformations

Given a database as [Person dn](use/db.md)

## list experts

    experts 
    
Check the presence of SpinOne

## eventually, if not is already present add expert spinone

    add-expert spinone <url-expert>

## list available expert skills

    expert spinone

## invoke suggestions

    expert spinone suggest
    
will return available transformations
    
## invoke

    expert spinone transform <tables>

## case1. transformation requires parameters

Expert will generate a new story and returns a message with the link.
Inside the story there is the recording of the activities with question points requiring your answers.

Complete the answers and rerun with:

    expert spinone transform <tables> <story>

## case2. transformation completed

Expert will create a new changeset and apply it to generate a new schema.
Expert will also write the recording of the activities inside a new story.
