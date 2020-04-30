(defn equalLengthVectors [& vectors] (apply = (mapv count vectors)))

(defn isNumbers [& numbers] (every? identity (mapv number? numbers)))

(defn isVector [vector] (and (vector? vector) (apply isNumbers vector)))

(defn isVectors [& vectors] (every? identity (mapv isVector vectors)))

(defn isMatrix [matrix] (and (vector? matrix) (apply isVectors matrix)))

(defn isMatrixes [& matrix] (every? identity (mapv isMatrix matrix)))

(defn equalSizeMatrix [& matrix] (and (every? identity (mapv (fn [a] (apply = (mapv count a))) matrix))
                                      (apply = (mapv count matrix)) (apply = (mapv (fn [a] (count (first a))) matrix))))

(defn vectorOperation [operation]
      (fn [& vectors]
          {:pre [(apply isVectors vectors) (apply equalLengthVectors vectors)]
           :post [(isVector %)]}
          (apply mapv operation vectors)))

(def v+ (vectorOperation +))

(def v- (vectorOperation -))

(def v* (vectorOperation *))

(defn scalar [& vectors]
      {:pre [(apply isVectors vectors) (apply equalLengthVectors vectors)]
       :post [(number? %)]}
      (reduce + (apply v* vectors)))

(defn vect [& vectors]
      {:pre [(apply isVectors vectors) (apply equalLengthVectors vectors) (= (count (first vectors)) 3)]
       :post [(isVector %)]}
      (letfn [
              (vect' [a, b]
                     [(- (* (nth a 1) (nth b 2)) (* (nth a 2) (nth b 1))),
                      (- (* (nth a 2) (nth b 0)) (* (nth a 0) (nth b 2))),
                      (- (* (nth a 0) (nth b 1)) (* (nth a 1) (nth b 0)))])]
             (reduce vect' vectors)))



(defn v*s [v, & s]
      {:pre [(isVector v) (apply isNumbers s)]
       :post [(isVector %)]}
      (mapv (partial * (reduce * s)) v))

(defn matrixOperation [operation]
      (fn [& matrix]
          {:pre [(apply isMatrixes matrix) (apply equalSizeMatrix matrix)]
           :post [(isMatrix %)]}
          (apply mapv (vectorOperation operation) matrix)))

(def m+ (matrixOperation +))

(def m- (matrixOperation -))

(def m* (matrixOperation *))

(defn m*s [m, & s]
      {:pre [(isMatrix m) (apply isNumbers s)]
       :post [(isMatrix %)]}
      (mapv (fn [v] (apply v*s v s)) m))

(defn m*v [m, v]
      {:pre [(isMatrix m) (isVector v) (= (count (first m)) (count v))]
       :post [(isMatrix %)]}
      (mapv (partial scalar v) m))

(defn transpose [matrix]
      {:pre [(isMatrix matrix)]
       :post [(isMatrix %)]}
      (if (= (count (peek matrix)) 1) (vector (mapv peek matrix))
                                      (conj (transpose (mapv pop matrix)) (mapv peek matrix))))

(defn m*m [& matrix]
      {:pre [(apply isMatrixes matrix)]
       :post [(isMatrix %)]}
      (reduce (fn [a, b] (mapv (partial m*v (transpose b)) a)) matrix))