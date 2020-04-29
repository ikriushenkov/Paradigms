"use strict";

function abstractOperation(sign, apply, applyDiff) {
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
    return this.apply(...this.exprs.map(expr => expr.evaluate(...values)));
};

Operation.prototype.toString = function() {
    return this.exprs.join(" ") + " " + this.sign;
};

Operation.prototype.diff = function(variable) {
    return this.applyDiff(...this.exprs, ...this.exprs.map((expr) =>
        expr.diff(variable)), variable);
};

const AbstractValue = (evaluate, diff) => function (value) {
    this.evaluate = (...values) => evaluate(value, ...values);
    this.toString = () => value.toString();
    this.diff = (variable) => diff(value, variable);
};

const Const = AbstractValue((value) => value, () => Consts.ZERO);

const Consts = {"ZERO" : new Const(0),
    "ONE": new Const(1),
    "NEGATETWO": new Const(-2)};

const variables = {
    "x" : 0,
    "y" : 1,
    "z" : 2
};

const Variable = AbstractValue((variable, ...values) => values[variables[variable]],
    (variable, diffVariable) => variable === diffVariable ? Consts.ONE : Consts.ZERO);

const Add = abstractOperation("+",
    (a, b) => a + b,
    (a, b, diffA, diffB) => new Add(diffA, diffB));

const Subtract = abstractOperation("-",
    (a, b) => a - b,
    (a, b, diffA, diffB) => new Subtract(diffA, diffB));

const Multiply = abstractOperation("*",
    (a, b) => a * b,
    (a, b, diffA, diffB) => new Add(new Multiply(diffA, b),
        new Multiply(a, diffB)));

const Divide = abstractOperation("/",
    (a, b) => a / b,
    (a, b, diffA, diffB) =>  new Divide(new Subtract(new Multiply(diffA, b),
        new Multiply(a, diffB)), new Multiply(b, b)));

const Negate = abstractOperation("negate",
    (a) => -a,
    (a, diffA) => new Negate(diffA));

const Gauss = abstractOperation("gauss",
    (a, b, c, x) => a * Math.exp(-((x - b) ** 2) / (2 * c ** 2)),
    (a, b, c, x, diffA, diffB, diffC, diffX, variable) => new Add(new Multiply(diffA, new Gauss(Consts.ONE, b, c, x)),
        new Multiply(a, new Multiply(new Gauss(Consts.ONE, b, c, x), new Divide(
            new Multiply(new Subtract(x, b), new Subtract(x, b)), new Multiply(Consts.NEGATETWO,
                new Multiply(c, c))).diff(variable)))));

const operations = {
    "+": Add,
    "-": Subtract,
    "*": Multiply,
    "/": Divide,
    "negate" : Negate,
    "gauss" : Gauss,
    "mean" : Mean,
    "var" : Var
};

const lengthOfOperation = {
    "+" : 2,
    "-": 2,
    "*": 2,
    "/": 2,
    "negate" : 1,
    "gauss" : 4
};

function parse(expression) {
    expression = expression.split(" ").filter((string) => string.length > 0);
    let stack = [];
    for (let i = 0; i < expression.length; i++) {
        let expr = expression[i];
        if (expr in operations) {
            const length = lengthOfOperation[expr];
            expr = new operations[expr](...(stack.splice(-length)));
        } else {
            if (expr in variables) {
                expr = new Variable(expr);
            } else {
                expr = new Const(parseInt(expr));
            }
        }
        stack.push(expr);
    }
    return stack.pop();
}