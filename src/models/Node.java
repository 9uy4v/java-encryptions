package models;

public class Node {
    private String name; // TODO : Text in Node - for debug purposes

    public Node() {
        name = "";
    }

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
