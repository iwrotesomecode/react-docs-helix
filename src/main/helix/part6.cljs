(ns helix.part6
  (:require [helix.core :refer [defnc $ <>]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react" :as react]
            ["react-dom" :as rdom]
            [helix.bios :as bios]
            [goog.string :as gstring]
            [goog.string.format]
            [promesa.core :as p]
            [clojure.string :as str]))
;;(.. js/object -prop1 -prop2 -prop3) ;; JS output: object.prop1.prop2.prop3;

;; (.. object -property -property method)
;; (.. object -property -property -property)
;;          Instead of:
;; (.method (.-property (.-property object)))
;; (.-property (.-property (.-property object)))

;; forward-ref test

(defnc my-input
  [props ref]
  {:wrap [(react/forwardRef)]}
  (d/input {:ref ref :& props}))

(defnc my-form
  []
  (let [input-ref (hooks/use-ref nil)
        handle-click #(.focus @input-ref)]
    (<>
     ($ my-input {:ref input-ref})
     (d/button {:on-click handle-click} "Focus the input"))))

(defnc stopwatch
  []
  (let [[start, set-start] (hooks/use-state nil)
        [now, set-now] (hooks/use-state nil)
        interval-ref (hooks/use-ref nil)
        handle-start (fn []
                       (set-start (js/Date.now))
                       (set-now (js/Date.now))
                       (when interval-ref (js/clearInterval @interval-ref))
                       (reset! interval-ref (-> (fn [] (set-now (js/Date.now)))
                                                (js/setInterval 10))))
        handle-stop #(js/clearInterval @interval-ref)
        elapsed-seconds (if (and start now)
                          (/ (- now start) 1000)
                          0)]
    (<>
     (d/h3 "Escaped Time: " (.toFixed elapsed-seconds 3) " s")
     (d/button {:on-click handle-start} "Start")
     (d/button {:on-click handle-stop} "Stop"))))

(defnc cat-friends
  []
  (let [[idx set-idx] (hooks/use-state 0)
        cat-list (for [x (range 10)]
                   {:id x
                    :image-url (str "https://placekitten.com/250/200?image=" x)})
        selected-ref (hooks/use-ref nil)
        handle-click (fn [] (rdom/flushSync
                             (if (< idx (dec (count cat-list)))
                               (set-idx inc)
                               (set-idx 0)))
                       (.scrollIntoView @selected-ref {:behavior "smooth"
                                                       :block "nearest"}))]
    (<>
     (d/p {:id "cat"
           :style {:color "gray"}} "Use react-dom/flushSync to force React to update DOM on button click before the ref is 'scrollIntoView.'")
     (d/div {:class "container"}
            (d/ul
             (->> cat-list
                  (map-indexed (fn [i cat]
                                 (d/li {:key (:id cat)
                                        :ref (if (= i idx) selected-ref nil)}
                                       (d/img {:class (if (= idx i) "active" "")
                                               :src (:image-url cat)
                                               :alt (str "Cat #" (:id cat))})))))))
     (d/nav
      (d/button {:on-click handle-click}
                "Next cat")))))

(defnc video-player
  [{:keys [src playing?]}]
  (let [ref (hooks/use-ref nil)]
    (hooks/use-effect
     :auto-deps ;; [playing?]
     (if playing?
       (.play @ref) ;; (.. ref -current play)
       (.pause @ref)))
    (d/video {:ref ref
              :src src
              :loop true
              :playsInline true})))

(defnc video-app
  []
  (let [[playing? set-playing] (hooks/use-state false)
        label (if playing? "Pause" "Play")]
    (<>
     (d/button {:on-click #(set-playing (not playing?))}
               label)
     ($ video-player {:playing? playing?
                      :src "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4"}))))

(defnc move-dot
  []
  (let [[position set-position] (hooks/use-state {:x 0 :y 0})
        [movable? set-movable] (hooks/use-state false)
        ref (hooks/use-ref nil)
        offset (.-scrollY js/window)
        handle-move #(set-position {:x (.-clientX %)
                                    :y (+ (.-clientY %) offset)})]
    (js/console.log (gstring/format "top-doc: %s, viewport-top: %s" offset (if @ref (.-top (.getBoundingClientRect @ref)) 0)))
    (hooks/use-effect
     [movable?]
     (when movable?
       (js/window.addEventListener "pointermove" handle-move))
     #(js/window.removeEventListener "pointermove" handle-move))
    (<>
     (d/input {:type "checkbox"
               :ref ref
               :name "move"
               :id "move"
               :checked movable?
               :on-change #(set-movable (.. % -target -checked))})
     (d/label {:for "move"} "The dot is allowed to move")
     (d/hr {:color "lightgray"})
     (d/div {:style {:position "absolute"
                     :background-color "pink"
                     :border-radius "50%"
                     :opacity 0.6
                     :transform (gstring/format "translate(%spx, %spx)" (:x position) (:y position))
                     :pointer-events "none"
                     :left -20
                     :top -20
                     :width 40
                     :height 40}}))))

(def planets [{:id "Earth" :places [{:id "Laos"}
                                    {:id "Spain"}
                                    {:id "Vietnam"}]}
              {:id "Venus" :places [{:id "Aurelia"}
                                    {:id "Diana Chasma"}
                                    {:id "Kŭmsŏng Vallis"}]}
              {:id "Mars" :places [{:id "Aluminum City"}
                                   {:id "New New York"}
                                   {:id "Vishniac"}]}])

(defn fetch-data
  [url]
  (let [[_ _ id _] (str/split url #"/")
        planets planets]
    (if id
      (p/delay 500 (->> (filter #(= id (:id %)) planets) (into {}) :places))
      (p/delay 2000 (mapv #(dissoc % :places) planets)))))

(defn use-select-options
  [url]
  (let [[list set-list] (hooks/use-state nil)
        [selected-id set-selected-id] (hooks/use-state nil)]
    (hooks/use-effect
     [url]
     (when url
       (let [ignore? (atom false)]
         (-> (fetch-data url)
             (p/then #(when (not @ignore?) (set-list %) (set-selected-id (:id (first %))))))
         #(reset! ignore? true))))
    [list selected-id set-selected-id]))

(defnc planet-page
  []
  (let [[planet-list planet-id set-planet-id] (use-select-options "/planets")
        [place-list place-id set-place-id] (use-select-options
                                            (if planet-id
                                              (gstring/format "/planets/%s/places" planet-id)
                                              nil))]
    (<>
     (d/label
      "Pick a planet: "
      (d/select {:value planet-id
                 :on-change #(set-planet-id (.. % -target -value))}
                (->> planet-list
                     (map (fn [planet]
                            (d/option {:key (:id planet)
                                       :value (:id planet)}
                                      (:id planet))))))
      (d/br)
      (d/label
       "Pick a place: "
       (d/select {:value place-id
                  :on-change #(set-place-id (.. % -target -value))}
                 (->> place-list
                      (map (fn [place]
                             (d/option {:key (:id place)
                                        :value (:id place)}
                                       (:id place)))))))
      (d/hr)
      (d/p (str "You are headed to " (or place-id "...") " on " (or planet-id "...")))))))

(defnc part6
  []
  (<>
   (d/p "Escape Artists \u2014 useRef, useEffect")
   ($ stopwatch)
   (d/p "forwardRef")
   ($ my-form)
   (d/br)
   ($ video-app)
   ($ cat-friends)
   (d/hr {:color "lightgray"})
   (d/h3 "useEffect, stale data, race conditions")
   ($ bios/page-1)
   ($ bios/page-2)
   (d/hr {:color "lightgray"})
   ($ move-dot)
   (d/p {:style {:color "darkslategray"}} "While both selections below use repetitive logic, they shouldn't be combined into a single Effect (then you'd need to refetch each list every change). Instead, extract the logic into a custom hook to hide synchronization logic. The calling component doesn't need to know about the Effect.")
   ($ planet-page)))
