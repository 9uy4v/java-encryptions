package gould;

public class GouldSequence {
    public static void main(String[] args) {
        int terms = 20;
        for (int n = 0; n < terms; n++) {
            System.out.print(countOddsInRow(n) + " ");
        }
    }

    private static int countOddsInRow(int n) {
        int count = 1;
        while (n > 0) {
            if ((n & 1) == 1) {
                count *= 2;
            }
            n >>= 1;
        }
        return count;
    }
}
