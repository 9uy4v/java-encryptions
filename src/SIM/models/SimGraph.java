package SIM.models;

import java.util.Arrays;

public class SimGraph {
    private PlayerCode[][] board; // A 2D-array representaion of the game graph
    private int numOfVertices;

    public SimGraph(int numOfVertices) {
        this.numOfVertices = numOfVertices;
        board = new PlayerCode[numOfVertices][numOfVertices];

        reset();
    }

    // If source != destination and connection has not been done- connect the two.
    // then check for a winning player and return his code
    public PlayerCode connect(int source, int destination, PlayerCode playerCode) {
        if (board[source][destination] != PlayerCode.None || source == destination)
            return null; // returns null if there's an error with current move

        board[source][destination] = playerCode;
        board[destination][source] = playerCode;

        System.out.println(source + " <-> " + destination + " : " + playerCode);

        // Check if move was game ending
        for (int i = 0; i < numOfVertices; i++) {
            if (source == i || destination == i)
                continue;

            if (board[source][i] == playerCode && board[destination][i] == playerCode) {
                // Returns the winner player's code in case the current one created a triangle
                return playerCode == PlayerCode.PlayerOne ? PlayerCode.PlayerTwo : PlayerCode.PlayerOne;
            }
        }

        // Returns Player Code none if no one won
        return PlayerCode.None;
    }

    public PlayerCode[][] getBoard() {
        return board;
    }

    public int getSize() {
        return numOfVertices;
    }

    public void reset() {
        for (int i = 0; i < numOfVertices; i++) {
            Arrays.fill(board[i], PlayerCode.None);
        }
    }
}
