(ns helix.part3
  (:require [helix.core :refer [defnc $ <>]]
            [helix.hooks :as hooks]
            [helix.dom :as d]))

;;; Describing the UI

(defnc profile
  []
  (d/img {:src "https://i.imgur.com/MK3eW3As.jpg"
          :class "avatar"
          :alt "Katherine Johnson"}))
(defnc profile2
  []
  (d/img {:src "https://i.imgur.com/QIrZWGIs.jpg"
          :class "avatar"
          :alt "Alan L. Hart"}))
(defnc gallery
  []
  (d/section
   (d/h3 "Amazing Scientists")
   ($ profile)
   ($ profile)
   ($ profile)
   (d/br)
   ($ profile2)
   ($ profile2)
   ($ profile2)))

(def person {:name "Gregorio Y. Zara"
             :theme {:background-color "#1d1d1d"
                     :color "pink"
                     :padding "30px"
                     :border-radius "20px"}})
(defnc todolist
  []
  (d/div {:style (:theme person)}
         (d/h3 (:name person) "'s Todos")
         (d/img {:class "avatar"
                 :src "https://i.imgur.com/7vQD0fPs.jpg"
                 :alt "Gregorio Y. Zara"})
         (d/img {:class "avatar"
                 :src "https://i.imgur.com/7vQD0fPs.jpg"
                 :alt "Gregorio Y. Zara"})
         (d/img {:class "avatar"
                 :src "https://i.imgur.com/7vQD0fPs.jpg"
                 :alt "Gregorio Y. Zara"})

         (d/ul
          (d/li "Improve the videophone")
          (d/li "Prepare aeronautics lectures")
          (d/li "Work on the alcohol-fuelled engine"))))

(def today (js/Date.))

(defn format-date
  [date]
  (-> (js/Intl.DateTimeFormat. "en-US" #js {:dateStyle "full"})
      (.format date)))

(defnc today-todo
  []
  (d/h3 "TODO for " (format-date today)))

;; passing props to a component
;; passing jsx as children
(defn get-image-url
  [{:keys [image-id]}]
  (str "https://i.imgur.com/" image-id "s.jpg"))

(defnc avatar
  [{:keys [person size]}]
  (d/img {:class "avatar"
          :src (get-image-url person)
          :alt (:name person)
          :width size
          :height size}))

(defnc card
  [{:keys [children]}]
  (d/div {:class "card"} children))

(defnc profile-card
  []
  ($ card {:children
           ($ avatar {:size 100
                      :person {:name "Katsuko Saruhashi"
                               :image-id "YfeOqp2"}})}))

;; conditional rendering

;; this short-circuiting works, but I prefer the clarity of a let binding and conditional
;; (defnc item
;;   [{:keys [name is-packed?]}]
;;   (d/li {:class "item"}
;;         name (and is-packed? "✔")))
(defnc item
  [{:keys [name is-packed?]}]
  (let [content (if is-packed? (d/del name " ✔") name)]
    (d/li {:class "item"}
          content)))

(defnc packing-list
  []
  (d/section
   (d/h3 "Sally Ride's Packing List")
   (d/ul
    ($ item {:name "Space suit" :is-packed? true})
    ($ item {:name "Helmet with a golden leaf" :is-packed? true})
    ($ item {:name "Photo of Tam" :is-packed? false}))))

(def people [{:id 0
              :name "Creola Katherine Johnson"
              :profession "mathematician"
              :accomplishment "spaceflight calculations"
              :image-id "MK3eW3A"}
             {:id 1
              :name "Mario José Molina-Pasquel Henríquez"
              :profession "chemist"
              :accomplishment "discovery of Arctic ozone hole"
              :image-id "mynHUSa"}
             {:id 2
              :name "Mohammad Abdus Salam"
              :profession "physicist"
              :accomplishment "electromagnetism theory"
              :image-id "bE7W1ji"}
             {:id 3
              :name "Percy Lavon Julian"
              :profession "chemist"
              :accomplishment "pioneering cortisone drugs steroids and birth control pills"
              :image-id "IOjWm71"}
             {:id 4
              :name "Subrahmanyan Chandrasekhar"
              :profession "astrophysicist"
              :accomplishment "white dwarf star mass calculations"
              :image-id "lrWQx8l"}])

(defnc sci-list
  []
  (let [[state set-state] (hooks/use-state "all")
        selected (if (= state "all") people (filter #(= state (:profession %)) people))
        list-items (->> selected
                        (map (fn [person]
                               (d/li {:key (:id person)}
                                     ($ card {:children (d/img {:src (get-image-url person)
                                                                :class "avatar"
                                                                :alt (:name person)})})
                                     (d/p
                                      (d/b (:name person) ":")
                                      (str " " (:profession person) " ")
                                      (str "known for " (:accomplishment person)))))))]
    (<>
     (d/h3 {:style {:color "peru"}} "Swipe Right! These scientists in your area!")
     (d/label {:for "profession"} "Choose a profession: ")
     (d/select {:id "profession" :name "profession" :on-change #(set-state (.. % -target -value))}
               (d/option {:value "all"} "all")
               (d/option {:value "mathematician"} "mathematician")
               (d/option {:value "chemist"} "chemist")
               (d/option {:value "physicist"} "physicist")
               (d/option {:value "astrophysicist"} "astrophysicist"))
     (d/ul ;; {:style {:list-style "none"
           ;;          :margin 0
           ;;          :padding 0}}
      list-items))))

(defnc part3
  []
  (<>
   ($ gallery)
   ($ todolist)
   ($ today-todo)
   ($ profile-card)
   ($ packing-list)
   ($ sci-list)))
