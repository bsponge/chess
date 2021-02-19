package chessLibOptimized;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
@Data
public class Move {

    @Transient
    public static final Move BAD_MOVE = new Move(-1, -1, -1, -1, -1, -1);


    private int fromX;
    private int fromY;
    private int fromPiece;
    private int toPiece;
    private int toX;
    private int toY;

    public Move(int fromX, int fromY, int toX, int toY, int fromPiece, int toPiece) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.fromPiece = fromPiece;
        this.toPiece = toPiece;
    }

    public Move() {

    }
}
