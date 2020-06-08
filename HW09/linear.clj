(defn equal-length [& vectors] (apply = (apply mapv count vectors)))

(defn is-numbers [& numbers] (apply every? number? numbers))

(defn is-vector [vector] (and (vector? vector) (is-numbers vector)))

(defn is-vectors [& vectors] (apply every? is-vector vectors))

(defn is-matrix [matrix] (and (vector? matrix) (is-vectors matrix)))

(defn is-matrices [& matrix] (apply every? is-matrix matrix))

(defn equal-size-matrix [& matriсes] (and (apply every? equal-length matriсes)
                                          equal-length matriсes
                                          (apply == (mapv (comp count first) matriсes))))

(defn vect? [tensor] (or (number? tensor) (vector? tensor) (and (instance? clojure.lang.Repeat tensor) (vect? (apply vector tensor)))))

(def get-shape (memoize (fn [tensor]
    {:pre [(vect? tensor)]
     :post [(vector? %)]}
    (if (number? tensor) [] (conj (get-shape (first tensor)) (count tensor))))))

(defn is-tensor [tensor] (or (number? tensor) (is-vector tensor) (and (vector? tensor) (apply = (mapv get-shape tensor)) (equal-length tensor))))

(defn is-tensors [& tensors] (apply every? is-tensor tensors))

(defn vector-operation [operation]
  (fn [& vectors]
    {:pre [(is-vectors vectors) (equal-length vectors)]
     :post [(is-vector %)]}
    (apply mapv operation vectors)))

(def v+ (vector-operation +))
(def v- (vector-operation -))
(def v* (vector-operation *))

(defn scalar [& vectors]
  {:pre [(is-vectors vectors) (equal-length vectors)]
   :post [(number? %)]}
  (reduce + (apply v* vectors)))

(defn vect [& vectors]
  {:pre [(is-vectors vectors) (equal-length vectors) (= (count (first vectors)) 3)]
   :post [(is-vector %)]}
  (letfn [
                               (vect' [a, b]
                                 [(- (* (nth a 1) (nth b 2)) (* (nth a 2) (nth b 1))),
                                  (- (* (nth a 2) (nth b 0)) (* (nth a 0) (nth b 2))),
                                  (- (* (nth a 0) (nth b 1)) (* (nth a 1) (nth b 0)))])]
  (reduce vect' vectors)))

(defn v*s [v, & s]
  {:pre [(is-vector v) (is-numbers s)]
   :post [(is-vector %)]}
  (let [mul (apply * s)] (mapv (partial * mul) v)))

(defn matrix-operation [operation]
  (fn [& matrix]
    {:pre [(is-matrices matrix) (equal-size-matrix matrix)]
     :post [(is-matrix %)]}
    (apply mapv (vector-operation operation) matrix)))

(def m+ (matrix-operation +))
(def m- (matrix-operation -))
(def m* (matrix-operation *))

(defn m*s [m, & s]
  {:pre [(is-matrix m) (is-numbers s)]
   :post [(is-matrix %)]}
  (let [mul (apply * s)] (mapv (fn [v] (v*s v mul)) m)))

(defn m*v [m, v]
  {:pre [(is-matrix m) (is-vector v) (= (count (first m)) (count v))]
   :post [(is-vector %)]}
  (mapv (partial scalar v) m))

(defn transpose [matrix]
  {:pre [(is-matrix matrix)]
   :post [(is-matrix %)]}
  (if (= (count (peek matrix)) 1) (vector (mapv peek matrix))
                                                         (conj (transpose (mapv pop matrix)) (mapv peek matrix))))

(defn m*m [& matrix]
  {:pre [(is-matrices matrix)]
   :post [(is-matrix %)]}
  (reduce (fn [a, b] (mapv (partial m*v (transpose b)) a)) matrix))

(defn is-prefix [a, b] (= (drop-last (- (count a) (count b)) a) (drop-last (- (count b) (count a)) b)))

(defn is-prefixes [& shapes] (if (= (count shapes) 1) true (and (is-prefix (first shapes) (nth shapes 1)) (apply is-prefixes (next shapes)))))

(defn tensor-operation [operation] (fn [& tensors]
    {:pre [(is-tensors tensors) (apply is-prefixes (mapv get-shape tensors))]
     :post [(is-tensor %)]}
    (letfn [
            (broadcast [a, b]
              (let [shape-a (get-shape a) shape-b (get-shape b)]
              (cond (= shape-a shape-b) (evaluate a b)
                        (> (count shape-a) (count shape-b)) (broadcast a (repeat (nth shape-a (count shape-b)) b))
                         :else (broadcast (repeat (nth shape-b (count shape-a)) a) b))))
            (evaluate [& tensor] (if (is-numbers tensor) (apply operation tensor) (apply mapv (partial evaluate) tensor)))
            ] (if (= (count tensors) 1) (evaluate (first tensors)) (reduce (partial broadcast) tensors)))))

(def b+ (tensor-operation +))

(def b- (tensor-operation -))

(def b* (tensor-operation *))

(def bd (tensor-operation /))