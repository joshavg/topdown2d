(ns topdown2d.input)

(def keysdown (atom {}))

(defn keydown? [code]
  (get @keysdown (name code) false))

(.addEventListener
 js/document
 "keydown"
 (fn [event]
   (swap! keysdown #(assoc % (.-code event) true))
   false))

(.addEventListener
 js/document
 "keyup"
 (fn [event]
   (swap! keysdown
          #(assoc % (.-code event) false))
   false))

(defn dir []
  (cond
    (keydown? :ArrowLeft) :w
    (keydown? :ArrowRight) :e
    (keydown? :ArrowUp) :n
    (keydown? :ArrowDown) :s
    :else :?))
