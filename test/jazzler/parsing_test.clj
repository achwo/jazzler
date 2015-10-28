(ns jazzler.parsing-test
  (:use midje.sweet)
  (:require [jazzler.parsing :refer :all]
            [instaparse.core :as i]))

(facts "about parse progression"
  (fact "it can be empty"
    (parse-progression "[]") => [:progression '()])
  (fact "it can have bars with one chord"
    (parse-progression "[[I]]") 
    => [:progression (list {:figures (list {:chord "I" :triad :major})})])
  (fact "it can have aliases of chordname for a whole bar"
    (parse-progression "[I]") 
    => [:progression (list {:figures (list {:chord "I" :triad :major})})])
  (fact "a bar can contain more than one chord"
    (parse-progression "[[I IV]]") 
    => [:progression (list {:figures (list {:chord "I" :triad :major} 
                                         {:chord "IV" :triad :major})})]
    (fact "it can contain more than one bar"
      (parse-progression "[[I] [IV]]") 
      => [:progression (list {:figures (list {:chord "I" :triad :major})}
                             {:figures (list {:chord "IV" :triad :major})})]
      (parse-progression "[I IV]") 
      => [:progression (list {:figures (list {:chord "I" :triad :major})}
                             {:figures (list {:chord "IV" :triad :major})})])))

(defn start-at [flag string]
  (->> string
       (#(song-parser % :start flag))
       (i/transform transformations)))

(def parse-title (partial start-at :title))

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
    => [:title "Bizarre Love Triangle"]))

(facts "about parse-song"
  (fact "basic song structure"
    (parse-song "Song: bla\n[I+ ii III ivo]")
    => [:song [:title "bla"] 
        [:progression (list {:figures (list {:chord "I" :triad :augmented})}
                            {:figures (list {:chord "ii" :triad :minor})}
                            {:figures (list {:chord "III" :triad :major})}
                            {:figures (list {:chord "iv" :triad :diminished})})]]))


(def parse-structure (partial start-at :structure))

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
    => [:structure [:figSym "Intro"] [:figSym "Verse"] [:figSym "Outro"]]))

(def parse-major (partial start-at :majorchord))

(facts "about major chords"
  (fact "major triads are written as uppercase roman numerals"
    (parse-major "I") => {:chord "I" :triad :major}
    (parse-major "IV") => {:chord "IV" :triad :major}
    (parse-major "VII") => {:chord "VII" :triad :major})
  (fact "edge cases"
    (every? i/failure? [(parse-major "VIII")
                        (parse-major "IIII")
                        (parse-major "VV")
                        (parse-major "")
                        (parse-major "IIV")]) => true))

(def parse-minor (partial start-at :minorchord))

(facts "about minor chords"
  (fact "minor triads are written as lowercase roman numerals"
    (parse-minor "ii") => {:chord "ii" :triad :minor}
    (parse-minor "vii") => {:chord "vii" :triad :minor}))

(def parse-dim (partial start-at :diminished))

(facts "about diminished chords"
  (fact "they are written as lowercase roman numerals followed by o"
    (parse-dim "io") => {:chord "i" :triad :diminished}
    (parse-dim "ivo") => {:chord "iv" :triad :diminished}
    (parse-dim "iiio") => {:chord "iii" :triad :diminished})
  (fact "they don't accept uppercase roman numerals"
    (i/failure? (parse-dim "Io")) => true))

(def parse-aug (partial start-at :augmented))

(facts "about augmented chords"
  (fact "they are written as uppercase roman numerals followed by +"
    (parse-aug "I+") => {:chord "I" :triad :augmented}))

(facts "about add-bar-numbers" 
  (fact "the first bar is bar 1"
    (add-bar-numbers [{}]) => [{:bar 1}])
  (fact "the second bar is bar 2"
    (add-bar-numbers [{} {}]) => [{:bar 1} {:bar 2}])
  (fact "works for more bars"
    (add-bar-numbers [{} {} {}]) => [{:bar 1} {:bar 2} {:bar 3}]))
