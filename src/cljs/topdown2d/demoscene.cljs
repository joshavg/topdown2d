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
        :x 100
        :y 80
        :w 50
        :h 50
      }
      :box
      {
        :x 5
        :y 5
        :w 10
        :h 10
        :pps 150
        :d :?
        :color :black
      }
    }))

(defn update [gamestate scenedata]
  (let [box (get-in scenedata [:data :box])
        bumper (get-in scenedata [:data :bumper])
        dir (input/dirinput)
        box (assoc box :d dir)
        mbox (objects/move-inside-gamestate
               gamestate
               box)]
    (assoc-in scenedata
      [:data :box]
      (if (objects/collide? mbox bumper)
        (-> box
          (objects/bump-into bumper)
          (assoc :color :red))
        (assoc mbox :color :black)))))

(defn draw [gamestate scenedata]
  (let [{{:keys [bumper box]} :data} scenedata
        ctx (:2d gamestate)]
    (let [{:keys [x y w h]} bumper]
      (.fillRect ctx
        x y w h))
    (let [{:keys [x y w h color]} box
          ctx (:2d gamestate)]
      (aset ctx "strokeStyle"
        (if (= :red color)
          "red"
          "black"))
      (.strokeRect ctx
        x y w h))))
