(ns topdown2d.objects)

(defn in? [obj box]
  (let [{:keys [x y w h]} obj
        {bx :x by :y bw :w bh :h} box]
    (println obj x)
    (and
      (> x bx)
      (> y by)
      (< (+ x w) (+ bx bw))
      (< (+ y h) (+ by bh)))))

(defn move [obj]
  (let [{:keys [x y v d]} obj
        moved (cond
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
                :else obj)
        keep-in (:keep-in obj)]
    (if (or (nil? keep-in) (and keep-in (in? moved keep-in)))
      moved
      obj)))
