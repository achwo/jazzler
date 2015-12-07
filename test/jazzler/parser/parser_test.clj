(ns jazzler.parser.parser-test
  (:use midje.sweet)
  (:require [jazzler.parser.system :refer [failure? parse parse-title]]
            [jazzler.parser.parser :refer :all]))

(facts "about title"
  (fact "it must not be empty"
    (failure? (parse-title "Song:")) => true
    (failure? (parse-title "Song: ")) => true)
  (fact "it can be a word of one letter or number"
    (parse-title "Song: a") => {:title "a"}
    (parse-title "Song: 1") => {:title "1"})
  (fact "it can be a word of more than one letter"
    (parse-title "Song: a1bcd3") => {:title "a1bcd3"})
  (fact "it can consist of several words"
    (parse-title "Song: Bizarre Love Triangle") 
    => {:title "Bizarre Love Triangle"}))

(facts "about structure"
  (let [parse-structure (partial parse :structure)]
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
      => [:structure 
          [:figSym "Intro"] 
          [:figSym "Verse"] 
          [:figSym "Outro"]])))

(facts "about major chords"
  (let [parse-major (partial parse :majorchord)]
    (fact "major triads are written as uppercase roman numerals"
      (parse-major "I") => {:chord :i, :triad :major}
      (parse-major "IV") => {:chord :iv, :triad :major}
      (parse-major "VII") => {:chord :vii, :triad :major})
    (fact "edge cases"
      (every? failure? [(parse-major "VIII")
                          (parse-major "IIII")
                          (parse-major "VV")
                          (parse-major "")
                          (parse-major "IIV")]) => true)))

(fact "minor triads are written as lowercase roman numerals"
  (let [parse-minor (partial parse :minorchord)]
    (parse-minor "ii") => {:chord :ii, :triad :minor}
    (parse-minor "vii") => {:chord :vii, :triad :minor}))

(facts "about diminished chords"
  (let [parse-dim (partial parse :diminished)]
    (fact "they are written as lowercase roman numerals followed by o"
      (parse-dim "io") => {:chord :i, :triad :diminished}
      (parse-dim "ivo") => {:chord :iv, :triad :diminished}
      (parse-dim "iiio") => {:chord :iii, :triad :diminished})
    (fact "they don't accept uppercase roman numerals"
      (failure? (parse-dim "Io")) => true)))

(facts "about augmented chords"
  (fact "they are written as uppercase roman numerals followed by +"
    (parse :augmented "I+") => {:chord :i, :triad :augmented}))

(future-facts "about variable definitions"
  (fact "they can be parsed"
    (failure? (parse :vardef "Varname = [I]")) =not=> true)
  (fact ""
    (parse :vardef "Varname = [I]") => nil)

  )

;; todo
;; - it's reachable from main parsing
;; - a symbol table is build
