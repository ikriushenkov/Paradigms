"use strict";

const cnst = (value) => () => value;

const variables = {
    "x" : 0,
    "y" : 1,
    "z" : 2
};

const variable = (variable) => {
    const i = variables[variable];
    return (...expr) =>  expr[i]
};

const apply = func => (...args) => (...vars) => func(...(args.map(expr => expr(...vars))));

const add = apply((a, b) => a + b);

const subtract = apply((a, b) => a - b);

const multiply = apply((a, b) => a * b);

const divide = apply((a, b) => a / b);

const negate = apply((a) => -a);

const abs = apply(Math.abs);

const iff = apply((a, b, c) => a >= 0 ? b : c);

const one = cnst(1);
const two = cnst(2);

const operations = {
    "+": add,
    "-": subtract,
    "*": multiply,
    "/": divide,
    "iff" : iff,
    "abs" : abs,
    "negate" : negate
};

const consts = {
    "one" : 1,
    "two" : 2
};

const lengthOfOperation = {
    "+" : 2,
    "-": 2,
    "*": 2,
    "/": 2,
    "abs": 1,
    "negate" : 1,
    "iff" : 3
};

const parse = function (expression) {
    expression = expression.split(" ").filter((string) => string.length > 0);
    let n = [];
    for (let i = 0; i < expression.length; i++) {
        let expr = expression[i];
        if (expr in operations) {
            const length = lengthOfOperation[expr];
            let exprs = n.splice(n.length - length, length);
            expr = operations[expr](...exprs);
        } else {
            if (expr in consts) {
                expr = cnst(consts[expr]);
            } else {
                if (expr in variables) {
                    expr =  variable(expr);
                } else {
                    expr = cnst(parseInt(expr));
                }
            }
        }
        n.push(expr);
    }
    return n[0];
};