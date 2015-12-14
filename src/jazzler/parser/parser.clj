(ns jazzler.parser.parser
  (:require [instaparse.core :as i]))

(def song-grammar
  (str 
   "<song> = title <nl> content <nl> structure "

   "title = <'Song:' | 'Title:'> <wsfull> title-value "
   "<title-value> = name "
   
   "tempo = <'Tempo:'> <ws> tempo-value "
   "<tempo-value> = number "

   "<content> = figdefs "
   "<figdefs> = figdef (<nl> figdef)* "

   "<barOrChord> = bar | bchord "
   "bar = <'['> chord (<wsfull> chord)* <']'> "
   "bchord = chord "

   "<chord> = majorchord | minorchord | diminished | augmented "
   "majorchord = 'I' | 'II' | 'III' | 'IV' | 'V' |'VI' | 'VII' "
   "minorchord = 'i' | 'ii' | 'iii' | 'iv' | 'v' | 'vi' | 'vii' "
   "diminished = minorchord <'o'> "
   "augmented = majorchord <'+'> "

   "figdef = figsym <ws> <'='> <ws> progression "
   "progression = <'['>barOrChord? (<wsfull> barOrChord)* <']'> "
   "structure = <'Structure'> <eol> structureContent "
   "<structureContent> = <ws> figsym (<wsfull> figsym)* "

   "<name> = #'[A-Za-z0-9 ]+' "
   "figsym = #'[A-Z][a-z]*' "
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
