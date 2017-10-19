(ns topdown2d.objects)

(defn in? [obj container]
  (let [{:keys [x y w h]} obj
        {cx :x cy :y cw :w ch :h} container]
    (and
      (> x cx)
      (> y cy)
      (< (+ x w) (+ cx cw))
      (< (+ y h) (+ cy ch)))))

(defn moved-object [obj pxs]
  (let [{:keys [x y d]} obj]
    (cond
      (= d :w)
        (assoc obj
          :x (- x pxs))
      (= d :e)
        (assoc obj
          :x (+ x pxs))
      (= d :n)
        (assoc obj
          :y (- y pxs))
      (= d :s)
        (assoc obj
          :y (+ y pxs))
      :else obj)))

(defn bump-in-wall [obj container]
  (let [{:keys [x y w h d]} obj
        {cx :x cy :y cw :w ch :h} container]
    (case d
      :w (assoc obj :x (inc cx))
      :e (update obj :x #(- (+ cx cw) w 1))
      :n (assoc obj :y (inc cy))
      :s (update obj :y #(- (+ cy ch) h 1))
      :? obj)))

(defn pps->px [gamestate obj]
  (let [prev (get-in gamestate [:timing :prev])
        now (get-in gamestate [:timing :now])
        secs (/ (- now prev) 1000)
        pps (:pps obj)]
    (* pps secs)))

(defn move-inside [obj container pxs]
  (let [moved (moved-object obj pxs)]
    (if (in? moved container)
      moved
      (bump-in-wall obj container))))

(defn move-inside-gamestate [gamestate obj]
  (let [pxs (pps->px gamestate obj)
        container (assoc
                    (:dimensions gamestate)
                    :x 0
                    :y 0)]
    (move-inside obj container pxs)))
