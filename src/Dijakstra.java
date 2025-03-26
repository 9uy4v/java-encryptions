import models.*;

import java.util.PriorityQueue;

class MainDijkstra<T> {
    public void Dijkstra(Graph<T> graph, Vertex<T> sourceVertex) {
        PriorityQueue<Vertex<T>> pq = new PriorityQueue<Vertex<T>>();

        for (Vertex<T> vertex : graph.getVertices()) {
            if (vertex.equals(sourceVertex))
                vertex.setMinDist(0);
            else
                vertex.setMinDist(Integer.MAX_VALUE);

            vertex.setPrev(null);
            pq.add(vertex);
        }

        while (!pq.isEmpty()) {
            Vertex<T> currentVertex = pq.poll();

            for (Vertex<T> outgoingVertex : currentVertex.getOutgoing()) {
                if (pq.contains(outgoingVertex)) {
                    Edge<T> connectingEdge = graph.findEdge(currentVertex.getValue(), outgoingVertex.getValue());

                    int currentWeight = currentVertex.getMinDist() + connectingEdge.getWeight();

                    if (currentWeight < outgoingVertex.getMinDist()) {
                        pq.remove(outgoingVertex);
                        outgoingVertex.setMinDist(currentWeight);
                        outgoingVertex.setPrev(currentVertex);
                        pq.add(outgoingVertex);
                    }
                }
            }
        }

    }
}