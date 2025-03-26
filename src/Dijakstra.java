import models.*;

import java.util.PriorityQueue;

class MainDijkstra<T> {
    public void Dijkstra(Graph<T> graph, Vertex<T> sourceVertex) {
        PriorityQueue<Vertex<T>> pq = new PriorityQueue<Vertex<T>>();

        for (Vertex<T> vertex : graph.getVertices()) {
            if (vertex.equals(sourceVertex)) {
                // TODO : impelemnt equals
            }

            vertex.setMinDist(Integer.MAX_VALUE);
            vertex.setPrev(null);
            pq.add(vertex);
        }

    }
}