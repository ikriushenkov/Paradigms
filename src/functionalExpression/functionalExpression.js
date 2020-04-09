"use strict";

const cnst = (value) => () => value;

const variables = {
    "x" : 0,
    "y" : 1,
    "z" : 2
};

const variable = (variable) => (...expr) => expr[variables[variable]];

const apply = function (func) {
    return function () {
        let expr = arguments;
        return function () {
            let values = [];
            for (let i = 0; i < expr.length; i++) {
                values.push(expr[i](...arguments));
            }
            return func(...values);
        }
    }
};

const add = apply((a, b) => a + b);

const subtract = apply((a, b) => a - b);

const multiply = apply((a, b) => a * b);

const divide = apply((a, b) => a / b);

const negate = apply((a) => -a);

const abs = apply((a) => Math.abs(a));

const iff = apply((a, b, c) => a >= 0 ? b : c);

const parse = function (expression) {
    expression = expression.split(" ").filter(function (string) {
        return string.length > 0;
    });
    let n = [];
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
    const miniParse = function (string) {
        if (string in consts) {
            return cnst(consts[string]);
        }
        for (let i = 0; i < string.length; i++) {
            if ((string[i] < '0' || string[i] > '9') &&
                !(string.length > 0 && i === 0 && string[i] === '-')) {
                return variable(string);
            }
        }
        return cnst(parseInt(string));
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
    for (let i = 0; i < expression.length; i++) {
        let temp = expression[i];
        if (temp in operations) {
            const length = lengthOfOperation[temp];
            if (n.length < length) {
                throw new Error("Not enough input data for " + temp);
            }
            let expr = [];
            expr = n.splice(n.length - length, length);
            temp = operations[temp](...expr);
        } else {
            temp = miniParse(temp);
        }
        n.push(temp);
    }
    if (n.length > 1) {
        throw new Error("There are too many numbers or variables");
    }
    return n[0];
};