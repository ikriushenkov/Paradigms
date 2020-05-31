; HW10

(defn constant [value] (fn [_] value))

(defn variable [name] (fn [vars] (vars name)))

(defn abstract-operation [operation] (fn [& args] (fn [& vars] (apply operation
                                      (mapv (fn [arg] (apply arg vars)) args)))))

(def add (abstract-operation +))

(def subtract (abstract-operation -))

(def multiply (abstract-operation *))

(def my-divide (fn ([a] (/ (double a)))
                 ([a, & vars] (/ (double a) (apply * vars)))))

(def divide (abstract-operation my-divide))

(def negate (abstract-operation (fn [a] (- a))))

(defn exp [a] (Math/exp a))

(defn sumexp' [& exprs] (apply + (mapv exp exprs)))

(def sumexp (abstract-operation sumexp'))

(defn softmax' [& exprs] (/ (double (exp (first exprs))) (apply sumexp' exprs)))

(def softmax (abstract-operation softmax'))

(def operations {'+ add,
                 '- subtract,
                 '* multiply,
                 '/ divide,
                 'negate negate,
                 'sumexp sumexp,
                 'softmax softmax})

(def variables (set ['x, 'y, 'z]))

(defn parse [expression] (cond (contains? variables expression) (variable (str expression))
                               (number? expression) (constant expression)
                               (contains? operations (first expression)) (apply (operations (first expression)) (mapv parse (rest expression)))))

(defn parseFunction [expression] (parse (read-string expression)))

; HW11

(definterface Expression
  (diff [diff-var])
  (evaluate [vars])
  (toStr []))

(defn evaluate [expression, vars] (.evaluate expression vars))

(defn toString [expression] (.toStr expression))

(defn diff [expression, diff-var] (.diff expression diff-var))

(declare Consts)

(deftype Constant_ [value]
  Expression
  (evaluate [_, _] value)
  (toStr [_] (format "%.1f" value))
  (diff [_, _] (Consts 0)))

(defn Constant [value] (Constant_. value))

(def Consts {0 (Constant 0), 1 (Constant 1)})

(deftype Variable_ [var]
  Expression
  (evaluate [_, vars] (vars var))
  (toStr [_] var)
  (diff [_, diff-var] (if (= var diff-var) (Consts 1) (Consts 0))))

(defn Variable [value] (Variable_. value))

(deftype Abstract_operation [operation, strOperation, diffFunc, values]
  Expression
  (evaluate [_, vars] (apply operation (mapv (fn [expr] (evaluate expr vars)) values)))
  (toStr [_] (str "(" strOperation " " (clojure.string/join " " (mapv toString values)) ")"))
  (diff [_, diff-var] (diffFunc values (mapv (fn [expr] (diff expr diff-var)) values))))

(defn constructor [operation, strOperation, diffFunc] (fn [& args] (Abstract_operation. operation strOperation diffFunc args)))

(def Add (constructor + '+ (fn [_, diff-vars] (apply Add diff-vars))))

(def Subtract (constructor - '- (fn [_, diff-vars] (apply Subtract diff-vars))))

(declare Multiply)
(declare Divide)

(defn binary-multiply [[a, b], [diff-a, diff-b]] (vector (Multiply a b) (Add (Multiply a diff-b) (Multiply diff-a b))))

(defn diff-multiply [vars, diff-vars] (second (reduce binary-multiply (mapv list vars diff-vars))))

(defn binary-divide [a, b, diff-a, diff-b] (Divide (Subtract (Multiply diff-a b) (Multiply a diff-b)) (Multiply b b)))

(defn diff-divide [[a & vars], [diff-a & diff-vars]] (if (empty? vars) diff-a
                                     (binary-divide a (apply Multiply vars) diff-a (diff-multiply vars diff-vars))))

(def Multiply (constructor * '* diff-multiply))

(def Negate (constructor - 'negate (fn [_, [diff-a]] (Negate diff-a))))

(def Divide (constructor my-divide '/ diff-divide))

(def Exp (constructor exp 'exp (fn [[a], [diff-a]] (Multiply diff-a (Exp a)))))

(defn diff-sumexp [vars, diff-vars] (apply Add (mapv (fn [a, diff-a] (Multiply diff-a (Exp a))) vars diff-vars)))

(def Sumexp (constructor sumexp' 'sumexp diff-sumexp))

(def Softmax (constructor softmax' 'softmax (fn [args, diff-args] (binary-divide (Exp (first args)) (apply Sumexp args)
                                     (Multiply (first diff-args) (Exp (first args))) (diff-sumexp args diff-args)))))

(def object-operations {'+       Add,
                        '-       Subtract,
                        '*       Multiply,
                        '/       Divide,
                        'negate  Negate,
                        'exp     Exp,
                        'sumexp  Sumexp,
                        'softmax Softmax})


(defn parse_ [expression] (cond (contains? variables expression) (Variable (str expression))
                                (number? expression) (Constant expression)
                                (contains? object-operations (first expression)) (apply (object-operations (first expression)) (mapv parse_ (rest expression)))))

(defn parseObject [expression] (parse_ (read-string expression)))