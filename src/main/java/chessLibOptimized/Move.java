package chessLibOptimized;

import lombok.Data;

@Data
public class Move {

    public static final Move BAD_MOVE = new Move(-1, -1, -1, -1, -1, -1);

    private final int fromX;
    private final int fromY;
    private final int fromPiece;
    private final int toPiece;
    private final int toX;
    private final int toY;

    public Move(int fromX, int fromY, int toX, int toY, int fromPiece, int toPiece) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.fromPiece = fromPiece;
        this.toPiece = toPiece;
    }
}
