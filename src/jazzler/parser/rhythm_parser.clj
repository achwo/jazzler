(ns jazzler.parser.rhythm-parser
  (:require [instaparse.core :as i]))

(def rhythm-grammar
  (str 
   "newstart = author (<wsfull> author)* "
   "content = complexprog | rhythmdef | author " 
   "complexprog = progression <wsfull?> rhythmblock? "
   "progression = <'['>barOrChord? (<wsfull> barOrChord)* <']'> "
   "rhythmdef = <'Rhythm:'> <wsfull> rhythmblock "
   "rhythmblock = '{' rhythmElement (<wsfull> rhythmElement)* '}' "
   
   "rhythmElement = topnum <'/'> bottomnum | '1' "
   "<topnum> = '1' | '2' | '4' | '8' | '16' | '32' "
   "<bottomnum> = '1' | '2' | '4' | '8' | '16' | '32' "
   
   "author = <'Author:'> <ws> text "

   "barOrChord = (bar | bchord) <wsfull?> rhythmblock? "
   "bar = <'['> chord (<wsfull> chord)* <']'> "
   "bchord = chord "

   "chord = 'C' "
   
   "text = #'[A-Za-z: ]*' "

   "ws = #'[ \t]*' "
   "nl = #'\\n+' "
   "wsfull = #'\\s+'"
))

;; Difference between ws and wsfull:
;; wsfull = [ \t\n\x0B\f\r]
;; ws = [ \t]
;; Most importantly: wsfull also contains newline characters
;; for more info, see: http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html

;(def rhythm-parser (i/parser rhythm-grammar :start :content))
(def rhythm-parser (i/parser rhythm-grammar :start :newstart))


;; call like (rhythm-parser "Author: A")
;; debugging: (i/parses rhythm-parser "..") for ambiguous grammar check
