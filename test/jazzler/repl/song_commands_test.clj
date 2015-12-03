(ns jazzler.repl.song-commands-test
  (:use midje.sweet)
  (:require [jazzler.repl.song-commands :refer :all]
            [clojure.string :as str]))

(facts "about title"
  (let [a-command (str/split "title Titel des Jahres" #"\s+")
        a-title (str/join " " (rest a-command))
        a-system {:song {:title a-title}}]
    (fact "it sets the title of the systems song to the parsed value"
      (title {} a-command) => a-system)
    (fact "it overrides an existing title"
      (title {:song {:title "Another title"}} a-command) => a-system)
    (fact "it returns the current title, when args are empty"
      (title a-system ["title"]) => (assoc a-system :result a-title))))
