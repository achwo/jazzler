(ns jazzler.repl.song-commands
  (:require [jazzler.repl.io :as io]
            [jazzler.repl.runtime :as rt]
            [jazzler.song :as song]
            [jazzler.parsing :as p]
            [clojure.string :as s]))

;; TODO: it would be really nice to have an exception if this fails on use
(def title-parser #(p/song-parser % :start :title-value))

(defn- transform-title [[title]] title)

(defn unknown [system args]
  (rt/error system (str "Unknown command: " (first args) " " (rest args))))

(defn exit [system args]
  (rt/shutdown system))

(defn title 
  [system args]
  (if (= (count args) 1)
    (rt/result system (song/title (rt/song system)))
    (let [title-string (s/join " " (rest args))
          title-parse (title-parser title-string)] 
      (if (= (type title-parse) instaparse.gll.Failure)
        (rt/error system "The given title is invalid!")
        (rt/song system 
                 (song/title (rt/song system) 
                             (transform-title title-parse)))))))

;; TODO: remove duplication between this and jazzler.repl.commands
(def commands
  {:title title
   :exit exit
   :quit exit})

(defn command 
  "Returns a tuple with a fn and a seq of arguments."
  [s]
  (let [words (s/split s #"\s+")
        comm ((keyword (first words)) commands unknown)]
    [comm words]))
