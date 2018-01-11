(ns topdown2d.sprites)

(defn proc-cycle [gamestate obj]
  (let [sprite (:sprite obj)
        sprite-cycle (:cycle sprite)
        from (:from sprite-cycle)
        maxpos (:count sprite-cycle)
        {:keys [pos spc last-cycle]} sprite-cycle
        restart? (> (inc pos) maxpos)
        elapsed (get-in gamestate [:timing :elapsed])]
    ; new sprite frame?
    (if (> (+ last-cycle elapsed) spc)
      ; start cycle from new?
      ; reset last-cycle
      (-> obj
        (assoc-in
          [:sprite :cycle :pos]
          (if restart? from (inc pos)))
        (assoc-in
          [:sprite :cycle :last-cycle]
          0))
      (update-in obj
        [:sprite :cycle :last-cycle]
        #(+ % elapsed)))))

(defn reset-cycle [obj]
  (assoc-in
    obj
    [:sprite :cycle :pos]
    0))

(defn pos-in-sprite [sprite d]
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
    (.drawImage ctx
      image
      (:x pos) (:y pos) sprite-size sprite-size
      x y sprite-size sprite-size)))
