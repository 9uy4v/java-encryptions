package dijkstra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.PriorityQueue;
import java.util.Arrays;
import java.util.List;

import dijkstra.models.*;

public class DijkstraEncryption {
    public static void main(String[] args) {
        File f = new File("assets\\test.png");
        System.err.println(generateKeyByFile(f));
    }

    public static boolean encrypt(File f) {
        System.out.println("dijakstra encryption");

        String oKey = generateKeyByFile(f);
        byte[] shifting = new byte[64];

        byte[] file;

        try {
            file = Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            System.out.println("Error reading file : " + e);
            return false;
        }

        for (int i = 0; i < shifting.length; i++) {
            shifting[i] = (byte) Integer.parseInt(oKey.substring(i, i + 1), 16);
        }

        for (int i = 0; i < file.length; i++) {
            byte curByte = file[i];

            file[i] ^= shifting[i % 64];
            shifting[i % 64] += curByte;
        }

        File encryptedFile = new File(f.getParentFile(), "Encrypted" + f.getName());

        try (FileOutputStream fos = new FileOutputStream(encryptedFile)) {
            fos.write(oKey.getBytes(StandardCharsets.UTF_8));

            fos.write((byte) '\n');

            fos.write(file);
        } catch (Exception e) {
            System.out.println("Error while writing encrypted file : " + e);
            return false;
        }

        return true;
    }

    public static boolean decrypt(File f) {
        System.out.println("dijkstra decryption");

        byte[] file;
        try {
            file = Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            System.out.println("Error reading file : " + e);
            return false;
        }

        if (file[64] != '\n') {
            System.out.println("Invalid encrypted file format");
            return false;
        }

        String oKey = new String(file, 0, 64, StandardCharsets.UTF_8);
        byte[] shifting = new byte[64];

        for (int i = 0; i < shifting.length; i++) {
            shifting[i] = (byte) Integer.parseInt(oKey.substring(i, i + 1), 16);
        }

        byte[] fileData = Arrays.copyOfRange(file, 64 + 1, file.length);

        for (int i = 0; i < fileData.length; i++) {
            fileData[i] ^= shifting[i % 64];
            shifting[i % 64] += fileData[i];
        }

        File decryptedFile = new File(f.getParentFile(), "Decrypted" + f.getName());

        try (FileOutputStream fos = new FileOutputStream(decryptedFile)) {
            fos.write(fileData);
        } catch (Exception e) {
            System.out.println("Error while writing decrypted file : " + e);
        }

        return true;
    }

    private static String generateKeyByFile(File f) {
        StringBuilder key = new StringBuilder();
        Graph<Integer> g = generateGraphByFile(f);
        System.out.println("Running Dijkstra");
        dijkstra(g, g.getVertices().getFirst());
        System.out.println("Dijkstra Finished!");

        PriorityQueue<Vertex<Integer>> pq = new PriorityQueue<Vertex<Integer>>();

        System.out.println("Sorting results...");
        for (Vertex<Integer> vertex : g.getVertices()) {
            pq.add(vertex);
        }

        System.out.println("Xor Folding the Key");
        Integer[] keyBuffer = new Integer[8];
        Arrays.fill(keyBuffer, 0);

        while (!pq.isEmpty()) {
            for (int i = 0; i < 8 && !pq.isEmpty(); i++) {
                keyBuffer[i] ^= pq.poll().getValue();
            }
        }

        for (int i = 0; i < 8; i++) {
            key.append(String.format("%08x", keyBuffer[i]));
        }

        System.out.println(("Key Generation Complete!"));
        return key.toString();
    }

    private static Graph<Integer> generateGraphByFile(File f) {
        System.out.println("Generating Graph by File:");

        int bufferSize = (int) (f.length() / 4); // Each chunk is 32bit (4 bytes)
        Integer[] chunkBuffer = new Integer[bufferSize];
        Graph<Integer> g = new Graph<Integer>();

        System.out.println("\tLoading Data Chunks from File...");

        try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
            // Move 4 bytes at a time to the buffer
            int index = 0;
            while (dis.available() >= 4) {
                chunkBuffer[index++] = dis.readInt();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        Integer[] foldedChunks = foldChunks(chunkBuffer, 8000);

        System.out.println("\tConnecting Vertices...");
        System.out.println();

        int i = 1;

        for (int chunk : foldedChunks) {
            System.out.print("\033[1A\033[2K"); // Move up 1 line and clear it
            System.out.printf("\t\tChunk (%d/%d)\n", i, foldedChunks.length);
            List<Vertex<Integer>> existingVertices = g.getVertices();
            short newLsb16 = (short) chunk; // the 16 less significant bits
            short newMsb16 = (short) (chunk >>> 16); // the 16 most significant bits

            if (existingVertices.isEmpty()) {
                g.addVertex(chunk); // Adds first vertex
                continue;
            }

            Vertex<Integer> lastVertex = existingVertices.getLast();

            // Create an edge originating from the last added vertex to the newly added
            // vertex (to keep the graph connected between all of it's vertices)
            g.addEdge(lastVertex.getValue(), chunk, newMsb16 ^ (lastVertex.getValue() >>> 16));

            for (Vertex<Integer> vertex : existingVertices) {
                int vertexValue = vertex.getValue();
                short curLsb16 = (short) vertexValue; // the 16 less significant bits
                short curMsb16 = (short) (vertexValue >>> 16); // the 16 most significant bits

                // if the bottom half of the value
                if (curLsb16 == newLsb16) {
                    int weight = newMsb16 ^ curMsb16;
                    if (curMsb16 < newMsb16) {
                        g.addEdge(chunk, vertexValue, weight);
                    } else if (curMsb16 > newMsb16) {
                        g.addEdge(vertexValue, chunk, weight);
                    }
                    // else they are the same and we don't want self connections
                }
            }
            i++;
        }

        System.out.println("Graph Building Complete!");

        return g;
    }

    private static Integer[] foldChunks(Integer[] arr, int size) {
        System.out.println("\tXor Folding the File...");
        Integer[] chunkBuffer = new Integer[size];
        Arrays.fill(chunkBuffer, 0);

        int i = 0;

        while (i < arr.length) {
            for (int j = 0; j < chunkBuffer.length && i < arr.length; i++, j++) {
                chunkBuffer[j] ^= arr[i];
            }
        }

        return chunkBuffer;
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
/// VERTEX : each vertex will contain the content of its designated 32bit
/// chunk
///
/// EDGE : connection between chunks will be to the next chunk and to vertices
/// with same reminder after division by a certain number
/// suggestion: if 16 MSB bits are the same
///
/// WEIGHT : will be xor of the origin and destination chunk's 16 LSB
///
/// 2. GET ENCRYPTION KEY BY GRAPH
/// use dijkstra's algorithm on graph and get a key by the order of the
/// vertices.
///
/// 3. USE KEY TO ENCRYPT FILE
///
/// 4. GET KEY BY ENCRYPTED FILE
/// repeat steps 1 and 2 but on the encrypted file.
/// then xor between the decrypted file key and the encrypted file key to get a
/// final key
///
/// 5. CONSTRUCT FINAL ENCRYPTED FILE
/// add the key and file together (maybe in a more complex way than concat)
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