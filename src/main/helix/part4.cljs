(ns helix.part4
  (:require [helix.core :refer [defnc $ <>]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            [goog.string :as gstring]
            [goog.string.format]))

;; test an updater function

(defnc counter1
  []
  (let [[num set-num] (hooks/use-state 0)]
    (<>
     (d/p "Without updater function " (d/i "(set-num (inc num))") " x3")
     (d/button {:on-click (fn [] (set-num (inc num))
                            (set-num (inc num))
                            (set-num (inc num)))} "+3 (a)")
     (d/b num))))

(defnc counter2
  []
  (let [[num set-num] (hooks/use-state 0)]
    (<>
     (d/p "With updater function " (d/i "(set-num #(inc %))") " x3")
     (d/button {:on-click (fn [] (set-num #(inc %))
                            (set-num #(inc %))
                            (set-num #(inc %)))} "+3 (b)")
     (d/b num))))

(defnc contact-form
  []
  (let [[person set-person] (hooks/use-state {:firstname "Barbara"
                                              :lastname "Hepworth"
                                              :email "bhepworth@sculpture.com"})]
    (<>
     (d/label "First name: "
              (d/input {:type "text"
                        :value (:firstname person)
                        :on-change #(set-person assoc :firstname (.. % -target -value))}))
     (d/p (str (:firstname person) " " (:lastname person) " (" (:email person) ")")))))

(defnc art-form
  []
  (let [[person set-person] (hooks/use-state {:name "Niki de Saint Phalle"
                                              :artwork {:title "Blue Nana"
                                                        :city "Hamburg"
                                                        :image "https://i.imgur.com/Sd1AgUOm.jpg"}})
        handle-namechange #(set-person assoc :name (.. % -target -value))
        handle-titlechange #(set-person assoc-in [:artwork :title] (.. % -target -value))
        handle-citychange #(set-person assoc-in [:artwork :city] (.. % -target -value))
        handle-imagechange #(set-person assoc-in [:artwork :image] (.. % -target -value))]
    (<>
     (d/h3 {:style {:color "cornflowerblue"}} "Clojure/Script really shines here")
     (d/p {:style {:color "gray"}} "Accessing/updating nested fields is so much cleaner with " (d/i "assoc-in") " and " (d/i "comp") "'ing keywords.")
     (d/label {:for "name"} "Name:")
     (d/input {:value (:name person)
               :id "name"
               :name "name"
               :on-change handle-namechange})
     (d/br)
     (d/label {:for "title"} "Title:")
     (d/input {:value ((comp :title :artwork) person)
               :id "title"
               :name "title"
               :on-change handle-titlechange})
     (d/br)
     (d/label {:for "city"} "City:")
     (d/input {:value ((comp :city :artwork) person)
               :id "city"
               :name "city"
               :on-change handle-citychange})
     (d/br)
     (d/label {:for "image"} "Image:")
     (d/input {:value ((comp :image :artwork) person)
               :id "image"
               :name "image"
               :on-change handle-imagechange})
     (d/br)
     (d/p
      (d/i ((comp :title :artwork) person))
      " by "
      (:name person)
      (d/br)
      "(located in " ((comp :city :artwork) person) ")")
     (d/img {:src ((comp :image :artwork) person)
             :class "card"
             :alt ((comp :title :artwork) person)}))))

;; Updating Arrays in State
;; https://beta.reactjs.org/learn/updating-arrays-in-state
;; :94,96s/\([a-zA-Z]*\):/:\1/g
(def initial-list  [{:id 0 :title "Big Bellies" :seen false}
                    {:id 1 :title "Lunar Landscape" :seen false}
                    {:id 2 :title "Terracotta Army" :seen true}])

(defnc item-list
  [{:keys [artwork on-toggle]}]
  (d/ul
   (->> artwork
        (map (fn [art]
               (d/li {:key (:id art)}
                     (d/label {:for (:id art)} (:title art))
                     (d/input {:type "checkbox"
                               :id (:id art)
                               :name (:id art)
                               :checked (:seen art)
                               :on-change #(on-toggle (:id art) (.. % -target -checked))})))))))

(defnc bucket-list
  []
  (let [[my-list, set-my-list] (hooks/use-state initial-list)
        [your-list, set-your-list] (hooks/use-state initial-list)
        handle-toggle-my-list (fn [id next-bool]
                                (set-my-list (mapv #(if (= id (:id %)) (assoc % :seen next-bool) %) my-list)))
        handle-toggle-your-list (fn [id next-bool]
                                  (set-your-list (mapv #(if (= id (:id %)) (assoc % :seen next-bool) %) your-list)))]
    (<>
     (d/h3 "Art Bucket List")
     (d/h4 "My list of art to see:")
     ($ item-list {:artwork my-list
                   :on-toggle handle-toggle-my-list})
     (d/h4 "Your list of art to see:")
     ($ item-list {:artwork your-list
                   :on-toggle handle-toggle-your-list}))))

(defnc part4
  []
  (<>
   ($ counter1)
   ($ counter2)
   (d/br)
   ($ art-form)
   ($ bucket-list)))
