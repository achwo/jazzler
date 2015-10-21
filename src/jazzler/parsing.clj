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

(defn chord [[_ name]] name )

(defn majorchord [name]
  [:chord name :major])

(defn minorchord [name]
  [:chord name :minor])

(def song-grammar
  (str 
   "song = title <nl> progression "
   "title = <'Song:'> <wsfull> name "
   "<name> = #'[A-Za-z0-9 ]+'"
   "progression = <'['>barOrChord? (<wsfull> barOrChord)* <']'> "
   "<barOrChord> = bar | bchord "
   "bar = <'['> chord (<wsfull> chord)* <']'> "
   "bchord = chord "
   "<chord> = (majorchord | minorchord) "
   "majorchord = 'I' | 'II' | 'III' | 'IV' | 'V' |'VI' | 'VII' "
   "minorchord = 'i' | 'ii' | 'iii' | 'iv' | 'v' | 'vi' | 'vii' "
   "structure = <'Structure'> <eol> structureContent "
   "<structureContent> = <ws> figSym (<wsfull> figSym)* "
   "figSym = #'[A-Z][a-z]*' "
   "eol = ws nl "
   "ws = #'[ \t]*' "
   "nl = #'\\n+'"
   "wsfull = #'\\s+'"
))
;; Difference between ws and wsfull:
;; wsfull = [ \t\n\x0B\f\r]
;; ws = [ \t]
;; Most importantly: wsfull also contains newline characters
;; for more info, see: http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html

(def song-parser
  (i/parser song-grammar))

(def progression-transformations
  {:progression progression
   :bchord barchord
;   :chord chord
   :majorchord majorchord
   :minorchord minorchord
   :bar bar})

(defn parse-progression [string]
  (let [prog-parser #(song-parser % :start :progression)]
    (->> string
         (prog-parser)
         (i/transform progression-transformations))))

(defn parse-song [string]
  (->> string
       (song-parser)
       (i/transform progression-transformations)))
