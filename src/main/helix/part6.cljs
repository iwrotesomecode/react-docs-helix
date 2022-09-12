(ns helix.part6
  (:require [helix.core :refer [defnc $ <>]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react" :as react]
            ["react-dom" :as rdom]
            [helix.bios :as bios]))
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
        handle-start #((set-start (js/Date.now))
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
   (d/hr {:color "lightgray"})))
