(defn constant [value] (fn [_] value))

(defn variable [name] (fn [vars] (vars name)))

(defn abstract-operation [operation] (fn [& args] (fn [& vars] (apply operation
                                      (mapv (fn [arg] (apply arg vars)) args)))))

(def add (abstract-operation +))

(def subtract (abstract-operation -))

(def multiply (abstract-operation *))

(def divide (abstract-operation (fn ([a] (/ (double a)))
                                  ([a, & vars] (/ (double a) (apply * vars))))))

(def negate (abstract-operation (fn [a] (- a))))

(defn exp' [a] (Math/exp a))

(def exp (abstract-operation exp'))

(def ln (abstract-operation (fn [a] (Math/log (Math/abs a)))))

(defn sumexps [& exprs] (apply + (mapv exp' exprs)))

(def sumexp (abstract-operation sumexps))

(def softmax (abstract-operation (fn [& exprs] (/ (double (exp' (first exprs))) (apply sumexps exprs)))))

(def operations {'+ add,
                 '- subtract,
                 '* multiply,
                 '/ divide,
                 'negate negate,
                 'exp exp,
                 'ln ln,
                 'sumexp sumexp,
                 'softmax softmax})

(def variables (set ['x, 'y, 'z]))

(defn parse [expression] (cond (contains? variables expression) (variable (str expression))
                               (number? expression) (constant expression)
                               (contains? operations (first expression)) (apply (operations (first expression)) (mapv parse (rest expression)))))

(defn parseFunction [expression] (parse (read-string expression)))