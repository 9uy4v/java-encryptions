package models;

import java.util.ArrayList;
import java.util.List;

public class Graph<T> {
    private List<Vertex<T>> vertices;
    private List<Edge<T>> edges;

    public Graph() {
        vertices = new ArrayList<Vertex<T>>();
        edges = new ArrayList<Edge<T>>();
    }

    public List<Vertex<T>> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertex<T>> vertices) {
        this.vertices = vertices;
    }

    public List<Edge<T>> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge<T>> edges) {
        this.edges = edges;
    }

    // Searches for vertex in the existing vertices list by value
    // Returns the found vertex- if not found returns null
    private Vertex<T> findVertex(T value) {
        for (Vertex<T> vertex : vertices) {
            // Check if a vertex with this value already exists
            if (vertex.getValue().equals(value)) {
                return vertex;
            }
        }

        return null;
    }

    // Checks if an edge already exists by origin and destination vertices value
    // Returns true if edge with corresponding values and false if not
    private boolean edgeExists(T valueOrigin, T valueDest) {
        for (Edge<T> edge : edges) {
            // Check if there's an edge already connecting these values
            if (edge.getOrigin().getValue().equals(valueOrigin) && edge.getDest().getValue().equals(valueOrigin)) {
                return true;
            }
        }
        return false;
    }

    // Checks validity of data and existance of such edges and creates and edge if
    // all data is valid
    public void addEdge(T valueOrigin, T valueDest, int weight) {
        // Check for duplicate edge or invalid weight (under zero)
        if (edgeExists(valueOrigin, valueDest) || weight < 0)
            return;

        // Check if origin vertex already exists in the graph
        Vertex<T> origin = findVertex(valueOrigin);

        if (origin == null) {
            // if origin vertex doesn't exist- create one and add it to the list of vetices
            // in the graph
            origin = new Vertex<T>(valueOrigin);
            vertices.add(origin);
        }

        // Check if destination vertex already exists in the graph
        Vertex<T> dest = findVertex(valueDest);

        if (dest == null) {
            // if destination vertex doesn't exist- create one and add it to the list of
            // vetices in the graph
            dest = new Vertex<T>(valueDest);
            vertices.add(dest);
        }

        // Create a new edge
        Edge<T> newEdge = new Edge<T>(origin, dest, weight);

        origin.addOutgoing(dest); // add destination vertex to origin's outgoing connections list
        dest.addIncoming(origin); // add origin vertex to destinations's incoming connections list
        edges.add(newEdge); // add new edge to graph's list
    }

}
