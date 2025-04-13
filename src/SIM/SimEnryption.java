package SIM;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import SIM.models.PlayerCode;
import SIM.models.SimGraph;

public class SimEnryption {
    static int SIZE = 6;

    public static void main(String[] args) {
        SimGraph game = generateGameByFile(new File("assets\\test2.pdf").toPath());
        PlayerCode result = PlayerCode.None;

        System.out.println("============Game Start=============");

        while (result == PlayerCode.None) {
            result = takeTurn(game, PlayerCode.PlayerOne);

            if (result != PlayerCode.None)
                break;

            result = takeTurn(game, PlayerCode.PlayerTwo);
        }

        System.out.println(result + " WON!!!");
    }

    private static SimGraph generateGameByFile(Path path) {
        byte[] movesBuffer = new byte[SIZE * SIZE];
        SimGraph game = new SimGraph(SIZE);

        try (InputStream in = Files.newInputStream(path)) {
            in.read(movesBuffer);
        } catch (IOException e) {
            System.out.println("Couldn't read the file : " + e);
            System.out.println("Returning empty game board");
            return game;
        }

        for (int i = 0; i < movesBuffer.length; i++) {
            byte curMove = movesBuffer[i];

            int a = (curMove >> 5) & 0b111; // 3 leftmost bits
            int b = curMove & 0b111; // 3 rightmost bits
            int playerBits = (curMove >> 3) & 0b11; // 2 middle bits

            PlayerCode player = switch (playerBits) {
                case 0b10 -> PlayerCode.PlayerOne;
                case 0b11 -> PlayerCode.PlayerTwo;
                default -> null;
            };

            if (player == null || a >= SIZE || b >= SIZE || a == b)
                continue;

            PlayerCode[][] board = game.getBoard();

            if (board[a][b] == PlayerCode.None && !wouldCreateTriangle(board, a, b, player)) {
                game.connect(a, b, player);
            }
        }

        return game;
    }

    private static PlayerCode takeTurn(SimGraph game, PlayerCode curPlayer) {
        int[] move = nextMove(game, curPlayer);
        return game.connect(move[0], move[1], curPlayer);
    }

    private static int[] nextMove(SimGraph game, PlayerCode curPlayer) {
        PlayerCode[][] board = game.getBoard();
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        for (int i = 0; i < game.getSize(); i++) {
            for (int j = i + 1; j < game.getSize(); j++) {
                if (board[i][j] != PlayerCode.None)
                    continue;

                int score = evaluateMove(board, i, j, curPlayer);

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = new int[] { i, j };
                }
            }
        }

        return bestMove;
    }

    private static int evaluateMove(PlayerCode[][] board, int a, int b, PlayerCode curPlayer) {
        int score = 1000;

        int degreeA = 0, degreeB = 0; // The amount of edges connected to a and b
        for (int i = 0; i < board.length; i++) {
            if (board[a][i] == curPlayer)
                degreeA++;
            if (board[b][i] == curPlayer)
                degreeB++;
        }

        score -= 100 * (degreeA >= 2 ? 1 : 0);
        score -= 100 * (degreeB >= 2 ? 1 : 0);
        score -= 20 * (degreeA + degreeB);

        if (wouldCreateTriangle(board, a, b, curPlayer)) {
            score -= 1000;
        }

        return score;
    }

    private static boolean wouldCreateTriangle(PlayerCode[][] board, int a, int b, PlayerCode color) {
        for (int i = 0; i < board.length; i++) {
            if (i == a || i == b)
                continue;
            if (board[a][i] == color && board[b][i] == color)
                return true;
        }
        return false;
    }
}
