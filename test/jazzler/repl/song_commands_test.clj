(ns jazzler.repl.song-commands-test
  (:use midje.sweet)
  (:require [jazzler.repl.song-commands :refer :all]
            [jazzler.parser.system :refer [parse-figdef parse-figsym]]
            [jazzler.song :refer [chord]]
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
    (command "") => [unknown [""]]))

(facts "about help"
  (fact "it returns a help string"
    (keys (help {} ["help"])) => (contains :result))
  (fact "it returns help string for each command"
    (:result (help {} ["help" "title"])) => (:title help-s)
    (:result (help {} ["help" "exit"])) => (:exit help-s)
    (:result (help {} ["help" "structure"])) => (:structure help-s)
    (:result (help {} ["help" "progression"])) => (:progression help-s)
    (:result (help {} ["help" "figure"])) => (:figure help-s))
  (fact "if command is unknown, it returns general help"
    (:result (help {} ["help" "what"])) => (:general help-s)))

(facts "about structure"
  (fact "it returns the current structure"
    (structure {:song {:structure "Struct"}} ["structure"]) 
    => {:song {:structure "Struct"} :result "Struct"})
  (fact "it returns an error, if figure not defined"
    (keys (structure {} ["structure" "Intro"]))
    => (contains :error))
  (fact "it sets the structure"
    (structure {:song {:figures {"Intro" 1 "Outro" 2}}} 
               ["structure" "Intro Outro"])
    => {:song {:figures {"Intro" 1 "Outro" 2}
               :structure ["Intro" "Outro"]}})
  (fact "it returns an error, if syntactically incorrect"
    (keys (structure {} ["structure" "34"])) 
    => (contains :error)))

(facts "about figdef"
  (future-fact "it returns the value of the figure"
    (figval {:song {:figures {"Fig" 1}}} (parse-figsym "Fig"))
    => {:song {:figures {"Fig" 1}} :result "Fig"})
  (fact "it adds the figure to the song"
    (figdef {} (parse-figdef "Fig = [I]"))
    => {:song {:figures {"Fig" [{:elements [(chord :i :major)]}]}}})
  (fact "it adds to and not overrides :figures"
    (figdef {:song {:figures {"F" 1}}} (parse-figdef "Fig = [I]"))
    => {:song {:figures 
               {"F" 1
                "Fig" [{:elements [(chord :i :major)]}]}}}))

(facts "about info"
  (fact "it shows the datastructure of the repl"
    (info {:song {:bpm 120}} []) 
    => {:song {:bpm 120} 
        :result {:song {:bpm 120}}
        :print-options [:pprint]}))

(facts "about song"
  (fact "it shows nil, if the song isn't defined"
    (song {} ["song"]) => {:result nil :print-options [:pprint]})
  (fact "it shows the song structure, if it is defined"
    (song {:song {:bpm 120}} []) => {:song {:bpm 120} 
                                     :result {:bpm 120}
                                     :print-options [:pprint]}))
