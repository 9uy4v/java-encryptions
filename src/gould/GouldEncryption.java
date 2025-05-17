package gould;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import javax.print.DocFlavor.STRING;

public class GouldEncryption {

    private static final int CHUNKS = 100;

    public static void main(String[] args) {

        File f = new File("assets\\test.png");
        System.err.println(generateKeyByFile(f));
    }

    public static void encrypt(File f) {
        byte[] data;

        try {
            data = Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            System.out.println("Error reading file : " + e);
            return;
        }

        String eKey = generateKeyByFile(f);
        int chunkSize = (int) Math.ceil((double) data.length / CHUNKS);
        byte[][] chunks = new byte[CHUNKS][chunkSize];

        // חלוקה לחלקים (החלק האחרון יכול להיות קצר יותר)
        for (int i = 0; i < CHUNKS; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, data.length);
            int length = end - start;
            if (length <= 0)
                break;
            System.arraycopy(data, start, chunks[i], 0, length);
        }

        byte[][] encryptedChunks = new byte[CHUNKS][chunkSize];

        for (int i = 0; i < CHUNKS; i++) {
            // קבלת אינדקס מה-key לפי 3 ספרות רצופות, מחזירה אינדקס בין 0 ל-99 (ל-100 חלקים)
            int keyIndex = getKeyIndex(i, eKey);

            // XOR בין החלק הנוכחי לחלק באינדקס keyIndex
            encryptedChunks[i] = xorChunks(chunks[i], chunks[keyIndex]);
        }

        // איחוד כל החלקים המוצפנים למערך בייטים אחד
        int totalLength = data.length;
        byte[] encryptedData = new byte[totalLength];
        int pos = 0;
        for (int i = 0; i < CHUNKS; i++) {
            int copyLength = Math.min(chunkSize, totalLength - pos);
            System.arraycopy(encryptedChunks[i], 0, encryptedData, pos, copyLength);
            pos += copyLength;
        }

    }

    private static int getKeyIndex(int chunkNumber, String key) {
        int start = (chunkNumber * 3) % (key.length() - 2);
        String part = key.substring(start, start + 3);
        int idx = Integer.parseInt(part) % CHUNKS; // להבטיח אינדקס בין 0 ל-99
        return idx;
    }

    private static byte[] xorChunks(byte[] a, byte[] b) {
        int length = Math.min(a.length, b.length);
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
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

        return foldToString(chunkBuffer, 32);
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
