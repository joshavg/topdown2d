(ns topdown2d.sprites)

(defn- reset-cycle [obj]
  (let [cycle (get-in obj [:sprite :cycle])
        {:keys [pos from] maxpos :count} cycle
        reset-position? (> (inc pos) maxpos)]
    (-> obj
        ;; set position
        (assoc-in
         [:sprite :cycle :pos]
         (if reset-position?
           from
           (inc pos)))
        ;; timestamp of last cycle is 0
        (assoc-in
         [:sprite :cycle :last-cycle]
         0))))

(defn proc [gamestate obj]
  (let [sprite (:sprite obj)
        sprite-cycle (:cycle sprite)
        {:keys [spc last-cycle]} sprite-cycle
        elapsed (get-in gamestate [:timing :elapsed])]
    ;; new sprite frame?
    (if (> (+ last-cycle elapsed) spc)
      (reset-cycle obj)
      ;; no new sprite, increase last-cycle
      (update-in
       obj
       [:sprite :cycle :last-cycle]
       #(+ % elapsed)))))

(defn reset [obj]
  (assoc-in
   obj
   [:sprite :cycle :pos]
   0))

(defn- pos-in-sprite [sprite d]
  (let [{:keys [size rows]} sprite
        pos (get-in sprite [:cycle :pos])
        row (d rows)]
    {:y (* row size) :x (* pos size)}))

(defn draw [ctx obj]
  (let [{:keys [x y w h d sprite]} obj
        image (:image sprite)
        sprite-size (:size sprite)
        sprite-cycle (:cycle sprite)
        pos (pos-in-sprite sprite d)]
    (.drawImage
     ctx
     image
     (:x pos) (:y pos) sprite-size sprite-size
     x y sprite-size sprite-size)))
