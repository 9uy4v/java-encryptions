import models.*;

import java.io.File;
import java.util.PriorityQueue;

class DijkstraEncryption {
    public static void main(String[] args) {
        Graph<String> g = new Graph<String>();
        // Create Graph here

        DijkstraEncryption.dijkstra(g, g.getVertices().get(0));

        System.out.println(g);
    }

    public static String keyByFile(File f) {
        return null;
    }

    public static void generateGraphByFile(File f) {
        byte[] chunks = new byte[(int) (f.getTotalSpace() / 256) + 1];

        for (byte b : chunks) {

        }
    }

    private static <T> void dijkstra(Graph<T> graph, Vertex<T> sourceVertex) {
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

/// ENCRYPTION LOGIC
/// 1. BUILD GRAPH BY FILE
///
/// VERTEX : each vertex will contain the content of its designaetd 128bits
/// chunk
///
/// EDGE : connection between chunks will be to the next chunk and to vertices
/// with same reminder after division by a certain number
/// (TODO : decide on number - deriven? salt?)
/// suggestion: if 32 MSB bits are the same
///
/// WEIGHT : will be xor of the origin and destination chunk's 16 LSB
///
/// 2. GET ENCRYPTION KEY BY GRAPH
/// use dijkstra's algorithm on graph and get a key by the order of the
/// vertices.
/// TODO : KEY NEEDS TO BE A SET SIZE
///
/// 3. USE KEY TO ENCRYPT FILE
/// TODO : think of encryption logic
///
/// 4. GET KEY BY ENCRYPTED FILE
/// repeat steps 1 and 2 but on the encrypted file.
/// then xor between the decrypted file key and the encrypted file key to get a
/// final key
///
/// 5. CONSTRUCT FINAL ENCRYPTED FILE
/// add the key and file together (maybe in a more complex way than concat)
/// TODO : order final key and encrypted file
///
/// DECRYPTION LOGIC
/// 1. GET KEY AND FILE
/// seperate the given key and the encrypted file bytes
///
/// 2. GET KEY BY ENCRYPTED FILE
/// run the file through the graph building and dijkstra to get the second key
/// generated in the encryption process
/// then xor the result (key from encrypted file) and the given key to get the
/// key from the decrypted file
///
/// 3. DECRYPT USING KEY
/// using the result of the last step we have hte key from the decrypted file
/// and we can now decrypt the file