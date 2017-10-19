(ns topdown2d.objects)

(defn in? [obj container]
  (let [{:keys [x y w h]} obj
        {cx :x cy :y cw :w ch :h} container]
    (and
      (> x cx)
      (> y cy)
      (< (+ x w) (+ cx cw))
      (< (+ y h) (+ cy ch)))))

(defn moved-object [obj]
  (let [{:keys [x y v d]} obj]
    (cond
      (= d :w)
        (assoc obj
          :x (- x v))
      (= d :e)
        (assoc obj
          :x (+ x v))
      (= d :n)
        (assoc obj
          :y (- y v))
      (= d :s)
        (assoc obj
          :y (+ y v))
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

(defn move-inside [obj container]
  (let [moved (moved-object obj)]
    (if (in? moved container)
      moved
      (bump-in-wall obj container))))

(defn move-inside-gamestate [gamestate obj]
  (let [container (assoc (:dimensions gamestate)
                    :x 0
                    :y 0)]
    (move-inside obj container)))
