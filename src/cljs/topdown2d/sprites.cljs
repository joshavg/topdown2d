(ns topdown2d.sprites)

(defn proc-cycle [gamestate obj]
  (let [sprite (:sprite obj)
        sprite-cycle (:cycle sprite)
        maxpos (:count sprite-cycle)
        {:keys [pos spc last-cycle]} sprite-cycle
        elapsed (get-in gamestate [:timing :elapsed])]
    ; new sprite frame?
    (if (> (+ last-cycle elapsed) spc)
      ; start cycle from new?
      ; reset last-cycle
      (assoc-in
        (if (> (inc pos) maxpos)
          ; restart cycle
          (assoc-in obj
            [:sprite :cycle :pos]
            (:from sprite-cycle))
          ; run cycle
          (update-in obj
            [:sprite :cycle :pos]
            inc))
        [:sprite :cycle :last-cycle]
        0)
      (update-in obj
        [:sprite :cycle :last-cycle]
        #(+ % elapsed)))))

(defn pos-in-sprite [sprite]
  (let [{:keys [d size rows]} sprite
        pos (get-in sprite [:cycle :pos])
        row (d rows)]
    {:y (* row size) :x (* pos size)}))

(defn draw [gamestate obj]
  (let [{:keys [x y w h d sprite]} obj
        image (:image sprite)
        sprite-size (:size sprite)
        {:keys [ctx]} gamestate
        sprite-cycle (:cycle sprite)
        pos (pos-in-sprite sprite)]
    (.drawImage ctx
      image
      (:x pos) (:y pos) sprite-size sprite-size
      x y sprite-size sprite-size)))
