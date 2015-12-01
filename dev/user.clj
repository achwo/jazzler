(ns user
  (:require [clojure.repl :refer [doc source]]
            [clojure.pprint :refer [pprint]]
            [midje.repl :refer :all]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [jazzler.song :refer :all]
            [jazzler.repl :refer :all]
            [jazzler.repl.system :as sys]
            [jazzler.repl.state-machine :as sm]
            ;; [jazzler.player :refer [play-song play-chord]]
            ))
