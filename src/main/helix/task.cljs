(ns helix.task
  (:require [helix.core :refer [defnc $ <>]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react" :as react]))

(def initial-tasks [{:id 0 :text "哲学者の道" :done false}
                    {:id 1 :text "神殿を訪れる" :done false}
                    {:id 2 :text "ドリンク抹茶" :done true}])
;; (def initial-tasks [{:id 0 :text "Philosopher's Path" :done false}
;;                     {:id 1 :text "Visit the temple" :done false}
;;                     {:id 2 :text "Drink matcha" :done true}])

(def task-context (react/createContext nil))
(def task-dispatch-context (react/createContext nil))

(defnc Add-task
  []
  (let [dispatch (hooks/use-context task-dispatch-context)
        [text set-text] (hooks/use-state "")]
    (<>
     (d/input {:placeholder "Add task"
               :value text
               :on-change #(set-text (.. % -target -value))})
     (d/button {:on-click (fn [] (set-text "") (dispatch {:type :added
                                                          :id (str (random-uuid))
                                                          :text text}))}
               "Add"))))

(defnc Task
  [{:keys [task]}]
  (let [dispatch (hooks/use-context task-dispatch-context)
        [editing? set-editing] (hooks/use-state false)
        task-content (if editing?
                       (<>
                        (d/input {:type "text"
                                  :value (:text task)
                                  :on-change #(dispatch {:type :changed
                                                         :task (assoc task :text (.. % -target -value))})})

                        (d/button {:on-click #(set-editing (not editing?))} "Save"))
                       (<>
                        (d/span (:text task))
                        (d/button {:on-click #(set-editing (not editing?))} "Edit")))]
    (<>
     (d/input {:type "checkbox"
               :name (:id task)
               :id (:id task)
               :checked (:done task)
               :on-change #(dispatch {:type :changed
                                      :task (assoc task :done (.. % -target -checked))})})
     (d/label {:for (:id task)} task-content)
     (d/button {:on-click #(dispatch {:type :deleted
                                      :id (:id task)})}
               "Delete"))))

(defnc Task-list
  []
  (let [tasks (hooks/use-context task-context)]
    (d/ul
     (->> tasks
          (mapv (fn [task] (d/li {:key (:id task)}
                                 ($ Task {:task task}))))))))

(defn task-reducer [tasks action]
  (case (:type action)
    :added (conj tasks {:id (:id action) :text (:text action) :done false})
    :changed (->> tasks
                  (mapv (fn [task] (if (= ((comp :id :task) action) (:id task))
                                     (:task action)
                                     task))))
    :deleted (filterv #(not= (:id %) (:id action)) tasks)
    :default (-> (js/Error. (str "Unknown action: " (:type action)))
                 (throw))))

(defnc Task-app
  []
  (let [[tasks dispatch] (hooks/use-reducer task-reducer initial-tasks)]
    (->> (<>
          (d/h3 "京都の休日 \u2014 useReducer and useContext")
          ;; (d/h3 "Day off in Kyoto (useReducer and useContext)")
          (js/console.log (.stringify js/JSON (clj->js tasks)))
          ($ Add-task)
          ($ Task-list))
         (helix.core/provider {:context task-dispatch-context
                               :value dispatch})
         (helix.core/provider {:context task-context
                               :value tasks}))))
