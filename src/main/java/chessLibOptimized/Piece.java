package chessLibOptimized;

public class Piece {
    private static final int PIECE_MASK = 0B111111;
    private static final int COLOR_MASK = 0B11000000;

    public static final int PAWN = 1;
    public static final int BISHOP = 2;
    public static final int KNIGHT = 4;
    public static final int ROOK = 8;
    public static final int QUEEN = 16;
    public static final int KING = 32;

    public static int getPieceType(int piece) {
        return piece & PIECE_MASK;
    }

    public static int getPieceColor(int piece) {
        return piece & COLOR_MASK;
    }
}
