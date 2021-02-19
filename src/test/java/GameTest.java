import chessLibOptimized.Color;
import chessLibOptimized.Game;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class GameTest {
    public Game gameSession;
    public String fen;
    public boolean expected;
    public int fromX;
    public int fromY;
    public int toX;
    public int toY;
    public String fenAfterMove;
    public boolean isMate;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // fen before move, isCheck, fromX, fromY, toX, toY, fen after move, isMate
                {"rnb1kb1r/pp1q1ppp/4n3/1Q1pp1p1/8/8/PPPPPPPP/RNB1KBNR w KQkq - 0 1", false, 1, 4, 3, 6, "rnb1kb1r/pp1Q1ppp/4n3/3pp1p1/8/8/PPPPPPPP/RNB1KBNR w KQkq - 0 1", false},
                {"rnbqkbnr/p1pppppp/1p6/8/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 1", false, 3, 0, 3, 2, "rnbqkbnr/p1pppppp/1p6/8/3P4/3Q4/PPP1PPPP/RNB1KBNR w KQkq - 0 1", false},
                {"rnb1kb1r/pp1q1ppp/4n3/3pp1p1/5Q2/8/PPPPPPPP/RNB1KBNR w KQkq - 0 1", false, 5, 3, 6, 3, "rnb1kb1r/pp1q1ppp/4n3/3pp1p1/6Q1/8/PPPPPPPP/RNB1KBNR w KQkq - 0 1", false},
                {"rnb1kb1r/pp1q1ppp/4n3/3pp1p1/4Q3/8/PPPPPPPP/RNB1KBNR w KQkq - 0 1", false, 4, 3, 3, 2, "rnb1kb1r/pp1q1ppp/4n3/3pp1p1/8/3Q4/PPPPPPPP/RNB1KBNR w KQkq - 0 1", false},
                {"rnb1kb1r/pp1q1ppp/4n3/3pp1p1/B3Q3/8/PPPPPPPP/RN2KBNR w KQkq - 0 1", false, 0, 3, 3, 6, "rnb1kb1r/pp1B1ppp/4n3/3pp1p1/4Q3/8/PPPPPPPP/RN2KBNR w KQkq - 0 1", false},
                {"1nbqkb1r/p1pppp1p/1p6/2n2Np1/4Pr2/2P5/PP1P1PPP/RNBQKB1R w KQk - 0 1", false, 1, 0, 0, 2, "1nbqkb1r/p1pppp1p/1p6/2n2Np1/4Pr2/N1P5/PP1P1PPP/R1BQKB1R w KQk - 0 1", false},
                {"1nbqkb1r/p1pRpp1p/1p6/2n2Np1/4Pr2/2P5/PP1P1PPP/1NBQKB1R w Kk - 0 1", false, 5, 4, 6, 6, "1nbqkb1r/p1pRppNp/1p6/2n3p1/4Pr2/2P5/PP1P1PPP/1NBQKB1R w Kk - 0 1", false},
                {"rnbqkbnr/1pp2ppp/7p/p5p1/8/2P1Q3/PPP2PPP/RNB1KBNR w KQkq - 0 1", true, 4, 0, 4, 1, "rnbqkbnr/1pp2ppp/7p/p5p1/8/2P1Q3/PPP1KPPP/RNB2BNR w kq - 0 1", false},
                {"rnbqkbnr/1pp2ppp/7p/p5p1/B7/2P1Q3/PPP2PPP/RN2KBNR w KQkq - 0 1", true, 2, 2, 2, 3, "rnbqkbnr/1pp2ppp/7p/p5p1/B1P5/4Q3/PPP2PPP/RN2KBNR w KQkq - 0 1", true},
                {"rnbqkbnr/1pp2ppp/5N1p/p5p1/8/2P5/PPPQ1PPP/RNB1KB1R w KQkq - 0 1", true, 5, 1, 5, 3, "rnbqkbnr/1pp2ppp/5N1p/p5p1/5P2/2P5/PPPQ2PP/RNB1KB1R w KQkq - 0 1", false},
                {"rnbqkbnr/1pN2ppp/7p/p5p1/8/2P5/PPPQ1PPP/RNB1KB1R w KQkq - 0 1", true, 3, 1, 3, 7, "rnbQkbnr/1pN2ppp/7p/p5p1/8/2P5/PPP2PPP/RNB1KB1R w KQkq - 0 1", false},
                {"rnbqkbnr/1p3ppp/7p/p5p1/8/N1P1R3/PPPQ1PPP/RNB1KB2 w Qkq - 0 1", true, 5, 0, 3, 2, "rnbqkbnr/1p3ppp/7p/p5p1/8/N1PBR3/PPPQ1PPP/RNB1K3 w Qkq - 0 1", false},
                {"rnbqkbnr/1p4pp/6Qp/p5p1/3p4/N1P4R/PPP2PPP/RNB1KB2 w Qkq - 0 1", true, 2, 2, 3, 3, "rnbqkbnr/1p4pp/6Qp/p5p1/3P4/N6R/PPP2PPP/RNB1KB2 w Qkq - 0 1", false},
                {"rnbqkbnr/1p4pp/7p/p2p1Qp1/8/N1P4R/PPP2PPP/RNB1KB2 w Qkq - 0 1", false, 1, 0, 3, 1, "rnbqkbnr/1p4pp/7p/p2p1Qp1/8/N1P4R/PPPN1PPP/R1B1KB2 w Qkq - 0 1", false},
                {"rnbqkbnr/1p4pp/7p/p2QN1p1/8/2P4R/PPP2PPP/RNB1KB2 w Qkq - 0 1", false, 7, 2, 4, 2, "rnbqkbnr/1p4pp/7p/p2QN1p1/8/2P1R3/PPP2PPP/RNB1KB2 w Qkq - 0 1", false},
                {"r1bNkbnr/1p4pp/7p/p2Q2p1/8/2P4R/PPP2PPP/RNB1KB2 w Qkq - 0 1", false, 2, 0, 4, 2, "r1bNkbnr/1p4pp/7p/p2Q2p1/8/2P1B2R/PPP2PPP/RN2KB2 w Qkq - 0 1", false},
                {"r1bN1bnr/1p4pp/7p/p2Q2p1/k7/2P4R/PPP2PPP/RNB1KB2 w Q - 0 1", false, 3, 4, 1, 2, "r1bN1bnr/1p4pp/7p/p5p1/k7/1QP4R/PPP2PPP/RNB1KB2 w Q - 0 1", true},
                {"rk1N1bnr/1bp3pp/7p/p2Q2p1/8/2P4R/PPP2PPP/RNB1KB2 w Qkq - 0 1", false, 3, 7, 2, 5, "rk3bnr/1bp3pp/2N4p/p2Q2p1/8/2P4R/PPP2PPP/RNB1KB2 w Qkq - 0 1", false},
                {"r2N1bnk/1bp3pp/7p/p2Q2p1/8/2P4R/PPP2PPP/RNB1KB2 w Qq - 0 1", false, 3, 4, 6, 7, "r2N1bQk/1bp3pp/7p/p5p1/8/2P4R/PPP2PPP/RNB1KB2 w Qq - 0 1", false},
                {"r5nk/1bpp2Qp/4b2p/p4Np1/8/2P4R/PPP2PPP/RNB1KB2 w Qq - 0 1", true, 1, 1, 1, 3, "r5nk/1bpp2Qp/4b2p/p4Np1/1P6/2P4R/P1P2PPP/RNB1KB2 w Qq - 0 1", true},
                {"r5nk/1bppp2Q/4bN1p/p5p1/8/2P4R/PPP2PPP/RNB1KB2 w Qq - 0 1", true, 7, 2, 6, 2, "r5nk/1bppp2Q/4bN1p/p5p1/8/2P3R1/PPP2PPP/RNB1KB2 w Qq - 0 1", true},
                {"rnbqkbnr/1p1p1ppp/2p1p3/p7/8/4PN2/PPPPBPPP/RNBQK2R w KQkq - 0 1", false, 4, 0, 6, 0, "rnbqkbnr/1p1p1ppp/2p1p3/p7/8/4PN2/PPPPBPPP/RNBQ1RK1 w Qkq - 0 1", false},
                {"rnbqkb1r/p1pp1ppp/1p3n2/3QP3/8/8/PPP1PPPP/RNB1KBNR w KQkq - 0 1", false, 3, 4, 5, 6, "rnbqkb1r/p1pp1Qpp/1p3n2/4P3/8/8/PPP1PPPP/RNB1KBNR w KQkq - 0 1", false},
                {"rnbqkbnr/2pppppp/8/7Q/2B5/8/PPPPPPPP/RNB1K1NR w KQkq - 0 1", false, 7, 4, 5, 6, "rnbqkbnr/2pppQpp/8/8/2B5/8/PPPPPPPP/RNB1K1NR w KQkq - 0 1", true},
                {"rnbqkbnr/pppppppp/8/8/8/4P3/PPPP1PPP/RNBQKBNR b KQkq - 0 1", false, 0, 6, 0, 5, "rnbqkbnr/1ppppppp/p7/8/8/4P3/PPPP1PPP/RNBQKBNR w KQkq - 0 1", false},
                {"rnbqkbnr/p2pp2P/8/1pp5/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1", false, 7, 6, 6, 7, "rnbqkbQr/p2pp3/8/1pp5/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1", false},
                {"rn1qkbnr/pppppppp/8/8/6b1/8/PPP1B3/RNBK4 w Qkq - 0 1", false, 4, 1, 6, 3, "rn1qkbnr/pppppppp/8/8/6B1/8/PPP5/RNBK4 w Qkq - 0 1", false}
                // king castle test
                // {"rnbqkbnr/pp2p1pp/3p1p2/2p5/2B5/4PN2/PPPP1PPP/RNBQK2R w KQkq - 0 1", false, 4, 0, 6, 0, "rnbqkbnr/pp2p1pp/3p1p2/2p5/2B5/4PN2/PPPP1PPP/RNBQ1RK1 w Qkq - 0 1", false}
        });
    }

    public GameTest(String fen, boolean expected, int fromX, int fromY, int toX, int toY, String fenAfterMove, boolean isMate) {
        this.gameSession = new Game();
        this.gameSession.loadGameFromFen(fen);
        this.fen = fen;
        this.expected = expected;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.fenAfterMove = fenAfterMove;
        this.isMate = isMate;
    }

    //@Test
    public void isCheckTest() {
        Game game = new Game();
        game.loadGameFromFen(fen);
        assertTrue(game.isCheck(Color.BLACK));
    }

    @Test
    public void moveTest() {
        Game game = new Game();
        game.loadGameFromFen(fen);
        assertTrue(game.move(fromX, fromY, toX, toY, 0));
    }
}
