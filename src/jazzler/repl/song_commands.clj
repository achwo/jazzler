(ns jazzler.repl.song-commands
  (:require [jazzler.repl.io :as io]
            [jazzler.repl.runtime :as r]
            [jazzler.song :as s]
            [jazzler.parser.system :as p]
            [jazzler.player.system :as pl]
            [clojure.string :as str]))

(defn- transform-title [[title]] title)
(defn- transform-tempo [[tempo]] (Integer. tempo))

(defn language_command [ctx parse]
  (let [new-song (s/merge-songs (r/song ctx) parse)
        errors (s/check new-song)]
    (if (empty? errors)
      (do (if-not (nil? (s/structure parse))
            (r/song ctx (s/structure (r/song ctx) (s/structure parse)))
            (r/song ctx new-song)))
      (r/error ctx 
               (str errors)))))

(defn unknown
  [ctx args]
  (let [input (str/join " " args)
        parse (p/parse-all input)]
    (if (p/valid? parse)
      (language_command ctx parse)
      (do 
        ;; (println parse)
        (r/error ctx
                   (str "Unknown command: " args))))))

(defn exit [ctx args]
  (r/shutdown ctx))

(defn play
  [ctx _]
  (if-not (nil? (s/structure (r/song ctx)))
    (do (pl/play-song (r/song ctx))
        ctx)
    (r/error ctx "There is not song structure to be played!")))

(defn info
  [ctx _]
  (r/result ctx ctx :pprint))

(defn song
  [ctx _]
  (r/result ctx (r/song ctx) :pprint))

(def help-s
  {:general "You are in song mode. The following commands are available:

help: Shows this help screen
help <command|element>: Shows detail info on the command or element.

info: Shows the current datastructure of the repl
song: Shows the current datastructure of the song
play: Plays the song back

exit: Quit the application

Use 'help <command>' for more info and syntactic information."
   :exit "Quits the application."
   :info "Shows the current datastructure of the repl."
   :song "Shows the current datastructure of the song."})

(defn help
  [ctx [cmd-str & [detail]]]
  (if-let [helptext (help-s (keyword detail))]
    (r/result ctx helptext)
    (r/result ctx (:general help-s))))

(def commands
  {:help help
   :info info
   :song song
   :play play
   :exit exit
   :quit exit
   })

(defn command 
  "Returns a tuple with a fn and a seq of arguments."
  [s]
  (let [words (str/split s #"\s+" 2)]
    [((keyword (first words)) commands unknown) words]))
