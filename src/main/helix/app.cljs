(ns helix.app
  (:require [helix.core :refer [defnc $ <>]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react-dom/client" :as rdom]
            [helix.part1 :as p1]
            [helix.part2 :as p2]
            [helix.part3 :as p3]
            [helix.part4 :as p4]
            [helix.part5 :as p5]
            [helix.part6 :as p6]))

(defnc app
  []
  (<>
     ;; Part 1
   (d/h1 "React Docs in Helix")
   (d/p "Working through the "
        (d/a {:href "https://beta.reactjs.org/learn"} "React Docs")
        " with "
        (d/a {:href "https://github.com/lilactown/helix"} "Helix")
        ", an unintrusive ClojureScript wrapper optimized for modern React development.")
   (d/hr {:size 1 :color "#242424"})
   (d/h2  "Part 1: Quick Start")
   ($ p1/part1)
   (d/hr {:size 1 :color "#242424"})
     ;; Part 2
   (d/h2  "Part 2: Thinking in React")
   ($ p2/filterable-product-table {:products p2/products2})
   (d/hr {:size 1 :color "#242424"})
     ;; Part 3
   (d/h2  "Part 3: Describing the UI")
   ($ p3/part3)
   (d/hr {:size 1 :color "#242424"})
     ;; Part 4
   (d/h2  "Part 4: Adding Interactivity")
   ($ p4/part4)
   (d/hr {:size 1 :color "#242424"})
     ;; Part 5
   (d/h2  "Part 5: Managing State")
   ($ p5/part5)
   (d/hr {:size 1 :color "#242424"})
     ;; Part 5
   (d/h2  "Part 6: Escape Hatches")
   ($ p6/part6)))

(defn ^:export init []
  (let [root (rdom/createRoot (js/document.getElementById "app"))]
    (.render root ($ app))))
