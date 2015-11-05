(ns jazzler.parsing
  (:gen-class)
  (:require [clojure.string :as s]
            [instaparse.core :as i]))

(defn string->degree 
  "Converts a string degree into a keyword degree as used in overtone
  Example: \"I\" => :i"
  [s] 
  {:pre [(string? s)]}
  (keyword (s/lower-case s)))

(defn progression 
  [& content]
  (cond
    (seq? content) [:progression content]
    (nil? content) [:progression '()]
    ;; :else [:progression (list content)];unused
))

(defn title [title] {:title title})

(defn add-bar-numbers [bars]
  (map #(assoc %1 :bar %2) bars (iterate inc 1)))

(defn bar [& content] {:elements content})

(defn barchord [content] {:elements (list content)})

(defn majorchord [root] {:chord (string->degree root) 
                         :triad :major})

(defn minorchord [root] {:chord (string->degree root) 
                         :triad :minor})

(defn diminished [{root :chord}] {:chord root, :triad :diminished})

(defn augmented [{root :chord}] {:chord root, :triad :augmented})

(def song-grammar
  (str 
   "song = title <nl> progression "
   "title = <'Song:'> <wsfull> name "
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

(def transformations
  {:progression progression
   :bchord barchord
   :majorchord majorchord
   :minorchord minorchord
   :diminished diminished
   :augmented augmented
   :title title
   :bar bar})

(defn parse-progression [string]
  (let [prog-parser #(song-parser % :start :progression)]
    (->> string
         (prog-parser)
         (i/transform transformations))))

(defn parse-song [string]
  (->> string
       (song-parser)
       (i/transform transformations)))
