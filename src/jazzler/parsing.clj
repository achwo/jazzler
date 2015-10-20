(ns jazzler.parsing
  (:gen-class)
  (:require [instaparse.core :as i]))

(defn progression 
  [& content]
  (cond
    (seq? content) [:progression content]
    (nil? content) [:progression '()]
    :else [:progression (list content)];unused
))

(defn bar [& content]
  [:bar content])

(defn barchord [content]
  [:bar (list content)])

(def general-grammar
  (str
   "eol = ws nl "
   "ws = #'[ \t]*' "
   "nl = #'\\n+'"
   "wsfull = #'\\s+'"
))

(def title-grammar
  (str 
   "title = <'Song:'> <wsfull> name "
   "<name> = #'[A-Za-z0-9 ]+'"))

(def progression-grammar 
  (str
   "progression = <'['>barOrChord? (<wsfull> barOrChord)* <']'> "
   "<barOrChord> = bar | bchord "
   "bar = <'['> chord (<wsfull> chord)* <']'> "
   "bchord = chord "
   "chord = 'I' | 'II' | 'III' | 'IV' | 'V' |'VI' | 'VII' "
   ))

(def structure-grammar
  (str
   "structure = <'Structure'> <eol> structureContent "
   "<structureContent> = <ws> figSym (<wsfull> figSym)* "
   "figSym = #'[A-Z][a-z]*' "
   ))

;; Difference between ws and wsfull:
;; wsfull = [ \t\n\x0B\f\r]
;; ws = [ \t]
;; Most importantly: wsfull also contains newline characters
;; for more info, see: http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html

(def song-grammar
  (str 
   "song = title <nl> progression " 
   title-grammar
   progression-grammar
   general-grammar))

(def song-parser
  (i/parser song-grammar
   ))

(def progression-parser (i/parser (str progression-grammar
                                       general-grammar)))

(def progression-transformations
  {:progression progression
   :bchord barchord
   :chord str
   :bar bar})

(defn parse-progression [string]
  (->> string
       (progression-parser)
       (i/transform progression-transformations)))

(defn parse-song [string]
  (->> string
       (song-parser)
       (i/transform progression-transformations)
       ))
