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

Operation.prototype.prefix = function () {
    return "(" + this.sign + " " + this.exprs.map((expr) => expr.prefix()).join(" ") + ")";
}

Operation.prototype.postfix = function () {
    return "(" + this.exprs.map((expr) => expr.postfix()).join(" ") + " " + this.sign +  ")";
}

const AbstractValue = (evaluate, diff) => function (value) {
    this.evaluate = (...values) => evaluate(value, ...values);
    this.toString = () => value.toString();
    this.diff = (variable) => diff(value, variable);
    this.prefix = this.toString;
    this.postfix = this.toString;
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

const mean = (...values) => values.reduce(sum, 0) / values.length;

const Mean = abstractOperation("mean",
    mean,
    function (...values) {
        let exprs = values.splice(values.length / 2, values.length / 2);
        if (exprs.length === 0) {
            return Consts.ZERO;
        }
        let ans = exprs[0];
        for (let i = 1; i < exprs.length; i++) {
            ans = new Add(ans, exprs[i]);
        }
        return new Divide(ans, new Const(exprs.length));
    });


const Var = abstractOperation("var",
    function (...values) {
        const mn = mean(...values);
        return mean(...values.map((expr) => (expr - mn) ** 2));
    },
    function (...values) {
        let variable = values.pop();
        let exprs = values.splice(0, values.length / 2);
        let left = new Mean(...exprs.map((expr) => new Multiply(expr, expr))).diff(variable);
        let right = new Mean(...exprs);
        right = new Multiply(right, right).diff(variable);
        return new Subtract(left, right);
    });

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

const anyLength = {
    "mean" : 1,
    "var" : 1
};

const isCorrectLength = (length, operation) => (operation in anyLength) ? true :
    length === lengthOfOperation[operation];

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

function createError(name, message) {
    let Err = function (...values) {
        Error.call(this, message(...values));
    };
    Err.prototype = Object.create(Error.prototype);
    Err.prototype.name = name;
    return Err;
}

const EmptyExpressionError = createError("EmptyExpressionError", () => "Empty expression");

const IllegalStringError = createError("IllegalStringError", (string, index) => "Illegal string " + string +
    " at position " + index);

const MissingClosingParenthesisError = createError("MissingClosingParenthesisError", (index) =>
    "Expected closing parenthesis at position " + index);

const WrongLengthOperationError = createError("WrongLengthOperationError", (operation, length, index) =>
    "Expected " + lengthOfOperation[operation] + " values for " + operation + " but detect " + length + " values in position " + index);

const IllegalOperationError = createError("IllegalOperationError", (operation, index) => "Illegal operation " +
    operation + " at position " + index);

function parsePrefix(expression) {
    return parseFix(expression, function(getStack, parseFunc, nextExpr) {
        const operation = nextExpr();
        return parseFunc(getStack(), operation);
    });
}

function parsePostfix(expression) {
    return parseFix(expression, (getStack, parseFunc, nextExpr, thisExpr) => parseFunc(getStack(), thisExpr(), nextExpr()));
}

function parseFix(expression, parseFunc) {
    expression = expression.replace(/[(, )]/g, (expr) => " " + expr + " ").split(" ").
    filter((string) => string.length > 0);
    if (expression.length === 0) {
        throw new EmptyExpressionError().toString();
    }
    let i = 0;
    const nextExpr = () => expression[i++];
    const thisExpr = () => expression[i - 1];
    const identificationParse = function (expr) {
        if (expr === '(') {
            return parseFunc(getStack, parseOperation, nextExpr, thisExpr);
        } else {
            if (expr in variables) {
                return new Variable(expr);
            } else {
                if (!isNaN(expr)) {
                    return new Const(parseInt(expr));
                } else {
                    throw new IllegalStringError(expr, i).toString();
                }
            }
        }
    };
    const parseOperation = function(stack, operation) {
        if (operation in operations) {
            if (thisExpr() === ')') {
                if (isCorrectLength(stack.length, operation)) {
                    return new operations[operation](...stack);
                } else {
                    throw new WrongLengthOperationError(operation, stack.length, i).toString();
                }
            } else {
                throw new MissingClosingParenthesisError(i).toString();
            }
        } else {
            throw new IllegalOperationError(operation, i).toString();
        }
    }
    const getStack = function () {
        let stack = [];
        let expr = nextExpr();
        while (i < expression.length && expr !== ')' && !(expr in operations)) {
            stack.push(identificationParse(expr));
            expr = nextExpr();
        }
        return stack;
    };
    const ans = identificationParse(nextExpr());
    if (i < expression.length) {
        throw new IllegalStringError(expression[i], i).toString();
    }
    return ans;
}

const sum = (s, expr) => s + expr;