"use strict";

const AbstractOperation = function(sign, apply, applyDiff) {
    const op = function(...exprs) {
        return Operation.apply(this, exprs);
    };
    op.prototype = new Operation;
    op.prototype.sign = sign;
    op.prototype.apply = apply;
    op.prototype.applyDiff = applyDiff;
    return op;
}

function Operation(...exprs) {
    this.exprs = exprs;
}

Operation.prototype.evaluate = function(...values) {
    return this.apply(...this.exprs.map((expr) =>
        expr.evaluate(...values)));
}

Operation.prototype.toString = function() {
    return this.exprs.join(" ") + " " + this.sign;
}

Operation.prototype.diff = function(variable) {
    return this.applyDiff(...this.exprs, ...this.exprs.map((expr) =>
        expr.diff(variable)), variable);
}

const AbstractValue = (evaluate, diff) => function (value) {
    this.evaluate = (...values) => evaluate(value, ...values);
    this.toString = () => value.toString();
    this.diff = (variable) => diff(value, variable);
    this.prefix = this.toString;
};

const Const = AbstractValue((value) => value, () => ConstZero);

const ConstZero = new Const(0);
const ConstOne = new Const(1);
const ConstNegateTwo = new Const(-2);

const variables = {
    "x" : 0,
    "y" : 1,
    "z" : 2
};

const Variable = AbstractValue((variable, ...values) => values[variables[variable]],
    (variable, diffVariable) => variable === diffVariable ? ConstOne : ConstZero);

// :NOTE: Прототипы
// :NOTE: копипаста
// :NOTE: const Add = f("+", (a, b) => a + b, (variable) => new Add(a.diff(variable), b.diff(variable)));
const Add = AbstractOperation("+",
    (a, b) => a + b,
    (a, b, diffA, diffB) => new Add(diffA, diffB));

const Subtract = AbstractOperation("-",
    (a, b) => a - b,
    (a, b, diffA, diffB) => new Subtract(diffA, diffB));

const Multiply = AbstractOperation("*",
    (a, b) => a * b,
    (a, b, diffA, diffB) => new Add(new Multiply(diffA, b),
        new Multiply(a, diffB)));

const Divide = AbstractOperation("/",
    (a, b) => a / b,
    (a, b, diffA, diffB) =>  new Divide(new Subtract(new Multiply(diffA, b),
        new Multiply(a, diffB)), new Multiply(b, b)));

const Negate = AbstractOperation("negate",
    (a) => -a,
    (a, diffA) => new Negate(diffA));

const Gauss = AbstractOperation("gauss",
    (a, b, c, x) => a * Math.exp(-((x - b) ** 2) / (2 * c ** 2)),
    (a, b, c, x, diffA, diffB, diffC, diffX, variable) => new Add(new Multiply(diffA, new Gauss(ConstOne, b, c, x)),
        new Multiply(a, new Multiply(new Gauss(ConstOne, b, c, x), new Divide(
            new Multiply(new Subtract(x, b), new Subtract(x, b)), new Multiply(ConstNegateTwo,
                new Multiply(c, c))).diff(variable)))));


const operations = {
    "+": Add,
    "-": Subtract,
    "*": Multiply,
    "/": Divide,
    "negate" : Negate,
    "gauss" : Gauss
};

const lengthOfOperation = {
    "+" : 2,
    "-": 2,
    "*": 2,
    "/": 2,
    "negate" : 1,
    "gauss" : 4
};

const parse = function (expression) {
    expression = expression.split(" ").filter((string) => string.length > 0);
    let n = [];
    for (let i = 0; i < expression.length; i++) {
        let expr = expression[i];
        if (expr in operations) {
            const length = lengthOfOperation[expr];
            let exprs = n.splice(n.length - length, length);
            expr = new operations[expr](...exprs);
        } else {
            if (expr in variables) {
                expr = new Variable(expr);
            } else {
                expr = new Const(parseInt(expr));
            }
        }
        n.push(expr);
    }
    return n[0];
};

const parsePrefix = function (expression) {
    expression = expression.replace(/[(, )]/g, (expr) => " " + expr + " ").split(" ").
    filter((string) => string.length > 0);
    let i = 0;
    const nextExpr = () => expression[i++];
    const identificationParse = function (expr) {
        if (expr === '(') {
            return parseOperation(nextExpr());
        } else {
            if (expr in variables) {
                return new Variable(expr);
            } else {
                if (!isNaN(expr)) {
                    return new Const(parseInt(expr));
                } else {
                    throw new Error("Unknown string " + expr);
                }
            }
        }
    };
    const parseOperation = function (operation) {
        if (operation in operations) {
            let n = [];
            let expr = nextExpr();
            while (i < expression.length && expr !== ')') {
                n.push(identificationParse(expr));
                expr = nextExpr();
            }
            if (expr === ')') {
                if (n.length !== lengthOfOperation[operation]) {
                    throw new Error("Expected " + lengthOfOperation[operation] + " values for "
                        + operation);
                }
                return new operations[operation](...n);
            } else {
                throw new Error("Expected ) for " + operation);
            }
        } else {
            throw new Error(operation + " is an unknown operation");
        }
    };
    const ans = identificationParse(nextExpr());
    if (i < expression.length) {
        throw new Error("Expected " + expression[i] + ", which cannot be parsed");
    }
    return ans;
};