"use strict";

const AbstractOperation = (sign, apply, diff) => function (...exprs) {
    this.evaluate = (...values) => apply(...exprs.map((expr) => expr.evaluate(...values)));
    this.toString = () => exprs.join(" ") + " " + sign;
    this.diff = (variable) => diff(variable, ...exprs);
};

const AbstractValue = (evaluate, diff) => function (value) {
    this.evaluate = (...values) => evaluate(value, ...values);
    this.toString = () => value.toString();
    this.diff = (variable) => diff(value, variable);
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

const Add = AbstractOperation("+",
    (a, b) => a + b,
    (variable, a, b) => new Add(a.diff(variable), b.diff(variable)));

const Subtract = AbstractOperation("-",
    (a, b) => a - b,
    (variable, a, b) => new Subtract(a.diff(variable), b.diff(variable)));

const Multiply = AbstractOperation("*",
    (a, b) => a * b,
    (variable, a, b) => new Add(new Multiply(a.diff(variable), b),
        new Multiply(a, b.diff(variable))));

const Divide = AbstractOperation("/",
    (a, b) => a / b,
    (variable, a, b) =>  new Divide(new Subtract(new Multiply(a.diff(variable), b),
        new Multiply(a, b.diff(variable))), new Multiply(b, b)));

const Negate = AbstractOperation("negate",
    (a) => -a,
    (variable, a) => new Subtract(ConstZero, a.diff(variable)));

const Gauss = AbstractOperation("gauss",
    (a, b, c, x) => a * Math.exp(-((x - b) ** 2) / (2 * c ** 2)),
    (variable, a, b, c, x) => new Add(new Multiply(a.diff(variable), new Gauss(new Const(1), b, c, x)),
        new Multiply(a, new Multiply(new Gauss(new Const(1), b, c, x), new Divide(
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