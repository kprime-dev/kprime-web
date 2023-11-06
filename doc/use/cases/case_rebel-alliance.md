## book Rebel Alliance
Mandatory is assumed if not specified otherwise.

USAGE:
run-book src/main/resources/book/rebel-alliance.book

* context it.semint.rebelalliance
* graph rebelalliance as ra
* with ra
>graph ra

* fact Rebel takes part in Battle [0..n]
>fact ra Rebel takes part in Battle

* fact Rebel has Name [text,key]
>fact ra Rebel named as Name

* fact Rebel has BeignType {human, alien, robot}
>fact ra Rebel beign type as BeignType


* fact Rebel beign relative with Rebel [irreflexive]
.>fact Rebel beign relative with Rebel
* fact Rebel died in Battle [subset of take part in]

* fact Rebel is a Jedi,Fighter [partition]
* fact Fighter has Experience [number]
* fact Jedi is a Knight, Padawan [not-overlapping,not-total]
* fact Padawan is trained by [1] Knight
* fact Knight trains [0..1] Padawans

* fact Battle has Code [key]
>fact ra Battle has Code
>constraint ra battle-has-code key
>constraint ra rebel-named-as-name key

* fact Battle has SpacePosition
>fact ra Battle has SpacePosition
* fact SpacePosition has Coordinate(longitude,latitude)

* fact Battle has StartTime [millisecond]
>fact ra Battle has StartTime

* fact Battle has EndTime [millisecond,optional]
>fact ra Battle has EndTime

>table-entity ra Battle
>insert Battle AZ123 23,44 20190916:10:30 20190917:17:33
>insert Battle AZ125 43,11 20190416:10:30 20190617:17:33
>show-data Battle

>table-entity ra Rebel
>insert Rebel AZ123 John-Dow HUMAN



* facts Rebel
    takes part in Battle [0..n]
    has Name [key]
    has BeignType {human, alien, robot}
    beign relative with Rebel [irreflexive]
    died in Battle [subset of take part in]
    is a Jedi,Fighter [partition]

* facts Battle
    has code [key]
    has SpacePosition
    has StartTime [millisecond]
    has EndTime [millisecond,optional]

* facts Jedi
    is a Knight, Padawan [not-overlapping,not-total]

* insert Rebel
    has Name? [text]
    has BeignType? {human, alien, robot}
    beign relative with Rebel? [rebel, irreflexive]
    takes part in Battle(s)? a,b,c
    died in Battle? [battle]
    is a Jedi,Fighter [partition]?
    [if Jedi] is a Knight,Padawan [non-overlapping,non-total]?
    [if Padawan] is trained by Knight? [knight]
    [if Knight] trains Padawan(s)? e,f,g
    [if Fighter] has experience Level? [level]



* bot which Rebels taken part in ?Battle ?
*  a1=select Rebels join Battle where Battle is ?Battle

exposed as 'http://localhost:4567/data/takenPart'

>sql takenPart select * from Rebel join Battle on Rebel.Battle=Battle.Code


* bot there are Rebels relative to ?Rebel who died ?
* a2=select Rebel r1 join Rebel r2 where r2 is relative r1 and r1 in a1

* bot in which Battle ?
* a3= select Battle join Rebel where r in a2


