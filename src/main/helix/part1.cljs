(ns helix.part1
  (:require [helix.core :refer [defnc $ <>]]
            [helix.hooks :as hooks]
            [helix.dom :as d]))

;;; Quick Start

(def products
  [{:title "Cabbage" :is-fruit false :id 1}
   {:title "Garlic" :is-fruit false :id 2}
   {:title "Apple" :is-fruit true :id 3}])

(defnc shopping-list
  []
  (d/ul
   (for [item products]
     (d/li {:key (:id item)
            :style {:color (if (:is-fruit item) "magenta" "darkgreen")}}
           (:title item)))))

(def user {:name "Hedy Lamar"
           :imageURL "https://i.imgur.com/yXOvdOSs.jpg"
           :imageSize 90})

(defnc profile
  []
  (<>
   (d/h3  (:name user))
   (d/img {:class "avatar"
           :src (:imageURL user)
           :alt (str "Photo of " (:name user))
           :style {:width (:imageSize user)
                   :height (:imageSize user)}})))

(defnc my-button
  [{:keys [count on-click]}]
  (d/button {:on-click on-click}
            (str "Clicked " count " times")))

(defnc part1
  []
  (let [[count, set-count] (hooks/use-state 0)
        handle-click #(set-count inc)]
    (<>
     ;; state lifted out of count and shared btwn buttons
     ($ my-button {:count count :on-click handle-click})
     ($ my-button {:count count :on-click handle-click})
     ($ profile)
     ($ shopping-list))))
