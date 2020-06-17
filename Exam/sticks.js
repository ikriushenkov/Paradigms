"use strict";

class Board {
    cells = [];
    constructor(row, column) {
        this.empty = row * (column - 1) + (row - 1) * column;
        this.row = row * 2 - 1;
        this.column = column * 2 - 1;
        for (let i = 0; i < row; i++) {
            this.cells[i * 2] = [];
            this.cells[i * 2 + 1] = [];
            for (let j = 0; j < column; j++) {
                this.cells[i * 2][j * 2] = '.';
                this.cells[i * 2][j * 2 + 1] = ' ';
            }
            for (let j = 0; j < column * 2 - 1; j++) {
                this.cells[i * 2 + 1][j] = ' ';
            }
        }
    }

    isValid(i, j) {
        return i >= 0 && i < this.row && j >= 0 && j < this.column && (i % 2) !== (j % 2) &&
            this.cells[i][j] === ' ';
    }

    haveValid() {
        return this.empty !== 0;
    }

    checkWin(cell) {
        let count = 0;
        for (let i = 1; i < this.row - 1; i++) {
            for (let j = 1; j < this.column - 1; j++) {
                if (this.goodCell(i, j)) {
                    this.cells[i][j] = cell;
                    count++;
                }
            }
        }
        return count;
    }

    goodCell(i, j) {
        return this.cells[i][j] === ' ' && this.cells[i - 1][j] === '_' &&
            this.cells[i + 1][j] === '_' && this.cells[i][j - 1] === '|' &&
            this.cells[i][j + 1] === '|';
    }

    bestCell() {
        let row = -1;
        let column = -1;
        let max = 0;
        for (let i = 0; i < this.row; i++) {
            for (let j = 0; j < this.column; j++) {
                if (this.isValid(i, j)) {
                    if (i % 2 === 1) {
                        this.cells[i][j] = '|';
                    } else {
                        this.cells[i][j] = '_';
                    }
                    let res = this.checkCell();
                    if (res > max) {
                        max = res;
                        row = i;
                        column = j;
                    }
                    this.cells[i][j] = ' ';
                }
            }
        }
        if (row === -1) {
            for (let i = 0; i < this.row; i++) {
                for (let j = 0; j < this.column; j++) {
                    if (this.isValid(i, j)) {
                        row = i;
                        column = j;
                    }
                }
            }
        }
        return [row, column];
    }

    checkCell() {
        let count = 0;
        for (let i = 1; i < this.row - 1; i++) {
            for (let j = 1; j < this.column - 1; j++) {
                if (this.goodCell(i, j)) {
                    count++;
                }
            }
        }
        return count;
    }

    makeMove(move) {
        let i = move.getI();
        let j = move.getJ();
        if (i % 2 === 1) {
            this.cells[i][j] = '|';
        } else {
            this.cells[i][j] = '_';
        }
        this.empty--;
        // :NOTE: * Нет дополнительного хода за закрашивание квадрата
        return this.checkWin(move.getCell());
    }

    toString() {
        let res = " ";
        for (let i = 1; i <= this.column; i++) {
            res += i;
        }
        res += "\n";
        for (let i = 0; i < this.row; i++) {
            res += i + 1;
            for (let j = 0; j < this.column; j++) {
                res += this.cells[i][j];
            }
            res += "\n";
        }
        return res;
    }

    getRow() {
        return this.row;
    }
    getColumn() {
        return this.column;
    }
}


class Game {
    constructor(...players) {
        this.players = players;
    }
    play(board, log) {
        let result = [];
        for (let i = 0; i < this.players.length; i++) {
            result[i] = 0;
        }
        for (let i = 0; board.haveValid(); i = (i + 1) % this.players.length) {
            let res = board.makeMove(this.players[i].move(board));
            if (log) {
                console.log(board.toString());
            }
            result[i] += res;
        }
        let max = 0;
        let bestPlayer = -1;
        for (let i = 0; i < result.length; i++) {
            if (result[i] > max) {
                max = result[i];
                bestPlayer = i;
            }
        }
        if (bestPlayer === -1) {
            console.log("Draw");
        } else {
            console.log("Player " + (bestPlayer + 1) + " win!");
        }
        return bestPlayer;
    }
    move(board, player) {
        // :NOTE: # Нет проверки корректности хода
        let move = player.move(board);
        return board.makeMove(move);
    }
}

const randomInt = (max) => Math.floor(Math.random() * (max + 1));

class RandomPlayer {
    constructor(cell) {
        this.cell = cell;
    }
    move(board) {
        while (true) {
            let i = randomInt(board.getRow());
            let j = randomInt(board.getColumn());
            if (board.isValid(i, j)) {
                return new Move(i, j, this.cell);
            }
        }
    }
    getCell() {
        return this.cell;
    }
}

class SequentialPlayer {
    constructor(cell) {
        this.cell = cell;
    }
    move(board) {
        for (let i = 0; i < board.getRow(); i++) {
            for (let j = 0; j < board.getColumn(); j++) {
                if (board.isValid(i, j)) {
                    return new Move(i, j, this.cell);
                }
            }
        }
    }
    getCell() {
        return this.cell;
    }
}

class SmartPlayer {
    constructor(cell) {
        this.cell = cell;
    }
    move(board) {
        let move = board.bestCell();
        return new Move(move[0], move[1], this.cell);
    }
    getCell() {
        return this.cell;
    }
}

class Move {
    constructor(i, j, cell) {
        this.i = i;
        this.j = j;
        this.cell = cell;
    }

    getI() {
        return this.i;
    }
    getJ() {
        return this.j;
    }
    getCell() {
        return this.cell;
    }
}

class Tournament {
    constructor(...players) {
        this.players = players;
    }

    play(row, column, numberRounds) {
        let result = [];
        for (let i = 0; i < this.players.length; i++) {
            result[i] = 0;
        }
        let draw = 0;
        for (let i = 0; i < numberRounds; i++) {
            let board = new Board(row, column);
            let game = new Game(...this.players);
            console.log("Game " + (i + 1) + ": ");
            let res = game.play(board, false);
            if (res === -1) {
                draw++;
            } else {
                result[res]++;
            }
        }
        console.log("Draw: " + draw);
        for (let i = 0; i < result.length; i++) {
            console.log("Player " + (i + 1) + " win: " + result[i]);
        }
    }

}

let board = new Board(4, 3);

let game = new Game(new SmartPlayer('A'), new SequentialPlayer('B'), new RandomPlayer('C'));

game.play(board, true);

let tournament = new Tournament(new RandomPlayer('A'), new SmartPlayer('B'), new SequentialPlayer('C'));

tournament.play(3, 4, 20);