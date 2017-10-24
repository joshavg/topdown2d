(ns topdown2d.core
  (:require
    [topdown2d.demoscene :as demoscene]
    [topdown2d.input :as input]))

(enable-console-print!)

(def gamestate {
  :canvas (.getElementById js/document "gamecanvas")
  :ctx (.getContext (.getElementById js/document "gamecanvas") "2d")
  :target-fps 30
  :timing {
    ; msecs of previous frame
    :prev 0
    ; msecs of current frame
    :now 0
    ; fps resulting of prev and now
    :fps 0
    ; difference between prev and now in seconds
    :elapsed 0
  }
  ; width and height of the canvas
  :dimensions {
    :w 600
    :h 400
  }
  ; currently active scene
  :scene :demo
  :scenes {
    :demo {
      :update demoscene/update-scene
      :draw demoscene/draw-scene
      :init demoscene/init
      :data {}
    }
  }
})

(defn set-timing
  "sets the current time at the given key"
  [state timingkey]
  (assoc-in state
    [:timing timingkey]
    (.now js/performance)))

(defn set-fps
  "calculates the current fps using the elapsed time"
  [state]
  (let [elapsed (get-in state [:timing :elapsed])
        fps (/ 1 elapsed)]
    (assoc-in state [:timing :fps] fps)))

(defn set-elapsed-seconds
  "calculates and writes the elapsed seconds since the last frame"
  [gamestate]
  (assoc-in gamestate
    [:timing :elapsed]
    (/
      (-
        (get-in gamestate [:timing :now])
        (get-in gamestate [:timing :prev]))
      1000)))

(defn update-scene
  "updates the current scene using its udpate function"
  [gamestate]
  (let [scenekey (:scene gamestate)
        scenedata (get-in gamestate [:scenes scenekey])
        updatefunc (:update scenedata)
        newdata (updatefunc gamestate scenedata)]
    (update-in gamestate [:scenes scenekey] (fn [] newdata))))

(defn update-step
  "updates timing information and the current scene"
  [gamestate]
  (-> gamestate
    (set-timing :now)
    (set-elapsed-seconds)
    (set-fps)
    (update-scene)
    (set-timing :prev)))

(defn draw-fps
  "draws the current fps"
  [gamestate]
  (let [ctx (:ctx gamestate)]
    (aset ctx "fillStyle" "white")
    (.fillRect
      ctx
      0 0 13 13)
    (aset ctx "fillStyle" "black")
    (aset ctx "font" "10px monospace")
    (.fillText
      (:ctx gamestate)
      (int (get-in gamestate [:timing :fps]))
      0 10)))

(defn draw-step
  "clears the canvas, draws fps and invokes the scene draw function"
  [gamestate]
  (.clearRect (:ctx gamestate)
    0 0
    (get-in gamestate [:dimensions :w])
    (get-in gamestate [:dimensions :h]))
  (let [scenekey (:scene gamestate)
        scene (scenekey (:scenes gamestate))
        drawfunc (:draw scene)]
    (drawfunc gamestate scene))
  (draw-fps gamestate))

(defn mainloop
  "transforms the given gamestate by invoking a series of update
  functions and draws it using the 2d context of the gamestate.
  then, it calls itself again with a delay according to the target fps"
  [gamestate]
  (let [newstate (update-step gamestate)]
    (draw-step newstate)
    ; calculate the duration of update-step and draw-step
    ; substract that from the wait time to reach target-fps
    ; more accurately
    (let [now (get-in newstate [:timing :now])
          duration (- (.now js/performance) now)]
      (.setTimeout js/window
        (fn []
          (.requestAnimationFrame js/window
            #(mainloop newstate)))
        (/
          (- 1000 duration)
          (:target-fps gamestate))))))

(defn init-scenes
  "initiates the scene data maps using their respective init functions"
  []
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
