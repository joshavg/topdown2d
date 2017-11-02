(ns topdown2d.collision)

(defn in? [obj container]
  (let [{:keys [x y w h]} obj
        {cx :x cy :y cw :w ch :h} container]
    (and
      (> x cx)
      (> y cy)
      (< (+ x w) (+ cx cw))
      (< (+ y h) (+ cy ch)))))

(defn collide? [obj obj2]
  (let [{:keys [x y w h]} obj
        {ox :x oy :y ow :w oh :h} obj2]
    (or
      ; top left corner
      (and (>= x ox) (>= y oy)
        (<= x (+ ox ow)) (<= y (+ oy oh)))
      ; top right corner
      (and (>= (+ x h) ox) (>= y oy)
        (<= (+ x h) (+ ox ow)) (<= y (+ oy oh)))
      ; bottom left corner
      (and (>= x ox) (>= (+ y h) oy)
        (<= x (+ ox ow)) (<= (+ y h) (+ oy oh)))
      ; bottom right corner
      (and (>= (+ x w) ox) (>= (+ y h) oy)
        (<= (+ x w) (+ ox ow)) (<= (+ y h) (+ oy oh))))))

(defn moved-object [obj pxs]
  (let [{:keys [x y d]} obj]
    (case d
      :w (assoc obj
           :x (- x pxs))
      :e (assoc obj
           :x (+ x pxs))
      :n (assoc obj
           :y (- y pxs))
      :s (assoc obj
           :y (+ y pxs))
      obj)))

(defn bump-into [obj obj2]
  (let [{:keys [x y w h d]} obj
        {ox :x oy :y ow :w oh :h} obj2]
    (case d
      :w (assoc obj :x (+ ox ow 1))
      :e (assoc obj :x (dec (- ox w)))
      :n (assoc obj :y (+ oy oh 1))
      :s (assoc obj :y (dec (- oy h)))
      obj)))

(defn bump-inside-container [obj container]
  (let [{:keys [x y w h d]} obj
        {cx :x cy :y cw :w ch :h} container]
    (case d
      :w (assoc obj :x (inc cx))
      :e (assoc obj :x (- (+ cx cw) w 1))
      :n (assoc obj :y (inc cy))
      :s (assoc obj :y (- (+ cy ch) h 1))
      obj)))

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
      (bump-inside-container obj container))))
