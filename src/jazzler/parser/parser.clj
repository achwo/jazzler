(ns jazzler.parser.parser
  (:require [instaparse.core :as i]))

(def song-grammar
  (str 
   "<song> = title <nl> content <nl> structure <wsfull*>"
   "<all> = title | figdef | tempo | scale | structureLine "

   "title = <'Song:' | 'Title:'> <wsfull> title-value "
   "<title-value> = name "
   
   "tempo = <'Tempo:'> <ws> tempo-value "
   "<tempo-value> = number "
   
   "scale = <'Key:'> <ws> scale-value "
   "scale-value = scaleroot <' '> mode "

   "scaleroot = normalroot | sharproot | flatroot "
   "<normalroot> = #'[A-G]' "
   "<sharproot> = 'C#' | 'D#' | 'F#' | 'G#' | 'A#' "
   "<flatroot> = 'Db' | 'Eb' | 'Gb' | 'Ab' | 'Bb' "
   
   "mode = 'Major' | 'major' | 'Minor' | 'minor' | 'Aeolian' | 'Ionian' | 'Dorian' | 'Phrygian' | 'Lydian' | 'Mixolydian' | 'Locrian' "

   "<content> = figdefs "
   "<figdefs> = figdef (<nl> figdef)* "

   "<barOrChord> = bar | bchord "
   "bar = <'['> chord? (<wsfull> chord)* <']'> "
   "bchord = chord "
   
   "chord = root quality? intervalnum? fifth? "
   "root = 'I' | 'II' | 'III' | 'IV' | 'V' |'VI' | 'VII' "
   "quality = '-' | '+' | 'o' "
   "intervalnum = six | seven | nine | eleven "
   "<six> = 'b'? '6' "
   "<seven> = '7' | 'maj7' "
   "<nine> = ('b' | '#')? '9' "
   "<eleven> = '#'? '11' "
   "fifth = '#5' | 'b5' | 'sus4' "

   "figdef = figsym <ws> <'='> <ws> progression "
   "progression = <'['>barOrChord? (<wsfull> barOrChord)* <']'> "
   "structure = <'Structure'> <eol> structureContent "
   "<structureContent> = <ws> figsym (<wsfull> figsym)* "
   
   "structureLine = <'Structure:'> <ws> structureContentLine "
   "<structureContentLine> = figsym (<ws> figsym)* "

   "<name> = #'[A-Za-z0-9 ,.<>;:äöüß_?!§$%&/()=*+#-]+' "
   "figsym = #'[A-Z][a-z0-9]*' "
   "<number> = #'\\d+' "

   "eol = ws nl "
   "ws = #'[ \t]*' "
   "nl = #'\\n+' "
   "wsfull = #'\\s+'"
))

;; Difference between ws and wsfull:
;; wsfull = [ \t\n\x0B\f\r]
;; ws = [ \t]
;; Most importantly: wsfull also contains newline characters
;; for more info, see: http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html

(def song-parser (i/parser song-grammar))
