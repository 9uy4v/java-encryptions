package models;

import java.util.List;

public class Vertex<T> {
    private T value;

    private Vertex<T> prevVertex; // Reference to previous Vertex
    private Integer minDistance; // Current min distance from source

    private List<Vertex<T>> incomingVertices; // List of all incoming vertices to this vertex
    private List<Vertex<T>> outgoingVertices; // List of all outgoing vertices to this vertex

    // Constructors
    public Vertex() {
        prevVertex = null;
        minDistance = Integer.MAX_VALUE;
    }

    public Vertex(T value) {
        this();

        this.value = value;
    }

    // Vertex variables
    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    // Dijkstra's variables updates
    public Vertex<T> getPrev() {
        return prevVertex;
    }

    public void setPrev(Vertex<T> prevVertex) {
        this.prevVertex = prevVertex;
    }

    public int getMinDist() {
        return minDistance;
    }

    public void setMinDist(int minDistance) {
        this.minDistance = minDistance;
    }

    public void addIncoming(Vertex<T> v) {
        incomingVertices.add(v);
    }

    public void addOutgoing(Vertex<T> v) {
        outgoingVertices.add(v);
    }
}
