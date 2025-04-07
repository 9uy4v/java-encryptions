package dijkstra.models;

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

    // Searches for edge in the existing edges list by origin and destination value
    // Returns the found vertex- if not found returns null
    public Edge<T> findEdge(T valueOrigin, T valueDest) {
        // Check if there's an edge already connecting these values
        for (Edge<T> edge : edges) {
            if (edge.getOrigin().getValue().equals(valueOrigin) && edge.getDest().getValue().equals(valueDest)) {
                return edge;
            }
        }
        return null;
    }

    // Adds vertex to the graph if it doesn't exist yet
    public Vertex<T> addVertex(T value) {
        // Check if vertex already exists in the graph
        Vertex<T> v = findVertex(value);

        if (v == null) {
            // if vertex doesn't exist- create one and add it to the list of vetices
            // in the graph
            v = new Vertex<T>(value);
            vertices.add(v);
        }
        // Returns the new vertex or exisiting vertex
        return v;
    }

    // Checks validity of data and existance of such edges and creates and edge if
    // all data is valid
    public void addEdge(T valueOrigin, T valueDest, int weight) {
        // Check for duplicate edge or invalid weight (under zero)
        if (findEdge(valueOrigin, valueDest) != null || weight < 0)
            return;

        Vertex<T> origin = addVertex(valueOrigin);

        Vertex<T> dest = addVertex(valueDest);

        // Create a new edge
        Edge<T> newEdge = new Edge<T>(origin, dest, weight);

        origin.addOutgoing(dest); // add destination vertex to origin's outgoing connections list
        dest.addIncoming(origin); // add origin vertex to destinations's incoming connections list
        edges.add(newEdge); // add new edge to graph's list
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        for (Vertex<T> v : vertices) {
            s.append(v);
        }
        return s.toString();
    }
}
