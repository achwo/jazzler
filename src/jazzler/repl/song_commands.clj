(ns jazzler.repl.song-commands
  (:require [jazzler.repl.io :as io]
            [jazzler.repl.runtime :as r]
            [jazzler.song :as s]
            [jazzler.parsing :as p]
            [clojure.string :as str]))

;; TODO: it would be really nice to have an exception if this fails on use
(def title-parser #(p/song-parser % :start :title-value))

(defn- transform-title [[title]] title)

(defn unknown [ctx args]
  (r/error ctx (str "Unknown command: " (first args) " " (rest args))))

(defn exit [ctx args]
  (r/shutdown ctx))

(defn title 
  [ctx [command-string & [title-string]]]
  (if (nil? title-string)
    (r/result ctx (s/title (r/song ctx)))
    (let [title-parse (title-parser title-string)] 
      (if (= (type title-parse) instaparse.gll.Failure)
        (r/error ctx "The given title is invalid!")
        (r/song ctx (s/title (r/song ctx) 
                             (transform-title title-parse)))))))

;; TODO: remove duplication between title and progression
(defn progression
  [ctx [cmd-str & [prog-str]]]
  (if (nil? prog-str)
    (r/result ctx (s/progression (r/song ctx)))
    (let [prog-parse (p/parse-progression prog-str)]
      (if (= (type prog-parse) instaparse.gll.Failure)
        (r/error ctx "The given progression is invalid!")
        (r/song ctx (s/progression (r/song ctx) prog-parse))))))

(def help-s
  {:general "The following commands are available:
help => shows this help screen
help <command> => shows detail info on the command
title <arg?> => shows or sets (if no arg given) the title value 
progression <arg?>=> shows or sets (if no arg given) the progression value
exit, quit => quit the application

Use 'help <command>' for more info and syntactic information."
   :title "If used without args, it returns the current title value.
A valid title string can contain of upper- and lowercase letters,
numbers and spaces."
   :exit "Quits the application."
   :progression "A valid progression consists of:

Chord: A chord is represented by roman numerals: 
- I - VII for major chords
- i - vii for minor chords
- minor chord + o for diminished, i. e. iiio
- major chord + + for augmented, i. e. V+

Bar: A bar consists of outer square braces and a number of chords.
i. e. [I II] is one bar of chord I and II, equally devided.
A bar can also be represented by only a chord, if the whole bar
consists of only this chord. I. e. V is one bar of V.

Progression: A progression is a number of bars within square braces.
I. e. [I [IV I] V I] would contain for bars."})

(defn help
  [ctx [cmd-str & [detail]]]
  (if-let [helptext (help-s (keyword detail))]
    (r/result ctx helptext)
    (r/result ctx (:general help-s))))

;; TODO: remove duplication between this and jazzler.repl.commands
(def commands
  {:help help
   :title title
   :exit exit
   :quit exit
   :progression progression})

(defn command 
  "Returns a tuple with a fn and a seq of arguments."
  [s]
  (let [words (str/split s #"\s+" 2)]
    [((keyword (first words)) commands unknown) words]))
