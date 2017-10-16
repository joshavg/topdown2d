(ns topdown2d.core
  (:require [topdown2d.demoscene]))

(def gamestate {
  :canvas (.getElementById js/document "gamecanvas")
  :2d (.getContext (.getElementById js/document "gamecanvas") "2d")
  :timing {
    :prev 0
    :now 0
    :fps 0
  }
  :scene :demo
  :scenes {
    :demo {
      :update topdown2d.demoscene/update
      :draw topdown2d.demoscene/draw
      :data {}
    }
  }
})

(aset (:2d gamestate) "font" "10px monospace")

(def keysdown (atom []))

(defn keydown? [keycode]
  (some #{keycode} @keysdown))

(.addEventListener js/document
  "keydown"
  (fn [event]
    (let [code (symbol (.-code event))]
      (swap! keysdown #(set (conj %1 code))))))

(.addEventListener js/document
  "keyup"
  (fn [event]
    (let [code (symbol (.-code event))]
      (swap! keysdown
        (fn [coll]
          (remove #(= % code) coll))))))

(defn set-timing [state timingkey]
  (update-in state
    [:timing timingkey]
    #(.now js/performance)))

(defn set-fps [state]
  (let [newstate (set-timing state :now)
        now (get-in newstate [:timing :now])
        prev (get-in newstate [:timing :prev])
        duration (- now prev)
        fps (/ 1000 duration)]
    (update-in newstate [:timing :fps] (fn [] fps))))

(defn update-scene [gamestate]
  (let [scenekey (:scene gamestate)
        scenedata (get-in gamestate [:scenes scenekey])
        updatefunc (:update scenedata)
        newdata (updatefunc gamestate scenedata)]
    (update-in gamestate [:scenes scenekey] (fn [] newdata))))

(defn update-step [gamestate]
  (-> gamestate
    (set-fps)
    (set-timing :prev)
    (update-scene)))

(defn draw-step [gamestate]
  (.clearRect (:2d gamestate) 0 0 400 600)
  (.fillText
    (:2d gamestate)
    (int (get-in gamestate [:timing :fps]))
    0 10))

(defn mainloop [gamestate]
  (let [newstate (update-step gamestate)]
    (draw-step newstate)
    (.setTimeout js/window
      (fn []
        (.requestAnimationFrame js/window
          #(mainloop newstate)))
      (/ 1000 30))))

(mainloop gamestate)