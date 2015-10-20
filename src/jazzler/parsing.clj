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
   "nl = #'\\n+'"))

(def progression-grammar 
  (str
   "progression = <'['>barOrChord? (<ws> barOrChord)* <']'> "
   "<barOrChord> = bar | bchord "
   "bar = <'['> chord (<ws> chord)* <']'> "
   "bchord = chord "
   "chord = 'I' | 'II' | 'III' | 'IV' | 'V' |'VI' | 'VII' "
   "ws = #'\\s+'"))

(def song-grammar
  (str 
   "song = title <nl> progression " 
   "title = <'Song:'> <ws> name "
   "<name> = #'[A-Za-z0-9 ]+'"
   progression-grammar
   general-grammar))

(def song-parser
  (i/parser song-grammar
   ))

(def progression-parser (i/parser progression-grammar))

(def transformations
  {:progression progression
   :bchord barchord
   :chord str
   :bar bar})

(defn parse-progression [string]
  (->> string
       (progression-parser)
       (i/transform transformations)))

(defn parse-song [string]
  (->> string
       (song-parser)
       (i/transform transformations)
       ))
