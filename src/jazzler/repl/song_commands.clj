(ns jazzler.repl.song-commands
  (:require [jazzler.repl.io :as io]
            [jazzler.repl.runtime :as r]
            [jazzler.song :as s]
            [jazzler.parser.system :as p]
            [clojure.string :as str]))

(defn- transform-title [[title]] title)

(defn figdef [ctx [_ figsym prog]]
  ;; INFO: Does not behave like the others, 
  ;;       because it does not have a keyword.
  ;; (r/song ctx (merge (r/song ctx) parse))
  (r/song ctx (s/figure (r/song ctx)
                        figsym prog)))

(defn figval [ctx ])

(defn unknown 
  "If command is not found in commands list, this will be used.
  Since figure definition does not have a keyword, it is processed here."
  [ctx args]
  (let [input (str/join " " args)
        fig-parse (p/parse-figdef input)]
    (if (p/valid? fig-parse)
      (figdef ctx fig-parse)
      (r/error ctx 
               (str "Unknown command: " (first args) " " (rest args))))))

(defn exit [ctx args]
  (r/shutdown ctx))

(defn title 
  [ctx [command-string & [title-string]]]
  (if (nil? title-string)
    (r/result ctx (s/title (r/song ctx)))
    (let [title-parse (p/parse-title-val title-string)] 
      (if (p/failure? title-parse)
        (r/error ctx "The given title is invalid!")
        (r/song ctx (s/title (r/song ctx) 
                             (transform-title title-parse)))))))

(defn structure
  [ctx [cmd-str & [struc-str]]]
  (if (nil? struc-str)
    (r/result ctx (s/structure (r/song ctx)))
    (let [struc-parse (p/parse-structure struc-str)]
      (if (p/failure? struc-parse)
        (r/error ctx "The given structure is invalid!")
        (if-not (every? #(s/figure (r/song ctx) %) struc-parse)
          (r/error ctx "At least one figure is not defined!")
          (r/song ctx (s/structure (r/song ctx) struc-parse)))))))

(def help-s
  {:general "The following commands are available:
help shows this help screen
help <command> shows detail info on the command
title <arg?> shows or sets (if no arg given) the title value 
structure <args> shows or sets the structure
<Figurename> = <prog> defines a Figure, see 'help figure'
exit, quit quit the application

Use 'help <command>' for more info and syntactic information."
   :title "If used without args, it returns the current title value.
A valid title string can contain of upper- and lowercase letters,
numbers and spaces."
   :exit "Quits the application."
   :structure "Defines the structure of the song:

It contains of a list of figure names. The figures have to be defined.
Example: structure Intro Refrain Outro"
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
I. e. [I [IV I] V I] would contain four bars."
   :figure "Figure Definition

Define a figure with '<figurename> = <progression>'
Figurename has to be a single uppercase word.
Example: Intro = [I [IV I]]
For progression syntax, see 'help progression'"})

(defn help
  [ctx [cmd-str & [detail]]]
  (if-let [helptext (help-s (keyword detail))]
    (r/result ctx helptext)
    (r/result ctx (:general help-s))))

(def commands
  {:help help
   :title title
   :structure structure
   :exit exit
   :quit exit
   })

(defn command 
  "Returns a tuple with a fn and a seq of arguments."
  [s]
  (let [words (str/split s #"\s+" 2)]
    [((keyword (first words)) commands unknown) words]))
