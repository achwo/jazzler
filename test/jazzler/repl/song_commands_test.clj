(ns jazzler.repl.song-commands-test
  (:use midje.sweet)
  (:require [jazzler.repl.song-commands :refer :all]
            [clojure.string :as str]))

(facts "about title"
  (let [a-command ["title" "Titel des Jahres"] 
        a-title "Titel des Jahres"
        a-system {:song {:title a-title}}]
    (fact "it sets the title of the systems song to the parsed value"
      (title {} a-command) => a-system)
    (fact "it overrides an existing title"
      (title {:song {:title "Another title"}} a-command) => a-system)
    (fact "it returns the current title, when args are empty"
      (title a-system ["title"]) => (assoc a-system :result a-title))))

(fact "unknown returns an error"
  (keys (unknown {} ["not-known" "arg"])) => (contains #{:error}))

(facts "about command"
  (fact "it returns a seq of a fn and a list with command- and args-string"
    (command "title Song Title") => [title ["title" "Song Title"]])
  (fact "it returns unknown as fn, if it does not exist"
    (command "what random args") => [unknown ["what" "random args"]])
  (fact "it treats empty string as unknown command"
    (command "") => [unknown [""]])
  (fact "it does not split progressions"
    (command "progression [I II]")
    => [progression ["progression" "[I II]"]]))

(facts "about progression"
  (fact "it returns the progression when used without args"
    (progression {:song {:progression :bla}} []) 
    => {:song {:progression :bla} :result :bla})
  (fact "when progression is not set, it returns nil"
    (progression {} []) => {:result nil})
  (fact "it sets the progression when used with args"
    (get-in (progression {} ["progression" "[I]"])
            [:song :progression :figures :progression]) =not=> nil)
  (fact "it returns an error when parse is unsuccessful"
    (keys (progression {} ["progression" "invalid"])) 
    => (contains :error)))

(facts "about help"
  (fact "it returns a help string"
    (keys (help {} ["help"])) => (contains :result))
  (fact "it returns help string for each command"
    (:result (help {} ["help" "title"])) => (:title help-s)
    (:result (help {} ["help" "title"])) =not=> nil
    (:result (help {} ["help" "exit"])) =not=> nil)
  (fact "if command is unknown, it returns general help"
    (:result (help {} ["help" "what"])) => (:general help-s)


)

)
