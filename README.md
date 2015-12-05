# jazzler

A Clojure library designed to compose jazz music.

## Usage

`lein run` to start the REPL, which can be used to evaluate progressions.

## REPL commands

The following commands are available:

help => shows this help screen

help <command> => shows detail info on the command

title <arg?> => shows or sets (if no arg given) the title value 

progression <arg?>=> shows or sets (if no arg given) the progression value

exit, quit => quit the application

## Syntax

The Jazzler language uses roman numerals to represent chords. The numerals indicate the scale degree according to the root note, which is defined by the scale of the song.
At the moment, only triads are supported:

### Major Triads
Major triads are represented by uppercase roman numerals: `I`.
Minor triads are represented by lowercase roman numerals: `i`.
Diminished triads are represented by lowercase roman numerals followed by an 'o': `io`.
Augmented triads are represented by uppercase roman numerals followed by an '+': `i+`.

A song consists of a title and a progression. The title is a string of at least one letter or number.
A progression is a sequence of bars to define the harmonic structure of a song. It is surrounded by `[]` and contains any amount of bars.
A bar consists of at least one chord symbol. To add a bar with only one chord, you can simply write the chord symbol, otherwise you have to surround a bar with `[]`.

`[I]` and `[[I]]` both describe a progression of one bar with the chord of the first scale degree.

## Example

```
Song: Demosong
[I ii [iiio IV+ V] VI [vii I] I]
```

## Further Information
For more information on Roman Numeral Analysis, see [Wikipedia: Roman Numeral Analysis](https://en.wikipedia.org/wiki/Roman_numeral_analysis).

## License

Copyright © 2015 Felix Jensen

Distributed under the GNU GENERAL PUBLIC LICENSE Version 3.
