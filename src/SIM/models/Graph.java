package SIM.models;

import java.util.Arrays;

public class Graph {
    PlayerCode[][] board;

    public Graph(int numOfVerticies) {
        board = new PlayerCode[numOfVerticies][numOfVerticies];

        Arrays.fill(board, PlayerCode.None);
    }

    public void connect(int source, int destination, PlayerCode playerCode) {
        if (board[source][destination] != PlayerCode.None || source == destination)
            return;

        board[source][destination] = playerCode;
        board[destination][source] = playerCode;
    }

    public PlayerCode[][] getBoard() {
        return board;
    }

}
