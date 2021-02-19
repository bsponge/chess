package chessLibOptimized;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {
    private static final int PIECE_MASK = 0B111111;

    private final UUID uuid;
    private Player whitePlayer;
    private Player blackPlayer;

    private int whiteKingX;
    private int whiteKingY;
    private int blackKingX;
    private int blackKingY;

    private boolean isWhiteKingQueenSideCastleAvailable = true;
    private boolean isWhiteKingKingSideCastleAvailable = true;
    private boolean isBlackKingQueenSideCastleAvailable = true;
    private boolean isBlackKingKingSideCastleAvailable = true;

    private final List<Move> movesHistory;

    private int turn;

    // ===========CHESSBOARD============
    //   y  ^
    //      |
    //   7  |
    //      |
    //   ^  |
    //   |  |
    //      |
    //   0  |
    //      ------------------------->
    //      0       ->          7   x
    //

    private final int[][] chessboard;

    public Game() {
        this.chessboard = new int[8][];
        for (int i = 0; i < 8; i++) {
            this.chessboard[i] = new int[8];
        }

        for (int i = 0; i < 8; i++) {
            this.chessboard[i][1] = Piece.PAWN ^ Color.WHITE;
            this.chessboard[i][6] = Piece.PAWN ^ Color.BLACK;
        }

        // ROOKS
        this.chessboard[0][0] = Piece.ROOK ^ Color.WHITE;
        this.chessboard[7][0] = this.chessboard[0][0];
        this.chessboard[0][7] = Piece.ROOK ^ Color.BLACK;
        this.chessboard[7][7] = this.chessboard[0][7];
        // BISHOPS
        this.chessboard[1][0] = Piece.BISHOP ^ Color.WHITE;
        this.chessboard[6][0] = this.chessboard[1][0];
        this.chessboard[1][7] = Piece.BISHOP ^ Color.BLACK;
        this.chessboard[6][7] = this.chessboard[1][7];
        // KNIGHTS
        this.chessboard[2][0] = Piece.KNIGHT ^ Color.WHITE;
        this.chessboard[5][0] = this.chessboard[2][0];
        this.chessboard[2][7] = Piece.KNIGHT ^ Color.BLACK;
        this.chessboard[5][7] = this.chessboard[2][7];
        // QUEENS
        this.chessboard[3][0] = Piece.QUEEN ^ Color.WHITE;
        this.chessboard[3][7] = Piece.QUEEN ^ Color.BLACK;
        // KINGS
        this.chessboard[4][0] = Piece.KING ^ Color.WHITE;
        this.chessboard[4][7] = Piece.KING ^ Color.BLACK;

        this.whiteKingX = 4;
        this.whiteKingY = 0;
        this.blackKingX = 4;
        this.blackKingY = 7;

        uuid = UUID.randomUUID();

        turn = Color.WHITE;

        movesHistory = new ArrayList<>();
    }

    private synchronized int getPieceColor(int x, int y) {
        if (this.chessboard[x][y] == 0) {
            return 0;
        } else {
            return this.chessboard[x][y] & 192;
        }
    }

    public void print() {
        for (int i = 7; i >= 0; --i) {
            for (int j = 0; j < 8; ++j) {
                System.out.printf("%3d ", this.chessboard[j][i]);
            }
            System.out.println();
        }
    }

    private synchronized Move getLastMove() {
        if (movesHistory.size() == 0) {
            return Move.BAD_MOVE;
        } else {
            return movesHistory.get(movesHistory.size() - 1);
        }
    }

    private synchronized boolean canEnPassant(int fromX, int fromY) {
        Move lastMove = getLastMove();
        if (lastMove != Move.BAD_MOVE) {
            if (getPieceColor(fromX, fromY) == Color.WHITE) {
                return Piece.getPieceType(lastMove.getFromPiece()) == Piece.PAWN
                        && lastMove.getFromY() == 6 && lastMove.getToY() == 4;
            } else {
                return Piece.getPieceType(lastMove.getFromPiece()) == Piece.PAWN
                        && lastMove.getFromY() == 1 && lastMove.getToY() == 3;
            }
        } else {
            return false;
        }
    }

    public synchronized boolean isCheck(int color) {
        int kingX = color == Color.WHITE ? whiteKingX : blackKingX;
        int kingY = color == Color.WHITE ? whiteKingY : blackKingY;
        boolean[] arr = new boolean[8];
        for (int i = 0; i < 8; ++i) {
            arr[i] = true;
        }
        for (int i = 0; i < 8; ++i) {
            if (arr[0] && kingX + i <= 7) {
                if (this.chessboard[kingX + i][kingY] != 0
                        && Piece.getPieceColor(this.chessboard[kingX + i][kingY]) != color
                        && (Piece.getPieceType(this.chessboard[kingX + i][kingY]) == Piece.ROOK || Piece.getPieceType(this.chessboard[kingX + i][kingY] ) == Piece.QUEEN)) {
                    return true;
                } else if (this.chessboard[kingX + i][kingY] != 0) {
                    arr[0] = false;
                }
            }
            if (arr[1] && kingX - i >= 0) {
                if (this.chessboard[kingX - i][kingY] != 0
                        && Piece.getPieceColor(this.chessboard[kingX - i][kingY]) != color
                        && (Piece.getPieceType(this.chessboard[kingX - i][kingY]) == Piece.ROOK || Piece.getPieceType(this.chessboard[kingX - i][kingY] ) == Piece.QUEEN)) {
                    return true;
                } else if (this.chessboard[kingX - i][kingY] != 0) {
                    arr[1] = false;
                }
            }
            if (arr[2] && kingY + i <= 7) {
                if (this.chessboard[kingX][kingY + i] != 0
                        && Piece.getPieceColor(this.chessboard[kingX][kingY + i]) != color
                        && (Piece.getPieceType(this.chessboard[kingX][kingY + i]) == Piece.ROOK || Piece.getPieceType(this.chessboard[kingX][kingY + i] ) == Piece.QUEEN)) {
                    return true;
                } else if (this.chessboard[kingX][kingY + i] != 0) {
                    arr[2] = false;
                }
            }
            if (arr[3] && kingY - i >= 0) {
                if (this.chessboard[kingX][kingY - i] != 0
                        && Piece.getPieceColor(this.chessboard[kingX][kingY - i]) != color
                        && (Piece.getPieceType(this.chessboard[kingX][kingY - i]) == Piece.ROOK || Piece.getPieceType(this.chessboard[kingX][kingY - i] ) == Piece.QUEEN)) {
                    return true;
                } else if (this.chessboard[kingX][kingY - i] != 0) {
                    arr[3] = false;
                }
            }
            if (arr[4] && kingX + i <= 7 && kingY - i >= 0) {
                if (this.chessboard[kingX + i][kingY - i] != 0
                        && Piece.getPieceColor(this.chessboard[kingX + i][kingY - i]) != color
                        && (Piece.getPieceType(this.chessboard[kingX + i][kingY - i]) == Piece.BISHOP || Piece.getPieceType(this.chessboard[kingX + i][kingY - i]) == Piece.QUEEN)) {
                    return true;
                } else if (this.chessboard[kingX + i][kingY - i] != 0) {
                    arr[4] = false;
                }
            }
            if (arr[5] && kingX + i <= 7 && kingY + i <= 7) {
                if (this.chessboard[kingX + i][kingY + i] != 0
                        && Piece.getPieceColor(this.chessboard[kingX + i][kingY + i]) != color
                        && (Piece.getPieceType(this.chessboard[kingX + i][kingY + i]) == Piece.BISHOP || Piece.getPieceType(this.chessboard[kingX + i][kingY + i]) == Piece.QUEEN)) {
                    return true;
                } else if (this.chessboard[kingX + i][kingY + i] != 0) {
                    arr[5] = false;
                }
            }
            if (arr[6] && kingX - i >= 0 && kingY - i >= 0) {
                if (this.chessboard[kingX - i][kingY - i] != 0
                        && Piece.getPieceColor(this.chessboard[kingX - i][kingY - i]) != color
                        && (Piece.getPieceType(this.chessboard[kingX - i][kingY - i]) == Piece.BISHOP || Piece.getPieceType(this.chessboard[kingX - i][kingY - i]) == Piece.QUEEN)) {
                    return true;
                } else if (this.chessboard[kingX - i][kingY - i] != 0) {
                    arr[6] = false;
                }
            }
            if (arr[7] && kingX - i >= 0 && kingY + i <= 7) {
                if (this.chessboard[kingX - i][kingY + i] != 0
                        && Piece.getPieceColor(this.chessboard[kingX - i][kingY + i]) != color
                        && (Piece.getPieceType(this.chessboard[kingX - i][kingY + i]) == Piece.BISHOP || Piece.getPieceType(this.chessboard[kingX - i][kingY + i]) == Piece.QUEEN)) {
                    return true;
                } else if (this.chessboard[kingX - i][kingY + i] != 0) {
                    arr[7] = false;
                }
            }
        }

        int piece;
        if (kingX + 2 <= 7) {
            if (kingY + 1 <= 7) {
                piece = this.chessboard[kingX + 2][kingY + 1];
                if (Piece.getPieceColor(piece) != color && Piece.getPieceType(piece) == Piece.KNIGHT) {
                    return true;
                }
            }
            if (kingY - 1 >= 0) {
                piece = this.chessboard[kingX + 2][kingY - 1];
                if (Piece.getPieceColor(piece) != color && Piece.getPieceType(piece) == Piece.KNIGHT) {
                    return true;
                }
            }
        }
        if (kingX - 2 >= 0) {
            if (kingY + 1 <= 7) {
                piece = this.chessboard[kingX - 2][kingY + 1];
                if (Piece.getPieceColor(piece) != color && Piece.getPieceType(piece) == Piece.KNIGHT) {
                    return true;
                }
            }
            if (kingY - 1 >= 0) {
                piece = this.chessboard[kingX - 2][kingY - 1];
                if (Piece.getPieceColor(piece) != color && Piece.getPieceType(piece) == Piece.KNIGHT) {
                    return true;
                }
            }
        }
        if (kingY + 2 <= 7) {
            if (kingX + 1 <= 7) {
                piece = this.chessboard[kingX + 1][kingY + 2];
                if (Piece.getPieceColor(piece) != color && Piece.getPieceType(piece) == Piece.KNIGHT) {
                    return true;
                }
            }
            if (kingX - 1 >= 0) {
                piece = this.chessboard[kingX - 1][kingY + 2];
                if (Piece.getPieceColor(piece) != color && Piece.getPieceType(piece) == Piece.KNIGHT) {
                    return true;
                }
            }
        }
        if (kingY - 2 >= 0) {
            if (kingX + 1 <= 7) {
                piece = this.chessboard[kingX + 1][kingY - 2];
                if (Piece.getPieceColor(piece) != color && Piece.getPieceType(piece) == Piece.KNIGHT) {
                    return true;
                }
            }
            if (kingX - 1 >= 0) {
                piece = this.chessboard[kingX - 1][kingY - 2];
                if (Piece.getPieceColor(piece) != color && Piece.getPieceType(piece) == Piece.KNIGHT) {
                    return true;
                }
            }
        }

        if (color == Color.WHITE) {
            if (kingY + 1 <= 6) {
                if (kingX + 1 <= 7) {
                    piece = this.chessboard[kingX + 1][kingY + 1];
                    if (Piece.getPieceType(piece) == Piece.PAWN && Piece.getPieceColor(piece) != color) {
                        return true;
                    }
                }
                if (kingX - 1 >= 0) {
                    piece = this.chessboard[kingX - 1][kingY + 1];
                    if (Piece.getPieceType(piece) == Piece.PAWN && Piece.getPieceColor(piece) != color) {
                        return true;
                    }
                }
            }
        } else {
            if (kingY - 1 >= 1) {
                if (kingX + 1 <= 7) {
                    piece = this.chessboard[kingX + 1][kingY - 1];
                    if (Piece.getPieceType(piece) == Piece.PAWN && Piece.getPieceColor(piece) != color) {
                        return true;
                    }
                }
                if (kingX - 1 >= 0) {
                    piece = this.chessboard[kingX - 1][kingY - 1];
                    if (Piece.getPieceType(piece) == Piece.PAWN && Piece.getPieceColor(piece) != color) {
                        return true;
                    }
                }
            }
        }

        for (int i = -1; i < 2; i++) {
            if (kingX + i >= 0) {
                if (kingY + 1 <= 7) {
                    piece = this.chessboard[kingX + i][kingY + 1];
                    if (Piece.getPieceColor(piece) != color && Piece.getPieceType(piece) == Piece.KING) {
                        return true;
                    }
                }
                if (kingY - 1 >= 0) {
                    piece = this.chessboard[kingX + i][kingY - 1];
                    if (Piece.getPieceColor(piece) != color && Piece.getPieceType(piece) == Piece.KING) {
                        return true;
                    }
                }
            }
            if (kingX + i <= 7) {
                if (kingY + 1 <= 7) {
                    piece = this.chessboard[kingX + i][kingY + 1];
                    if (Piece.getPieceColor(piece) != color && Piece.getPieceType(piece) == Piece.KING) {
                        return true;
                    }
                }
                if (kingY - 1 >= 0) {
                    piece = this.chessboard[kingX + i][kingY - 1];
                    if (Piece.getPieceColor(piece) != color && Piece.getPieceType(piece) == Piece.KING) {
                        return true;
                    }
                }
            }
        }
        if (kingX - 1 >= 0) {
            piece = this.chessboard[kingX - 1][kingY];
            return Piece.getPieceColor(piece) != color && Piece.getPieceType(piece) == Piece.KING;
        }
        if (kingX + 1 < chessboard.length) {
            piece = this.chessboard[kingX + 1][kingY];
            return Piece.getPieceColor(piece) != color && Piece.getPieceType(piece) == Piece.KING;
        }
        return false;
    }

    public synchronized boolean isMate(int color) {
        return false;
    }

    public void undoLastMove() {
        if (getLastMove() != Move.BAD_MOVE) {

        }
    }

    public void loadGameFromFen(String fen) {
        String[] options = fen.split(" ");
        String[] lines = options[0].split("/");

        for (int i = 0; i < 8; i++) {
            int counter = 0;
            for (int j = 0; j < lines[i].length(); j++) {
                if (Character.isDigit(lines[i].charAt(j))) {
                    for (int k = 0; k < Character.getNumericValue(lines[i].charAt(j)); k++) {
                        this.chessboard[counter][7 - i] = 0;
                        ++counter;
                    }
                } else {
                    switch (lines[i].charAt(j)) {
                        case 'p' -> chessboard[counter][7 - i] = Color.BLACK ^ Piece.PAWN;
                        case 'r' -> chessboard[counter][7 - i] = Color.BLACK ^ Piece.ROOK;
                        case 'b' -> chessboard[counter][7 - i] = Color.BLACK ^ Piece.BISHOP;
                        case 'n' -> chessboard[counter][7 - i] = Color.BLACK ^ Piece.KNIGHT;
                        case 'q' -> chessboard[counter][7 - i] = Color.BLACK ^ Piece.QUEEN;
                        case 'k' -> chessboard[counter][7 - i] = Color.BLACK ^ Piece.KING;
                        case 'P' -> chessboard[counter][7 - i] = Color.WHITE ^ Piece.PAWN;
                        case 'R' -> chessboard[counter][7 - i] = Color.WHITE ^ Piece.ROOK;
                        case 'B' -> chessboard[counter][7 - i] = Color.WHITE ^ Piece.BISHOP;
                        case 'N' -> chessboard[counter][7 - i] = Color.WHITE ^ Piece.KNIGHT;
                        case 'Q' -> chessboard[counter][7 - i] = Color.WHITE ^ Piece.QUEEN;
                        case 'K' -> chessboard[counter][7 - i] = Color.WHITE ^ Piece.KING;
                    }
                    ++counter;
                }
            }
        }
        if (options[1].equals("w")) {
            turn = Color.WHITE;
        } else {
            turn = Color.BLACK;
        }
    }

    private boolean tryMove(int fromX, int fromY, int toX, int toY) {
        int pieceCopy = this.chessboard[toX][toY];
        this.chessboard[toX][toY] = this.chessboard[fromX][fromY];
        this.chessboard[fromX][fromY] = 0;
        if (isCheck(getPieceColor(toX, toY))) {
            this.chessboard[fromX][fromY] = this.chessboard[toX][toY];
            this.chessboard[toX][toY] = pieceCopy;
            return false;
        } else {
            Move move = new Move(fromX, fromY, toX, toY, this.chessboard[toX][toY], pieceCopy);
            movesHistory.add(move);
            turn = Color.WHITE == turn ? Color.BLACK : Color.WHITE;
            return true;
        }
    }

    private boolean tryBishopMove(int fromX, int fromY, int toX, int toY) {
        int i = 1;
        int color = getPieceColor(fromX, fromY);
        if (fromX > toX) {
            if (fromX - toX == fromY - toY) {
                while (i <= 7 && fromX - i != toX && fromY - i != toY && fromX - i >= 0 && fromY - i >= 0 && this.chessboard[fromX - i][fromY - i] == 0) {   // move left down
                    ++i;
                }
                if (fromX - i == toX && fromY - i == toY && (this.chessboard[toX][toY] == 0 || getPieceColor(toX, toY) != color)) {
                    return tryMove(fromX, fromY, toX, toY);
                }
            } else if (fromX - toX == toY - fromY) {
                while (i <= 7 && fromX - i != toX && fromY + i != toY && fromX - i >= 0 && fromY + i <= 7 && this.chessboard[fromX - i][fromY + i] == 0) {   // move left up
                    ++i;
                }
                if (fromX - i == toX && fromY + i == toY && (this.chessboard[toX][toY] == 0 || getPieceColor(toX, toY) != color)) {
                    return tryMove(fromX, fromY, toX, toY);
                }
            }
        } else if (fromX < toX) {
            if (toX - fromX == toY - fromY) {
                while (i <= 7 && fromX + i != toX && fromY + i != toY && fromX + i <= 7 && fromY + i <= 7 && this.chessboard[fromX + i][fromY + i] == 0) {   // move right up
                    ++i;
                }
                if (fromX + i == toX && fromY + i == toY && (this.chessboard[toX][toY] == 0 || getPieceColor(toX, toY) != color)) {
                    return tryMove(fromX, fromY, toX, toY);
                }
            } else if (toX - fromX == fromY - toY) {
                while (i <= 7 && fromX + i != toX && fromY - i != toY && fromX + i <= 7 && fromY - i >= 0 && this.chessboard[fromX + i][fromY - i] == 0) {   // move right down
                    ++i;
                }
                if (fromX + i == toX && fromY - i == toY && (this.chessboard[toX][toY] == 0 || getPieceColor(toX, toY) != color)) {
                    return tryMove(fromX, fromY, toX, toY);
                }
            }
        } else {
            return false;
        }
        return false;
    }

    private boolean tryRookMove(int fromX, int fromY, int toX, int toY) {
        int i = 1;
        int color = getPieceColor(fromX, fromY);
        if (fromX == toX) {
            if (fromY > toY) {  // move down
                while (i <= 7 && fromY - i != toY && this.chessboard[fromX][fromY - i] == 0) {
                    ++i;
                }
                if (fromY - i == toY && (this.chessboard[toX][toY] == 0 || getPieceColor(toX, toY) != color)) {
                    return tryMove(fromX, fromY, toX, toY);
                }
            } else {            // move up
                while (i <= 7 && fromY + i != toY && this.chessboard[fromX][fromY + i] == 0) {
                    ++i;
                }
                if (fromY + i == toY && (this.chessboard[toX][toY] == 0 || getPieceColor(toX, toY) != color)) {
                    return tryMove(fromX, fromY, toX, toY);
                }
            }

        } else if (fromY == toY) {
            if (fromX > toX) {  // move left
                while (i <= 7 && fromX - i != toX && this.chessboard[fromX - 1][fromY] == 0) {
                    ++i;
                }
                if (fromX - i == toX && (this.chessboard[toX][toY] == 0 || getPieceColor(toX, toY) != color)) {
                    return tryMove(fromX, fromY, toX, toY);
                }
            } else {            // move right
                while (i <= 7 && fromX + i != toX && this.chessboard[fromX + 1][fromY] == 0) {
                    ++i;
                }
                if (fromX + i == toX && (this.chessboard[toX][toY] == 0 || getPieceColor(toX, toY) != color)) {
                    return tryMove(fromX, fromY, toX, toY);
                }
            }

        } else {
            return false;
        }
        return false;
    }

    private boolean isKingCastleAvailable(int fromX, int fromY, int toX, int toY) {
        if (Piece.getPieceColor(this.chessboard[fromX][fromY]) == Color.WHITE) {    // white king
            if (fromX < toX && isWhiteKingKingSideCastleAvailable) {
                if (this.chessboard[fromX + 1][fromY] == 0 && this.chessboard[fromX + 2][fromY] == 0) {
                    if (tryMove(fromX, fromY, fromX + 1, toY)) {
                        if (tryMove(fromX + 1, fromY, fromX + 2, toY)) {
                            this.chessboard[5][0] = this.chessboard[7][0];
                            this.chessboard[7][0] = 0;
                            isWhiteKingKingSideCastleAvailable = false;
                            isWhiteKingQueenSideCastleAvailable = false;
                            return true;
                        }
                        this.chessboard[fromX][fromY] = this.chessboard[fromX + 1][fromY];
                        this.chessboard[fromX + 1][fromY] = 0;
                    }
                }
            } else if (fromX > toX && isWhiteKingQueenSideCastleAvailable) {
                if (this.chessboard[fromX - 1][fromY] == 0 && this.chessboard[fromX - 2][fromY] == 0) {
                    if (tryMove(fromX, fromY, fromX - 1, toY)) {
                        if (tryMove(fromX - 1, fromY, fromX - 2, toY)) {
                            this.chessboard[2][0] = this.chessboard[0][0];
                            this.chessboard[0][0] = 0;
                            isWhiteKingKingSideCastleAvailable = false;
                            isWhiteKingQueenSideCastleAvailable = false;
                            return true;
                        }
                        this.chessboard[fromX][fromY] = this.chessboard[fromX - 1][fromY];
                        this.chessboard[fromX - 1][fromY] = 0;
                    }
                }
            }
        } else {                                                                    // black king
            if (fromX < toX && isBlackKingKingSideCastleAvailable) {
                if (this.chessboard[fromX + 1][fromY] == 0 && this.chessboard[fromX + 2][fromY] == 0) {
                    if (tryMove(fromX, fromY, fromX + 1, toY)) {
                        if (tryMove(fromX + 1, fromY, fromX + 2, toY)) {
                            this.chessboard[5][7] = this.chessboard[7][7];
                            this.chessboard[7][7] = 0;
                            isBlackKingQueenSideCastleAvailable = false;
                            isBlackKingKingSideCastleAvailable = false;
                            return true;
                        }
                        this.chessboard[fromX][fromY] = this.chessboard[fromX + 1][fromY];
                        this.chessboard[fromX + 1][fromY] = 0;
                    }
                }
            } else if (fromX > toX && isBlackKingQueenSideCastleAvailable) {
                if (this.chessboard[fromX - 1][fromY] == 0 && this.chessboard[fromX - 2][fromY] == 0) {
                    if (tryMove(fromX, fromY, fromX - 1, toY)) {
                        if (tryMove(fromX - 1, fromY, fromX - 2, toY)) {
                            this.chessboard[2][7] = this.chessboard[0][7];
                            this.chessboard[0][7] = 0;
                            isBlackKingQueenSideCastleAvailable = false;
                            isBlackKingKingSideCastleAvailable = false;
                            return true;
                        }
                        this.chessboard[fromX][fromY] = this.chessboard[fromX - 1][fromY];
                        this.chessboard[fromX - 1][fromY] = 0;
                    }
                }
            }
        }
        return false;
    }

    public synchronized boolean move(int fromX, int fromY, int toX, int toY, int promotion) {
        if (fromX >= 0 && fromX <= 7 && fromY >= 0 && fromY <= 7
                && toX >= 0 && toX <= 7 && toY >= 0 && toY <= 7) {

            if (getPieceColor(fromX, fromY) == turn) {

                int piece = this.chessboard[fromX][fromY];
                int color = getPieceColor(fromX, fromY);
                switch (piece & PIECE_MASK) {
                    case Piece.PAWN:
                        if (color == Color.WHITE) {     // WHITE PAWN
                            if (fromY + 2 == toY && fromY == 1 && this.chessboard[fromX][fromY + 1] == 0 && this.chessboard[fromX][fromY + 2] == 0) {       // move 2 squares
                                return tryMove(fromX, fromY, toX, toY);
                            } else if (fromY + 1 == toY && fromX + 1 == toX
                                    && this.chessboard[fromX + 1][fromY + 1] == 0) {        // en passant on right
                                return tryMove(fromX, fromY, toX, toY);
                            } else if (fromY + 1 == toY && fromX - 1 == toX
                                    && this.chessboard[fromX - 1][fromY + 1] == 0) {        // en passant on left
                                if (canEnPassant(fromX, fromY)) {
                                    return tryMove(fromX, fromY, toX, toY);
                                } else {    // wrong move
                                    return false;
                                }
                            } else if (fromY + 1 == toY) {
                                boolean isMoveValid;
                                if (fromX == toX && this.chessboard[fromX][fromY + 1] == 0) {         // move 1 square
                                    isMoveValid = tryMove(fromX, fromY, toX, toY);
                                } else if (fromX + 1 == toX && getPieceColor(toX, toY) == Color.BLACK) {    // capture on right
                                    isMoveValid = tryMove(fromX, fromY, toX, toY);
                                } else if (fromX - 1 == toX && getPieceColor(toX, toY) == Color.BLACK) {    // capture on left
                                    isMoveValid = tryMove(fromX, fromY, toX, toY);
                                } else {    // wrong move
                                    return false;
                                }
                                if (isMoveValid) {
                                    if (toX == 7) {
                                        this.chessboard[toX][toY] = promotion ^ color;
                                    }
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        } else {    // BLACK PAWN
                            if (fromY - 1 == 0 && toY == 0 && this.chessboard[fromX][0] == 0) {
                                if (tryMove(fromX, fromY, toX, toY)) {
                                    this.chessboard[fromX][0] = promotion ^ color;
                                } else {
                                    return false;
                                }
                            } else if (fromY - 2 == toY && fromY == 6 && this.chessboard[fromX][fromY - 1] == 0 && this.chessboard[fromX][fromY - 2] == 0) {       // move 2 squares
                                return tryMove(fromX, fromY, toX, toY);
                            } else if (fromY - 1 == toY && fromX + 1 == toX
                                    && this.chessboard[fromX + 1][fromY - 1] == 0) {    // en passant on right
                                if (canEnPassant(fromX, fromY)) {
                                    return tryMove(fromX, fromY, toX, toY);
                                } else {    // wrong move
                                    return false;
                                }
                            } else if (fromY - 1 == toY && fromX - 1 == toX
                                    && this.chessboard[fromX - 1][fromY - 1] == 0) {    // en passant on left
                                if (canEnPassant(fromX, fromY)) {
                                    return tryMove(fromX, fromY, toX, toY);
                                } else {    // wrong move
                                    return false;
                                }
                            } else if (fromY - 1 == toY) {
                                boolean isMoveValid;
                                if (fromX == toX && this.chessboard[fromX][fromY - 1] == 0) {     // move 1 square
                                    isMoveValid = tryMove(fromX, fromY, toX, toY);
                                } else if (fromX + 1 == toX && getPieceColor(toX, toY) == Color.WHITE) {    // capture on rigth
                                    isMoveValid = tryMove(fromX, fromY, toX, toY);
                                } else if (fromX - 1 == toX && getPieceColor(toX, toY) == Color.WHITE) {    // capture on left
                                    isMoveValid = tryMove(fromX, fromY, toX, toY);
                                } else {    // wrong move
                                    return false;
                                }
                                if (isMoveValid) {
                                    if (toY == 0) {
                                        this.chessboard[toX][toY] = promotion ^ color;
                                    }
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        }
                    case Piece.BISHOP:
                        return tryBishopMove(fromX, fromY, toX, toY);
                    case Piece.KNIGHT:
                        if (fromX + 2 == toX) {
                            if (fromY + 1 == toY) {
                                return tryMove(fromX, fromY, toX, toY);
                            } else if (fromY - 1 == toY) {
                                return tryMove(fromX, fromY, toX, toY);
                            } else {
                                return false;
                            }
                        } else if (fromX - 2 == toX) {
                            if (fromY + 1 == toY) {
                                return tryMove(fromX, fromY, toX, toY);
                            } else if (fromY - 1 == toY) {
                                return tryMove(fromX, fromY, toX, toY);
                            } else {
                                return false;
                            }
                        } else if (fromY + 2 == toY) {
                            if (fromX + 1 == toX) {
                                return tryMove(fromX, fromY, toX, toY);
                            } else if (fromX - 1 == toX) {
                                return tryMove(fromX, fromY, toX, toY);
                            } else {
                                return false;
                            }
                        } else if (fromY - 2 == toY) {
                            if (fromX + 1 == toX) {
                                return tryMove(fromX, fromY, toX, toY);
                            } else if (fromX - 1 == toX) {
                                return tryMove(fromX, fromY, toX, toY);
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    case Piece.ROOK:
                        boolean canMove = tryMove(fromX, fromY, toX, toY);
                        if (canMove) {
                            if (Piece.getPieceColor(this.chessboard[toX][toY]) == Color.WHITE) {
                                if (fromX == 0) {
                                    isWhiteKingQueenSideCastleAvailable = false;
                                } else if (fromX == 7) {
                                    isWhiteKingKingSideCastleAvailable = false;
                                }
                            } else {
                                if (fromX == 0) {
                                    isBlackKingQueenSideCastleAvailable = false;
                                } else if (fromX == 7) {
                                    isBlackKingKingSideCastleAvailable = false;
                                }
                            }
                        }
                        return canMove;
                    case Piece.QUEEN:
                        return tryBishopMove(fromX, fromY, toX, toY) || tryRookMove(fromX, fromY, toX, toY);
                    case Piece.KING:
                        int x = Math.abs(toX - fromX);
                        int y = Math.abs(toY - fromY);
                        if (fromY == toY && Math.abs(fromX - toX) == 2) {
                            return isKingCastleAvailable(fromX, fromY, toX, toY);
                        } else if (x < 2 && y < 2) {
                            boolean b = tryMove(fromX, fromY, toX, toY);
                            if (b) {
                                if (color == Color.WHITE) {
                                    this.whiteKingX = toX;
                                    this.whiteKingY = toY;
                                    isWhiteKingQueenSideCastleAvailable = false;
                                    isWhiteKingKingSideCastleAvailable = false;
                                } else {
                                    this.blackKingX = toX;
                                    this.blackKingY = toY;
                                    isBlackKingKingSideCastleAvailable = false;
                                    isBlackKingQueenSideCastleAvailable = false;
                                }
                                return true;
                            } else {
                                return false;
                            }
                        }
                    default:
                        return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
