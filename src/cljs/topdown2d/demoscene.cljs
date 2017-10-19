(ns topdown2d.demoscene
  (:require
    [topdown2d.objects :as objects]
    [topdown2d.input :as input]))

(defn init [gamestate scenedata]
  (assoc scenedata
    :data
    {
      :bumper
      {
        :x 50
        :y 50
        :w 10
        :h 10
      }
      :box
      {
        :x 5
        :y 5
        :w 10
        :h 10
        :pps 100
        :d :?
      }
    }))

(defn update [gamestate scenedata]
  (let [box (get-in scenedata [:data :box])
        dir (input/dirinput)
        box (assoc box :d dir)]
    (assoc-in scenedata
      [:data :box]
      (objects/move-inside-gamestate
        gamestate
        box))))

(defn draw [gamestate scenedata]
  (let [{{:keys [bumper box]} :data} scenedata
        ctx (:2d gamestate)]
    (let [{:keys [x y w h]} bumper]
      (.fillRect ctx
        x y w h))
    (let [{:keys [x y w h]} box]
      (.strokeRect ctx
        x y w h))))
