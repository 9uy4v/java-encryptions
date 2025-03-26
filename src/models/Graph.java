package models;

import java.util.HashSet;

public class Graph {
    private HashSet<Node> nodes = new HashSet<Node>();

    public void addNode(Node newNode) {
        nodes.add(newNode);
    }
}
