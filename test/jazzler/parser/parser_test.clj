(ns jazzler.parser.parser-test
  (:use midje.sweet)
  (:require [jazzler.parser.system :refer :all]
            [jazzler.parser.parser :refer :all]
            [jazzler.parser.transformer :as t]))

(defn chord [root triad] {:chord root, :triad triad})

;; TODO: use this for figprog testing
(facts "about parse progression"
  (let [chord-I (chord :i :major)
        chord-IV (chord :iv :major)]
    (fact "it can be empty"
      (parse-progression "[]") => nil)
    (fact "it can have bars with one chord"
      (parse-progression "[[I]]") 
      => [{:elements [chord-I]}])
    (fact "it can have aliases of chordname for a whole bar"
      (parse-progression "[I]") 
      => [{:elements [chord-I]}])
    (fact "a bar can contain more than one chord"
      (parse-progression "[[I IV]]") 
      => [{:elements [chord-I chord-IV]}])
    (fact "it can contain more than one bar"
      (parse-progression "[[I] [IV]]") 
      =>[{:elements [chord-I]} {:elements [chord-IV]}]
      (parse-progression "[I IV]") 
      => [{:elements [chord-I]}
          {:elements [chord-IV]}])))
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

(facts "about variable definitions"
  (fact "it has the correct structure"
    (parse :figdef "Varname = [I]") 
    => {:figures {"Varname" [{:elements [{:chord :i :triad :major}]}]}})
  (fact "it works for longer progressions"
    (parse :figdef "Fig = [[I II] III]")
    => {:figures {"Fig" [{:elements [{:chord :i :triad :major}
                                     {:chord :ii :triad :major}]}
                         {:elements [{:chord :iii :triad :major}]}]}})
  )

(facts "about song-parser"
  (fact "it parses a song, containing of title and a figure definition"
    (parse-song "Song: Song Name\nFigure = [I [ii]]\nStructure\nFigure")
    => {:title "Song Name"
        :figures {"Figure" [{:elements [{:chord :i :triad :major}]}
                            {:elements [{:chord :ii :triad :minor}]}]}
        :structure ["Figure"]}))

(facts "about structure"
  (let [parse-structure (partial parse :structure)]
    (fact "it has at least one figure"
      (parse-structure "Structure\nIntro") 
      => {:structure ["Intro"]})
    (fact "it can have more than one figure in one line"
      (parse-structure "Structure\nIntro Verse")
      => {:structure ["Intro" "Verse"]})
    (fact "it can have more than one line"
      (parse-structure "Structure\nIntro\nChorus Verse\nChorus\nOutro")
      => {:structure ["Intro" "Chorus" "Verse" "Chorus" "Outro"]})
    (fact "it can have whitespace almost anywhere"
      (parse-structure "Structure\n   Intro")
      => {:structure ["Intro"]}
      (parse-structure "Structure \nIntro")
      => {:structure ["Intro"]}
      (parse-structure "Structure\n\n Intro   Verse\n\tOutro")
      => {:structure ["Intro" "Verse" "Outro"]})))

