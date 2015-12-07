(ns jazzler.parser.parser
  (:require [instaparse.core :as i]))

(def song-grammar
  (str 
   "<song> = title <nl> progression "
   "title = <'Song:' | 'Title:'> <wsfull> name "
   "<title-value> = name"
   "<name> = #'[A-Za-z0-9 ]+'"
   "progression = <'['>barOrChord? (<wsfull> barOrChord)* <']'> "
   "<barOrChord> = bar | bchord "
   "bar = <'['> chord (<wsfull> chord)* <']'> "
   "bchord = chord "
   "<chord> = majorchord | minorchord | diminished | augmented "
   "majorchord = 'I' | 'II' | 'III' | 'IV' | 'V' |'VI' | 'VII' "
   "minorchord = 'i' | 'ii' | 'iii' | 'iv' | 'v' | 'vi' | 'vii' "
   "diminished = minorchord <'o'> "
   "augmented = majorchord <'+'> "
   ;; "vardef = figSym <ws> '=' <ws> varprog "
   ;; "varprog = progression "
   "structure = <'Structure'> <eol> structureContent "
   "<structureContent> = <ws> figSym (<wsfull> figSym)* "
   "figSym = #'[A-Z][a-z]*' "
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
