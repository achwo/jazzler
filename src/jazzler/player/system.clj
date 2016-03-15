(ns jazzler.player.system
  (:gen-class)
  (:use [overtone.live])
  (:require [jazzler.player.player :as p]))

(defn play-song [song] 
  (p/play-song song))
