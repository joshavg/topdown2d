(ns topdown2d.core
  (:require
    [topdown2d.demoscene :as demoscene]
    [topdown2d.input :as input]))

(enable-console-print!)

(def gamestate {
  :canvas (.getElementById js/document "gamecanvas")
  :2d (.getContext (.getElementById js/document "gamecanvas") "2d")
  :target-fps 30
  :timing {
    :prev 0
    :now 0
    :fps 0
    :elapsed 0
  }
  :dimensions {
    :w 600
    :h 400
  }
  :scene :demo
  :scenes {
    :demo {
      :update demoscene/update
      :draw demoscene/draw
      :init demoscene/init
      :data {}
    }
  }
})

(aset (:2d gamestate) "font" "10px monospace")

(defn set-timing [state timingkey]
  (assoc-in state
    [:timing timingkey]
    (.now js/performance)))

(defn set-fps [state]
  (let [elapsed (get-in state [:timing :elapsed])
        fps (/ 1 elapsed)]
    (assoc-in state [:timing :fps] fps)))

(defn set-elapsed-seconds [gamestate]
  (assoc-in gamestate
    [:timing :elapsed]
    (/
      (-
        (get-in gamestate [:timing :now])
        (get-in gamestate [:timing :prev]))
      1000)))

(defn update-scene [gamestate]
  (let [scenekey (:scene gamestate)
        scenedata (get-in gamestate [:scenes scenekey])
        updatefunc (:update scenedata)
        newdata (updatefunc gamestate scenedata)]
    (update-in gamestate [:scenes scenekey] (fn [] newdata))))

(defn update-step [gamestate]
  (-> gamestate
    (set-timing :now)
    (set-elapsed-seconds)
    (set-fps)
    (update-scene)
    (set-timing :prev)))

(defn draw-step [gamestate]
  (.clearRect (:2d gamestate)
    0 0
    (get-in gamestate [:dimensions :w])
    (get-in gamestate [:dimensions :h]))
  (.fillText
    (:2d gamestate)
    (int (get-in gamestate [:timing :fps]))
    0 10)
  (let [scenekey (:scene gamestate)
        scene (scenekey (:scenes gamestate))
        drawfunc (:draw scene)]
    (drawfunc gamestate scene)))

(defn mainloop [gamestate]
  (let [newstate (update-step gamestate)]
    (draw-step newstate)
    (.setTimeout js/window
      (fn []
        (.requestAnimationFrame js/window
          #(mainloop newstate)))
      (/ 1000 (:target-fps gamestate)))))

(defn init-scenes []
  (assoc
    gamestate
    :scenes
    (reduce
      (fn [scenes [scenekey scenedata]]
        (let [initfunc (:init scenedata)
              newdata (initfunc gamestate scenedata)]
          (assoc scenes
            scenekey newdata)))
      {}
      (:scenes gamestate))))

(mainloop (init-scenes))
