(ns helix.part6
  (:require [helix.core :refer [defnc $ <>]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react" :as react]))
;;(.. js/object -prop1 -prop2 -prop3) ;; JS output: object.prop1.prop2.prop3;

;; (.. object -property -property method)
;; (.. object -property -property -property)
;;          Instead of:
;; (.method (.-property (.-property object)))
;; (.-property (.-property (.-property object)))

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

(defnc video-player
  [{:keys [src playing?]}]
  (let [ref (hooks/use-ref nil)]
    (hooks/use-effect
     :auto-deps
     (if playing?
       (.play @ref) ;; (.. @ref -current play)
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
   (d/br)
   ($ video-app)))
