package models;

public class DijakstraNode extends Node {
    private Node prevNode; // Reference to previous Node
    private Integer minDistance; // Current min distance from source

    public DijakstraNode() {
        prevNode = null;
        minDistance = Integer.MAX_VALUE;
    }

    public void updateIfShorter(Node prevNode, Integer minDistance) {
        if (this.minDistance > minDistance) {
            // Update Path
            this.prevNode = prevNode;
            this.minDistance = minDistance;
        }
    }
}
