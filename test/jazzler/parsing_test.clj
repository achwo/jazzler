(ns jazzler.parsing-test
  (:use midje.sweet)
  (:require [jazzler.parsing :refer :all]
            [instaparse.core :as i]))

(facts "about parse progression"
  (fact "it can be empty"
    (parse-progression "[]") => [:progression '()])
  (fact "it can have bars with one chord"
    (parse-progression "[[I]]") 
    => [:progression (list [:bar (list "I")])])
  (fact "it can have aliases of chordname for a whole bar"
    (parse-progression "[I]") 
    => [:progression (list [:bar (list "I")])])
  (fact "a bar can contain more than one chord"
    (parse-progression "[[I IV]]") 
    => [:progression (list [:bar (list "I" "IV")])])
  (fact "it can contain more than one bar"
    (parse-progression "[[I] [IV]]") 
    => [:progression (list [:bar (list "I")]
                           [:bar (list "IV")])]
    (parse-progression "[I IV]") 
    => [:progression (list [:bar (list "I")]
                           [:bar (list "IV")])])
)

(defn parse-title [string]
  (->> string
       ((i/parser (str title-grammar general-grammar)))))

(facts "about title"
  (fact "it must not be empty"
    (i/failure? (parse-title "Song:")) => true
    (i/failure? (parse-title "Song: ")) => true)

  (fact "it can be a word of one letter or number"
    (parse-title "Song: a") => [:title "a"]
    (parse-title "Song: 1") => [:title "1"])

  (fact "it can be a word of more than one letter"
    (parse-title "Song: a1bcd3") => [:title "a1bcd3"])

  (fact "it can consist of several words"
    (parse-title "Song: Bizarre Love Triangle") 
    => [:title "Bizarre Love Triangle"])
)

(facts "about song-parser"
  (fact "basic song structure"
    (parse-song "Song: bla\n[I II III]")
    => [:song [:title "bla"] 
        [:progression (list [:bar (list "I")]
                            [:bar (list "II")]
                            [:bar (list "III")])]])
)

(defn parse-structure [string]
  (->> string
       ((i/parser (str structure-grammar general-grammar)))
))

(facts "about structure"
  (fact "it has at least one figure"
    (parse-structure "Structure\nIntro") 
    => [:structure [:figSym "Intro"]])

  (fact "it can have more than one figure in one line"
    (parse-structure "Structure\nIntro Verse")
    => [:structure [:figSym "Intro"] [:figSym "Verse"]])

  (fact "it can have more than one line"
    (parse-structure "Structure\nIntro\nChorus Verse\nChorus\nOutro")
    => [:structure 
        [:figSym "Intro"] 
        [:figSym "Chorus"] [:figSym "Verse"]
        [:figSym "Chorus"]
        [:figSym "Outro"]])

  (fact "it can have whitespace almost anywhere"
    (parse-structure "Structure\n   Intro")
    => [:structure [:figSym "Intro"]]
    (parse-structure "Structure \nIntro")
    => [:structure [:figSym "Intro"]]
    (parse-structure "Structure\n\n Intro   Verse\n\tOutro")
    => [:structure [:figSym "Intro"] [:figSym "Verse"] [:figSym "Outro"]])
)
