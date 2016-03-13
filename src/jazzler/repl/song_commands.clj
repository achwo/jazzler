(ns jazzler.repl.song-commands
  (:require [jazzler.repl.io :as io]
            [jazzler.repl.runtime :as r]
            [jazzler.song :as s]
            [jazzler.parser.system :as p]
            [jazzler.player :as pl]
            [clojure.string :as str]))

(defn- transform-title [[title]] title)
(defn- transform-tempo [[tempo]] (Integer. tempo))

(defn figdef [ctx [_ figsym prog]]
  ;; INFO: Does not behave like the others, 
  ;;       because it does not have a keyword.
  ;; (r/song ctx (merge (r/song ctx) parse))
  (r/song ctx (s/figure (r/song ctx)
                        figsym prog)))

(defn language_command [ctx parse]
  (let [new-song (s/merge-songs (r/song ctx) parse)
        errors (s/check new-song)]
    (if (empty? errors)
      (r/song ctx new-song)
      (r/error ctx 
               (str errors)))))

(defn unknown
  [ctx args]
  (let [input (str/join " " args)
        parse (p/parse-all input)]
    (if (p/valid? parse)
      (language_command ctx parse)
      (r/error ctx
               (str "Unknown command: " args)))))

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

(defn play
  [ctx _]
  (pl/play-song (r/song ctx))
  ctx)

(defn tempo
  [ctx [cmd-str & [tempo-str]]]
  (if (nil? tempo-str)
    (r/result ctx (s/tempo (r/song ctx)))
    (let [tempo-parse (p/parse-tempo-val tempo-str)]
      (if (p/failure? tempo-parse)
        (r/error ctx "The given tempo is invalid!")
        (r/song ctx (s/tempo (r/song ctx)
                             (transform-tempo tempo-parse)))))))

(defn scale
  [ctx [cmd-str & [scale-str]]]
  (if (nil? scale-str)
    (r/result ctx (s/scale (r/song ctx)))
    (let [scale-parse (p/parse-scale-val scale-str)]
      (if (p/failure? scale-parse)
        (r/error ctx "The given scale is invalid!")
        (r/song ctx (s/scale (r/song ctx) scale-parse))))))

(defn info
  [ctx _]
  (r/result ctx ctx :pprint))

(defn song
  [ctx _]
  (r/result ctx (r/song ctx) :pprint))

(def help-s
  {:general "You are in song mode. The following commands are available:

help: Shows this help screen
help <command>: Shows detail info on the command

title <arg?>: Shows or sets (if no arg given) the title value 
tempo <arg?>: Shows or sets the tempo, in bpm
scale <arg?>: Shows or sets the scale (only Major or Minor at the moment)
<Figurename> = <prog>: Defines a Figure, see 'help figure'
structure <args>: Shows or sets the structure

info: Shows the current datastructure of the repl
song: Shows the current datastructure of the song

exit: Quit the application

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
For progression syntax, see 'help progression'"
   :tempo "Shows or sets the speed in bpm.
Example: tempo 80"
   :scale "Shows or sets the scale.

A key can be:
- major: D# Major
- minor: Eb Minor"
   :info "Shows the current datastructure of the repl."
   :song "Shows the current datastructure of the song."})

(defn help
  [ctx [cmd-str & [detail]]]
  (if-let [helptext (help-s (keyword detail))]
    (r/result ctx helptext)
    (r/result ctx (:general help-s))))

(def commands
  {:help help
   :title title
   :structure structure
   :info info
   :song song
   :tempo tempo
   :scale scale
   :play play
   :exit exit
   :quit exit
   })

(defn command 
  "Returns a tuple with a fn and a seq of arguments."
  [s]
  (let [words (str/split s #"\s+" 2)]
    [((keyword (first words)) commands unknown) words]))
