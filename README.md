# PocketLang
A language derivated from brainfuck with pokemon names (And some cool stuff :P)

## Interpreter components
> - `int` pointer
> - `int[30000]` heap
> - `List<Integer>` stack

## Instructions
(All pokemon names are available in french, english and japanese)
#### Brainfuck like
> - xerneas as >
> - yveltal as <
> - plusle as +
> - minun as -
> - smeargle as .
> - pikachu as ,
> - reshiram as [
> - zekrom as ]

#### PocketLang
>**porygon**
>clear the actual pointed value (set it to 0)

----------

>**munchlax**
>act as nanosleep
>sleep n milis with n as the pointed value

----------

>**snorlax**
>act as sleep
>sleep n seconds with n as the pointed value

----------

>**chatot**
>play piano and drum notes
>the pointed value is decomposed in two parts
>the most significant bit represent the instrument (0 = piano and 1 = drum)
>the 7 other bits represent the note height

----------

>**groudon**
>add the pointed value from the heap to the stack

----------

>**kyogre**
>get the pointed value from the heap and delete it from the stack

----------

>**porygon2**
>clear all the stack entries

----------

>**loudred**
>send a popup notice with the text contained in the stack

----------

>**exploud**
>send a popup notice with the text contained in the stack and prompt for a text to set in the stack