import models.*;

import java.util.PriorityQueue;

class MainDijkstra {
    public static void main(String[] args) {
        Graph<String> g = new Graph<String>();
        // Create Graph here

        MainDijkstra.Dijkstra(g, g.getVertices().get(0));

        System.out.println(g);
    }

    public static <T> void Dijkstra(Graph<T> graph, Vertex<T> sourceVertex) {
        // Minimal heap storing to be visited vertices ordered by minimal known reach
        // distance
        PriorityQueue<Vertex<T>> pq = new PriorityQueue<Vertex<T>>();

        // Adding to priority queue all of the graph's vertices
        for (Vertex<T> vertex : graph.getVertices()) {
            // source vertex gets a distance of 0, others get max value
            if (vertex.equals(sourceVertex))
                vertex.setMinDist(0);
            else
                vertex.setMinDist(Integer.MAX_VALUE);

            vertex.setPrev(null);
            pq.add(vertex);
        }

        while (!pq.isEmpty()) {
            Vertex<T> currentVertex = pq.poll(); // Get Vertex with minimal known reach distance

            // Passing over outgoing-connection's destination vertices that weren't visited
            for (Vertex<T> outgoingVertex : currentVertex.getOutgoing()) {
                if (pq.contains(outgoingVertex)) {
                    // Get relevant connecting edge
                    Edge<T> connectingEdge = graph.findEdge(currentVertex.getValue(), outgoingVertex.getValue());

                    // The destination vertex's distance from the source via the current vertex
                    int altOutgoingDist = currentVertex.getMinDist() + connectingEdge.getWeight();

                    // If the alt distance is smaller than the minimal distance to reach the
                    // destination node update the vertex and the priority queue
                    if (altOutgoingDist < outgoingVertex.getMinDist()) {
                        pq.remove(outgoingVertex);
                        outgoingVertex.setMinDist(altOutgoingDist);
                        outgoingVertex.setPrev(currentVertex);
                        pq.add(outgoingVertex);
                    }
                }
            }
        }

    }
}