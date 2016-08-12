(ns user
  (:require [clojure.repl :refer [doc source apropos]]
            [clojure.pprint :refer [pprint]]
            [midje.repl :refer :all]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [jazzler.core :refer :all]
            [jazzler.song :as js]
            [jazzler.repl.song-commands :as jrs]
            [jazzler.parser.parser :as jpp]
            [jazzler.parser.transformer :as jpt]
            [jazzler.parser.system :as jps]
            ))
