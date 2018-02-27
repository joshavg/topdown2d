(ns topdown2d.demoscene
  (:require
   [topdown2d.collision :as collision]
   [topdown2d.input :as input]
   [topdown2d.sprites :as sprites]))

(defn init [gamestate scenedata]
  (assoc
   scenedata
   :data
   {:player {:x (- (/ (get-in gamestate [:dimensions :w]) 2) 32)
             :y (- (/ (get-in gamestate [:dimensions :h]) 2) 32)
             :w 64 :h 64
             :d :s
             :sprite
             {:image (.getElementById js/document "demo-player")
              :size 64
              :rows {:w 1 :e 3
                     :n 0 :s 2
                     :? 2}
              :cycle {:pos 0
                      :from 1
                      :count 8
                      :last-cycle 0
                      ;; seconds per cycle
                      :spc 0.08}}}
    :viewport {:image (.getElementById js/document "demo-background")
               :keep-in {:x 0 :y 0
                         :w 2239 :h 2235}
               :x 1 :y 1
               :d :?
               :pps 350
               :w (get-in gamestate [:dimensions :w])
               :h (get-in gamestate [:dimensions :h])
               }}))

(defn update-player [gamestate player dir]
  (let [old-dir (:d player)
        new-dir (if (= :? dir) old-dir dir)]
    (as-> player p
      (assoc p :d new-dir)
      (if (= :? dir)
        (sprites/reset player)
        (sprites/proc gamestate p)))))

(defn update-viewport [gamestate viewport dir]
  (collision/move-inside
   (assoc viewport :d dir)
   (:keep-in viewport)
   (collision/pps->px gamestate viewport)))

(defn update-scene [gamestate scenedata]
  (let [player (get-in scenedata [:data :player])
        viewport (get-in scenedata [:data :viewport])
        dir (get-in gamestate [:input :dir])]
    (-> scenedata
        (assoc-in [:data :player]
                  (update-player gamestate player dir))
        (assoc-in [:data :viewport]
                  (update-viewport gamestate viewport dir)))))

(defn draw-scene [gamestate scenedata]
  (let [viewport (get-in scenedata [:data :viewport])
        {:keys [x y w h background]} viewport]
    (.drawImage
     (:ctx gamestate)
     (:image viewport)
     x y w h
     0 0 w h))
  (sprites/draw
   (:ctx gamestate)
   (get-in scenedata [:data :player])))
