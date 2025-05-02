package gould;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

public class GouldEncryption {
    public static void main(String[] args) {

        File f = new File("assets\\test.png");
        System.err.println(generateKeyByFile(f));
    }

    public static void encrypt(File f) {
        System.out.println("gould encryption");
        String oKey = generateKeyByFile(f);
        // TODO : encrypt using key
    }

    public static void decrypt(File f) {
        System.out.println("gould decryption");
        // TODO : decrypt
    }

    private static String generateKeyByFile(File f) {
        int bufferSize = (int) (f.length() / 4);

        int[] chunkBuffer = new int[bufferSize];
        int[] sequence = getSequence(bufferSize);

        try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
            // Move 4 bytes at a time to the buffer
            int index = 0;

            while (dis.available() >= 4) {
                chunkBuffer[index++] = dis.readInt();
            }
        } catch (Exception e) {
            System.out.println(e);

            return null;
        }

        for (int i = 0; i < bufferSize; i++) {
            chunkBuffer[i] ^= sequence[i];
        }

        return foldToString(chunkBuffer, 8);
    }

    private static String foldToString(int[] arr, int size) {
        StringBuilder foldedString = new StringBuilder();
        int[] buffer = new int[size];
        Arrays.fill(buffer, 0);

        int i = 0;

        while (i < arr.length) {
            for (int j = 0; j < buffer.length && i < arr.length; i++, j++) {
                buffer[j] ^= arr[i];
            }
        }

        for (int fChunk : buffer) {
            foldedString.append(fChunk & 0x7FFFFFFF); // & removes any negative values
        }

        return foldedString.toString();
    }

    private static int[] getSequence(int t) {
        int[] sequence = new int[t];

        for (int i = 0; i < t; i++) {
            int count = 1, n = i;

            while (n > 0) {
                if ((n & 1) == 1) {
                    count *= 2;
                }
                n >>= 1;
            }
            sequence[i] = count;
        }

        return sequence;
    }
}
