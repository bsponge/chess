package chessLibOptimized;

import java.util.Scanner;

public class Main {
    public static void main(String... args) {
        Game game = new Game();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            game.print();
            System.out.print("fromX: ");
            int fromX = scanner.nextInt();
            System.out.print("fromY: ");
            int fromY = scanner.nextInt();
            System.out.print("toX: ");
            int toX = scanner.nextInt();
            System.out.print("toY: ");
            int toY = scanner.nextInt();
            System.out.println(game.move(fromX, fromY, toX, toY, 0));
        }
    }
}
