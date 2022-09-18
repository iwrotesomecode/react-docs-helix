(ns helix.part5
  (:require [helix.core :refer [defnc $ <>]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react" :as react]
            [helix.p5data :refer [places]]
            [helix.task :as task]
            [goog.string :as gstring]
            [goog.string.format]))

;; Managing State
;; Context

(def image-size-context (react/createContext 500))

(defn get-image-url
  [place]
  (str "https://i.imgur.com/" (:image-id place) "l.jpg"))

(defnc Place-image
  [{:keys [place]}]
  (let [image-size (hooks/use-context image-size-context)]
    (d/img {:src (get-image-url place)
            :class "card"
            :alt (:name place)
            :width image-size
            :height image-size})))

(defnc Place
  [{:keys [place]}]
  (<>
   ($ Place-image {:place place})
   (d/p
    (d/b (:name place))
    (str ": " (:description place)))))

(defnc Mural-list
  []
  (let [list-items (mapv (fn [place] (d/li {:key (:id place)}
                                           ($ Place {:place place})))
                         places)]
    (d/ul list-items)))

(defnc Murals
  []
  (let [[largesse? set-largesse] (hooks/use-state false)
        image-size (if largesse? 150 100)]
    (helix.core/provider {:context image-size-context
                          :value image-size}
                         (d/input {:type "checkbox"
                                   :name "largesse"
                                   :id "largesse"
                                   :checked largesse?
                                   :on-change #(set-largesse (.. % -target -checked))})
                         (d/label {:for "largesse"} "Use large images")
                         (d/hr {:size 1 :color "lightgray"})
                         ($ Mural-list))))

(def initial-tasks [{:id 0 :text "Navštivte Kafkovo muzeum" :done true}
                    {:id 1 :text "Podívejte se na loutkové představení" :done false}
                    {:id 2 :text "Fotografie Lennonova zdi" :done false}])
;; (def initial-tasks [{:id 0 :text "Visit Kafka Museum" :done true}
;;                     {:id 1 :text "Watch a puppet show" :done false}
;;                     {:id 2 :text "Lennon Wall pic" :done false}])

(defnc Add-task
  [{:keys [on-add-task]}]
  (let [[text set-text] (hooks/use-state "")]
    (<>
     (d/input {:placeholder "Add task"
               :value text
               :on-change #(set-text (.. % -target -value))})
     (d/button {:on-click (fn [] (set-text "") (on-add-task text))}
               "Add"))))

(defnc Task
  [{:keys [task on-change on-delete]}]
  (let [[editing? set-editing] (hooks/use-state false)
        task-content (if editing?
                       (<>
                        (d/input {:type "text"
                                  :value (:text task)
                                  :on-change #(on-change (assoc task :text (.. % -target -value)))})
                        (d/button {:on-click #(set-editing (not editing?))} "Save"))
                       (<>
                        (d/span (:text task))
                        (d/button {:on-click #(set-editing (not editing?))} "Edit")))]
    (<>
     (d/input {:type "checkbox"
               :name (:id task)
               :id (:id task)
               :checked (:done task)
               :on-change #(on-change (assoc task :done (.. % -target -checked)))})
     (d/label {:for (:id task)} task-content)
     (d/button {:on-click #(on-delete (:id task))} "Delete"))))

(defnc Task-list
  [{:keys [tasks on-change-task on-delete-task]}]
  (d/ul
   (->> tasks
        (mapv (fn [task] (d/li {:key (:id task)}
                               ($ Task {:task task
                                        :on-change on-change-task
                                        :on-delete on-delete-task})))))))

(defn task-reducer [tasks action]
  (case (:type action)
    :added (conj tasks {:id (:id action) :text (:text action) :done false})
    :changed (->> tasks
                  (mapv (fn [task] (if (= ((comp :id :task) action) (:id task))
                                     (:task action)
                                     task))))
    :deleted (filterv #(not= (:id %) (:id action)) tasks)
    :default (-> (str "Unknown action: " (:type action))
                 throw)))

(defnc Task-app
  []
  (let [[idx set-idx] (hooks/use-state 3)
        [tasks dispatch] (hooks/use-reducer task-reducer initial-tasks)
        handle-add-task (fn [text] (dispatch {:type :added
                                              :id idx
                                              :text text})
                          (set-idx inc))
        handle-change-task (fn [task] (dispatch {:type :changed
                                                 :task task}))
        handle-delete-task (fn [id] (dispatch {:type :deleted
                                               :id id}))]
    (<>
     (d/h3 "Itinerář Prahy \u2014 useReducer")
     ;; (d/h3 "Prague Itinerary (useReducer)")
     ;; (d/h4 idx)
     (js/console.log (.stringify js/JSON (clj->js tasks)))
     ($ Add-task {:on-add-task handle-add-task})
     ($ Task-list {:tasks tasks
                   :on-change-task handle-change-task
                   :on-delete-task handle-delete-task}))))

;; doesn't preserve whitespace
(defnc gstring-test
  []
  (d/ul
   (d/li (str (gstring/format "%-20s" "One") 1))
   (d/li (str (gstring/format "%-20s" "Two") 2))
   (d/li (str (gstring/format "%-20s" "Three") 3))))

(defnc part5
  []
  (<>
   (d/h3 "useContext example")
   ($ Murals)
   (d/hr {:size 1 :color "lightgray"})
   ($ Task-app)
   (d/hr {:size 1 :color "lightgray"})
   ($ task/Task-app)
   (d/hr {:size 1 :color "lightgray"})))
