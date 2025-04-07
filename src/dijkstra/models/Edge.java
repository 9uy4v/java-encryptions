package dijkstra.models;

public class Edge<T> {
    private int weight;
    private Vertex<T> origin;
    private Vertex<T> dest;

    public Edge(Vertex<T> origin, Vertex<T> dest, int weight) {
        this.origin = origin;
        this.dest = dest;
        this.weight = weight;
    }

    public Vertex<T> getOrigin() {
        return origin;
    }

    public void setOrigin(Vertex<T> origin) {
        this.origin = origin;
    }

    public Vertex<T> getDest() {
        return dest;
    }

    public void setDest(Vertex<T> dest) {
        this.dest = dest;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("Origin: " + origin.getValue());
        s.append(" Dest: " + dest.getValue());
        s.append(" Weight: " + weight);

        return s.toString();
    }
}
