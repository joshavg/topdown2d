(ns topdown2d.objects)

(defn move [obj]
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
