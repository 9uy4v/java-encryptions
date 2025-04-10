package SIM;

import SIM.models.PlayerCode;
import SIM.models.SimGraph;

public class SimEnryption {

    public static void main(String[] args) {
        SimGraph game = new SimGraph(6);
        PlayerCode result = PlayerCode.None;

        while (result == PlayerCode.None) {
            result = takeTurn(game, PlayerCode.PlayerOne);

            if (result != PlayerCode.None)
                break;

            result = takeTurn(game, PlayerCode.PlayerTwo);
        }

        System.out.println(result + " WON!!!");
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

        int degreeA = 0, degreeB = 0;
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
