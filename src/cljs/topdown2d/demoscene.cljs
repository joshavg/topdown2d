(ns topdown2d.demoscene
  (:require
    [topdown2d.objects :as objects]
    [topdown2d.input :as input]
    [topdown2d.sprites :as sprites]))

(defn init [gamestate scenedata]
  (assoc scenedata
    :data {
      :bumper {
        :x 100
        :y 80
        :w 50
        :h 50
      }
      :player {
        :x 0 :y 0
        :w 64 :h 64
        :pps 150
        :d :?
        :sprite {
          :image (.getElementById js/document "player")
          :size 64
          :d :s
          :rows {
            :w 1 :e 3
            :n 0 :s 2
            :? 2
          }
          :cycle {
            :count 8
            :from 1
            :pos 0
            ; seconds per cycle
            :spc 0.08
            :last-cycle 0
          }
        }
      }
    }))

(defn update-scene [gamestate scenedata]
  (let [player (get-in scenedata [:data :player])
        dir (input/dirinput :?)
        player (assoc-in player
                 [:sprite :d]
                 dir)
        player (sprites/proc-cycle gamestate player)]
    (assoc-in scenedata
      [:data :player]
      player)))

(defn draw-scene [gamestate scenedata]
  (let [{{:keys [bumper player]} :data} scenedata
        ctx (:ctx gamestate)]
    (let [{:keys [x y w h]} bumper]
      (.fillRect ctx
        x y w h))
    (sprites/draw gamestate player)))
