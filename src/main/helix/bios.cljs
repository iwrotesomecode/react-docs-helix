(ns helix.bios
  (:require [helix.core :refer [defnc $ <>]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react" :as react]
            ["react-dom" :as rdom]
            [promesa.core :as p]))

(def bios {:Alice "This is Alice's bio."
           :Bob "This is Bob's bio."
           :Taylor "This is Taylor's bio."})

(defn fetch-bio
  [person]
  (case (keyword person)
    :Alice (p/delay 250 (:Alice bios))
    :Taylor (p/delay 2000 (:Taylor bios))
    :Bob (p/delay 10 (:Bob bios))))

(defnc page-1
  []
  (let [[person set-person] (hooks/use-state "Alice")
        [bio set-bio] (hooks/use-state nil)
        p (fetch-bio person)]
    (hooks/use-effect
     [person]
     (set-bio nil)
     (-> p (p/then #(set-bio %))))
    (d/div {:class "race"}
           (d/p "This simulated API call example has a race condition. Selecting Taylor then Bob quickly enough results in Bob being selected and Taylor's bio being displayed.")
           (d/select {:value person
                      :on-change #(set-person (.. % -target -value))}
                     (d/option {:value "Alice"} "Alice")
                     (d/option {:value "Taylor"} "Taylor")
                     (d/option {:value "Bob"} "Bob"))
           (d/hr {:color "red"})
           (d/p
            (d/i {:style {:color "red"}} (or bio "Loading..."))))))

(defnc page-2
  []
  (let [[person set-person] (hooks/use-state "Alice")
        [bio set-bio] (hooks/use-state nil)
        p (fetch-bio person)]
    (hooks/use-effect
     [person]
     (let [ignore? (atom false)]
       (set-bio nil)
       (-> p (p/then #(when (not @ignore?) (set-bio %))))
       ;; return/cleanup command
       #(reset! ignore? true)))
    (d/div {:class "race"}
           (d/p "The race condition is fixed here with a cleanup function on useEffect, preventing state from being set from the return of an outdated API call.")
           (d/select {:value person
                      :on-change #(set-person (.. % -target -value))}
                     (d/option {:value "Alice"} "Alice")
                     (d/option {:value "Taylor"} "Taylor")
                     (d/option {:value "Bob"} "Bob"))
           (d/hr {:color "green"})
           (d/p
            (d/i {:style {:color "green"}} (or bio "Loading..."))))))
